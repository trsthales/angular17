import { Component, inject, OnDestroy, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TokenService } from './token.service';
import { ToastService } from './toast.service';

@Component({
  selector: 'app-token-expiry',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div *ngIf="tokenSvc.getToken()" style="display:flex;align-items:center;gap:.5rem;align-items:center">
      <ng-container *ngIf="tokenSvc.refreshing(); else normalState">
        <div class="spinner" aria-hidden="true"></div>
        <span style="color:var(--muted); font-size:.9rem">Renovando…</span>
      </ng-container>
      <ng-template #normalState>
        <span *ngIf="!expired" style="color:#666; font-size:.9rem">Expira em {{ remainingLabel }}</span>
        <span *ngIf="expired" class="expired-badge" style="color:#fff; background:#d9534f; padding:.2rem .4rem; border-radius:4px; font-size:.85rem">Token expirado</span>
        <button *ngIf="expired" (click)="refresh()" style="margin-left:.25rem">Gerar novo</button>
      </ng-template>
      <!-- progress bar (percentage of TTL remaining) -->
      <div *ngIf="!tokenSvc.refreshing() && ttlPercent !== null" style="width:160px; height:6px; background:#eee; border-radius:6px; overflow:hidden; margin-left:.5rem">
        <div [style.width.%]="ttlPercent" style="height:100%; background:linear-gradient(90deg,var(--primary),var(--accent)); transition:width 600ms linear"></div>
      </div>
    </div>
  `
})
export class TokenExpiryComponent implements OnDestroy {
  tokenSvc = inject(TokenService);
  toast = inject(ToastService);
  now = signal<number>(Date.now());
  private prevExpired = this.tokenSvc.isExpired();
  private timer = setInterval(() => {
    this.now.set(Date.now());
    const cur = this.tokenSvc.isExpired();
    if (!this.prevExpired && cur) {
      this.toast.showError('Token expirado');
    }
    this.prevExpired = cur;
  }, 1000);

  get expired(): boolean {
    return this.tokenSvc.isExpired();
  }

  get remainingSeconds(): number {
    const exp = this.tokenSvc.getExpiresAt();
    if (!exp) return 0;
    const rem = Math.max(0, Math.floor(exp - (this.now() / 1000)));
    return rem;
  }

  get remainingLabel(): string {
    const s = this.remainingSeconds;
    if (s <= 0) return 'agora';
    const m = Math.floor(s / 60);
    const sec = s % 60;
    if (m > 0) return `${m}m ${sec}s`;
    return `${sec}s`;
  }

  get ttlPercent(): number | null {
    const ttl = this.tokenSvc.getTtlSeconds();
    const rem = this.remainingSeconds;
    if (!ttl || ttl <= 0) return null;
    const p = Math.max(0, Math.min(100, Math.round((rem / ttl) * 100)));
    return p;
  }

  async refresh() {
    const t = await this.tokenSvc.requestDevToken();
    if (t) this.toast.showSuccess('Token renovado');
    else this.toast.showError('Falha ao renovar token');
  }

  ngOnDestroy(): void {
    clearInterval(this.timer);
  }
}
