import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from './toast.service';

@Component({
  selector: 'app-toasts',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toasts" aria-live="polite">
      <div *ngFor="let m of svc.messages()" class="toast" [attr.data-type]="m.type">
        {{ m.text }}
      </div>
    </div>
  `,
  styles: [
    `.toasts { position: fixed; top: 1rem; right: 1rem; display:flex; flex-direction:column; gap:0.5rem; z-index:9999 }
     .toast { background: #222; color: white; padding: .5rem .75rem; border-radius:6px; box-shadow:0 6px 16px rgba(0,0,0,0.2);}
     .toast[data-type="success"]{ background: #1f8f3b } .toast[data-type="error"]{ background:#9b1f1f }
  `]
})
export class ToastsComponent {
  svc = inject(ToastService);
}
