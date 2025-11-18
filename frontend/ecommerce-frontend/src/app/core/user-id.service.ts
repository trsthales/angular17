import { inject, Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class UserIdService {
  private readonly _userId = signal<string | null>(null);
  readonly userId = this._userId;

  private readonly _modalVisible = signal(false);
  readonly modalVisible = this._modalVisible;

  constructor() {
    const stored = localStorage.getItem('userId');
    if (stored) this._userId.set(stored);
  }

  setUserId(id: string | null) {
    if (id && id.trim().length > 0) {
      localStorage.setItem('userId', id);
      this._userId.set(id);
    } else {
      localStorage.removeItem('userId');
      this._userId.set(null);
    }
  }

  openModal() { this._modalVisible.set(true); }
  closeModal() { this._modalVisible.set(false); }
}
