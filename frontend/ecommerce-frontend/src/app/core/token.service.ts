import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';
import { ToastService } from './toast.service';

/**
 * Serviço simples para gerenciar token de autenticação no frontend.
 * - Para desenvolvimento armazenamos no localStorage (simples).
 * - Para produção prefira cookies HttpOnly setados pelo backend.
 */
@Injectable({ providedIn: 'root' })
export class TokenService {
  private readonly key = 'authToken';
  readonly token = signal<string | null>(localStorage.getItem('authToken'));
  // `expiresAt` armazena o claim `exp` (em segundos desde epoch) quando disponível
  readonly expiresAt = signal<number | null>(null);
  readonly issuedAt = signal<number | null>(null);

  readonly refreshing = signal(false);

  private refreshTimer: ReturnType<typeof setTimeout> | null = null;
  private paused = false;
  private retryCount = 0;
  private readonly maxRetries = 5;
  private readonly baseRetryMs = 2000; // 2s

  constructor(private http: HttpClient, private toast: ToastService) {
    // Pause/resume when page visibility changes
    if (typeof document !== 'undefined' && 'addEventListener' in document) {
      document.addEventListener('visibilitychange', () => {
        if (document.hidden) {
          this.paused = true;
          this.clearRefreshTimer();
        } else {
          this.paused = false;
          // reschedule based on current exp
          this.scheduleRefresh();
        }
      });
    }
  }

  setToken(t: string | null) {
    if (t) {
      localStorage.setItem(this.key, t);
      this.token.set(t);
      const exp = this.parseExpFromJwt(t);
      const iat = this.parseIatFromJwt(t);
      this.expiresAt.set(exp);
      this.issuedAt.set(iat);
      // schedule auto-refresh 30s before expiration
      this.scheduleRefresh();
    } else {
      localStorage.removeItem(this.key);
      this.token.set(null);
      this.expiresAt.set(null);
      this.issuedAt.set(null);
      this.clearRefreshTimer();
    }
  }

  getToken(): string | null { return this.token(); }

  /** Retorna o `exp` (em segundos) se presente no token, ou null. */
  getExpiresAt(): number | null { return this.expiresAt(); }

  getIssuedAt(): number | null { return this.issuedAt(); }

  /** Retorna a duração (em segundos) do token se `iat` e `exp` estiverem presentes */
  getTtlSeconds(): number | null {
    const exp = this.expiresAt();
    const iat = this.issuedAt();
    if (!exp || !iat) return null;
    return Math.max(0, Math.floor(exp - iat));
  }

  /** Retorna true se o token estiver expirado (com base no claim `exp`). */
  isExpired(): boolean {
    const exp = this.expiresAt();
    if (!exp) return false;
    return Date.now() / 1000 >= exp;
  }

  /**
   * Gera token via endpoint de desenvolvimento `/dev/token` quando disponível.
   * Retorna o token em um Promise.
   */
  async requestDevToken(sub = 'local-user') {
    try {
      const res = await this.http.post<{ token: string }>('/dev/token', { sub }).toPromise();
      const t = res?.token ?? null;
      if (t) this.setToken(t);
      return t;
    } catch (e) {
      console.error('Falha ao solicitar token dev', e);
      return null;
    }
  }

  private scheduleRefresh() {
    this.clearRefreshTimer();
    if (this.paused) return;
    const exp = this.expiresAt();
    if (!exp) return;
    const msUntilExpiry = exp * 1000 - Date.now();
    const msBeforeRefresh = 30_000; // 30 seconds
    const msUntilRefresh = msUntilExpiry - msBeforeRefresh;

    const doRefresh = async () => {
      // reset retry count for this attempt
      this.retryCount = 0;
      await this.attemptRefreshWithRetry();
    };

    if (msUntilRefresh <= 0) {
      // token is close to expiry - refresh in next tick
      this.refreshTimer = setTimeout(doRefresh, 50);
    } else {
      this.refreshTimer = setTimeout(doRefresh, msUntilRefresh);
    }
  }

  private clearRefreshTimer() {
    if (this.refreshTimer) {
      clearTimeout(this.refreshTimer);
      this.refreshTimer = null;
    }
  }

  private parseSubFromJwt(token?: string | null): string | null {
    if (!token) return null;
    try {
      const parts = token.split('.');
      if (parts.length < 2) return null;
      const payload = parts[1];
      const b64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      const json = decodeURIComponent(atob(b64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      const obj = JSON.parse(json);
      if (obj && typeof obj.sub === 'string') return obj.sub;
      return null;
    } catch (e) {
      return null;
    }
  }

  private parseIatFromJwt(token?: string | null): number | null {
    if (!token) return null;
    try {
      const parts = token.split('.');
      if (parts.length < 2) return null;
      const payload = parts[1];
      const b64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      const json = decodeURIComponent(atob(b64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      const obj = JSON.parse(json);
      if (obj && typeof obj.iat === 'number') return obj.iat;
      return null;
    } catch (e) {
      return null;
    }
  }

  private parseExpFromJwt(token: string): number | null {
    try {
      const parts = token.split('.');
      if (parts.length < 2) return null;
      const payload = parts[1];
      // base64url -> base64
      const b64 = payload.replace(/-/g, '+').replace(/_/g, '/');
      const json = decodeURIComponent(atob(b64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      const obj = JSON.parse(json);
      if (obj && typeof obj.exp === 'number') return obj.exp;
      return null;
    } catch (e) {
      console.warn('Não foi possível decodificar exp do token', e);
      return null;
    }
  }

  private async attemptRefreshWithRetry(): Promise<void> {
    if (this.refreshing()) return;
    this.refreshing.set(true);
    const sub = this.parseSubFromJwt(this.token() ?? undefined) ?? 'local-user';

    while (this.retryCount <= this.maxRetries) {
      try {
        const t = await this.requestDevToken(sub);
        if (t) {
          this.toast.showSuccess('Token renovado automaticamente');
          this.refreshing.set(false);
          // schedule next refresh based on new token
          this.scheduleRefresh();
          return;
        } else {
          throw new Error('Sem token retornado');
        }
      } catch (err) {
        this.retryCount++;
        const backoff = this.baseRetryMs * Math.pow(2, this.retryCount - 1);
        const jitter = Math.floor(Math.random() * 500);
        const waitMs = Math.min(backoff + jitter, 60_000);
        this.toast.showError(`Falha ao renovar token (tentativa ${this.retryCount}/${this.maxRetries})`);
        if (this.retryCount > this.maxRetries) break;
        await new Promise(res => setTimeout(res, waitMs));
      }
    }

    this.refreshing.set(false);
    this.toast.showError('Não foi possível renovar token automaticamente após várias tentativas');
  }
}
