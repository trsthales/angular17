import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastService } from './toast.service';
import { trigger, transition, style, animate } from '@angular/animations';

@Component({
  selector: 'app-toasts',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="toasts" aria-live="polite">
      <div *ngFor="let m of svc.messages()" class="toast" [attr.data-type]="m.type" [@toastAnim]>
        <div class="toast-body">{{ m.text }}</div>
        <div class="progress" [style.animationDuration]="(m.ttl ?? 3000) + 'ms'"></div>
      </div>
    </div>
  `,
  animations: [
    trigger('toastAnim', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-8px) scale(0.98)' }),
        animate('180ms ease-out', style({ opacity: 1, transform: 'translateY(0) scale(1)' }))
      ]),
      transition(':leave', [
        animate('160ms ease-in', style({ opacity: 0, transform: 'translateY(-6px) scale(0.98)' }))
      ])
    ])
  ],
    styles: [
    `
      .toasts { position: fixed; top: 1rem; right: 1rem; display:flex; flex-direction:column; gap:0.5rem; z-index:9999 }
      .toast { background: #222; color: white; padding: .5rem .75rem; border-radius:6px; box-shadow:0 6px 16px rgba(0,0,0,0.2); transform-origin: top right; position:relative; overflow:hidden }
      .toast-body{ position:relative; z-index:2 }
      .toast .progress{ position:absolute; left:0; right:0; bottom:0; height:3px; background: rgba(255,255,255,0.3); transform-origin:left; animation-name: progressBar; animation-timing-function: linear; z-index:1 }
      @keyframes progressBar{ from{ width:100% } to{ width:0% } }
      .toast[data-type="success"]{ background: #1f8f3b } .toast[data-type="error"]{ background:#9b1f1f }
    `]
})
export class ToastsComponent {
  svc = inject(ToastService);
}
