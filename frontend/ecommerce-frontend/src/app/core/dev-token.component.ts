import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TokenService } from './token.service';
import { UserIdService } from './user-id.service';

@Component({
  selector: 'app-dev-token',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div style="display:flex;align-items:center;gap:.5rem">
      <input [(ngModel)]="sub" placeholder="sub (user)" style="width:12rem" />
      <button (click)="generate()">Gerar token dev</button>
      <button (click)="clear()" *ngIf="token">Limpar</button>
    </div>
    <div *ngIf="token" style="margin-top:.4rem; max-width:34rem; word-break:break-all; font-size:.85rem">
      <div><strong>Token atual (dev):</strong></div>
      <div style="background:#f6f6f6;padding:.4rem;border-radius:4px">{{ maskedToken() }}</div>
      <div style="margin-top:.25rem"><button (click)="copy()">Copiar</button></div>
    </div>
  `
})
export class DevTokenComponent {
  private tokenSvc = inject(TokenService);
  private userSvc = inject(UserIdService);

  sub = this.userSvc.userId() ?? 'local-user';

  get token() { return this.tokenSvc.getToken(); }

  async generate() {
    const t = await this.tokenSvc.requestDevToken(this.sub || 'local-user');
    if (!t) alert('Falha ao gerar token de desenvolvimento. Verifique o backend.');
  }

  clear() { this.tokenSvc.setToken(null); }

  maskedToken() {
    const t = this.token;
    if (!t) return '';
    if (t.length < 20) return t;
    return t.slice(0, 12) + '…' + t.slice(-8);
  }

  async copy() {
    try {
      await navigator.clipboard.writeText(this.token ?? '');
      alert('Token copiado para a área de transferência');
    } catch (e) {
      console.error(e);
      alert('Não foi possível copiar o token');
    }
  }
}
