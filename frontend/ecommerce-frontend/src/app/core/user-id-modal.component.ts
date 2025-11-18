import { Component, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserIdService } from './user-id.service';

@Component({
  selector: 'app-user-id-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="modal-backdrop" *ngIf="svc.modalVisible()" (click)="svc.closeModal()"></div>
    <div class="modal" *ngIf="svc.modalVisible()">
      <h3>Configurar userId (desenv)</h3>
      <p>Insira um UUID para simular o usuário nas requisições.</p>
      <input [(ngModel)]="value" placeholder="userId (UUID)" />
      <div style="margin-top:.5rem">
        <button (click)="save()">Salvar</button>
        <button (click)="svc.closeModal()">Cancelar</button>
      </div>
    </div>
  `,
  styles: [
    `.modal-backdrop { position: fixed; inset: 0; background:#00000066;} .modal { position: fixed; top:20%; left:50%; transform:translateX(-50%); background:white; padding:1rem; border-radius:6px; box-shadow:0 6px 24px rgba(0,0,0,0.2);}`
  ]
})
export class UserIdModalComponent {
  svc = inject(UserIdService);
  value = this.svc.userId() ?? '';

  save() {
    this.svc.setUserId(this.value || null);
    this.svc.closeModal();
  }
}
