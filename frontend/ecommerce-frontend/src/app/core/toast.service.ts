import { Injectable, signal } from '@angular/core';

export type ToastMessage = { id: number; text: string; type?: 'info' | 'success' | 'error' };

@Injectable({ providedIn: 'root' })
export class ToastService {
  private idSeq = 1;
  private readonly _messages = signal<ToastMessage[]>([]);
  readonly messages = this._messages;

  show(text: string, type: ToastMessage['type'] = 'info', ttl = 3000) {
    const id = this.idSeq++;
    const msg: ToastMessage = { id, text, type };
    this._messages.set([...this._messages(), msg]);
    setTimeout(() => this.remove(id), ttl);
  }

  showSuccess(text: string, ttl = 3000) { this.show(text, 'success', ttl); }
  showError(text: string, ttl = 5000) { this.show(text, 'error', ttl); }

  remove(id: number) { this._messages.set(this._messages().filter(m => m.id !== id)); }
}
