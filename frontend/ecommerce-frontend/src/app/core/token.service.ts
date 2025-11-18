import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';

/**
 * Serviço simples para gerenciar token de autenticação no frontend.
 * - Para desenvolvimento armazenamos no localStorage (simples).
 * - Para produção prefira cookies HttpOnly setados pelo backend.
 */
@Injectable({ providedIn: 'root' })
export class TokenService {
  private readonly key = 'authToken';
  readonly token = signal<string | null>(localStorage.getItem('authToken'));

  constructor(private http: HttpClient) {}

  setToken(t: string | null) {
    if (t) {
      localStorage.setItem(this.key, t);
      this.token.set(t);
    } else {
      localStorage.removeItem(this.key);
      this.token.set(null);
    }
  }

  getToken(): string | null { return this.token(); }

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
}
