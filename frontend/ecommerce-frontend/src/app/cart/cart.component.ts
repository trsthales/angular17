import { Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartStore } from '../core/cart.store';
import { UserIdService } from '../core/user-id.service';

/**
 * Componente standalone para gerenciar o carrinho do usuário.
 * Mostra itens, permite atualizar quantidade e remover itens.
 */
@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section class="cart">
      <h2>Seu Carrinho</h2>

      <div *ngIf="store.loading()">Carregando...</div>

      <div *ngIf="!store.loading() && items().length === 0">Carrinho vazio</div>

      <ul *ngIf="items().length > 0">
        <li *ngFor="let item of items()" class="cart-item">
          <div class="info">
            <div class="name">{{ item.productName }}</div>
            <div class="price">R$ {{ item.unitPrice.toFixed(2) }}</div>
          </div>
          <div class="actions">
            <button (click)="decrease(item)">-</button>
            <span class="qty">{{ item.quantity }}</span>
            <button (click)="increase(item)">+</button>
            <button class="remove" (click)="remove(item)">Remover</button>
          </div>
        </li>
      </ul>

      <div class="total">Total: R$ {{ total().toFixed(2) }}</div>

      <div style="text-align:right; margin-top:1rem">
        <button (click)="checkout()" style="padding:0.5rem 1rem; font-weight:600">Finalizar Compra</button>
      </div>
    </section>
  `,
  styles: [
    `
    .cart { padding: 1rem; max-width:800px; margin: 0 auto; font-family: Arial, sans-serif }
    .cart h2 { margin-top: 0 }
    .cart-item { display:flex; align-items:center; justify-content:space-between; padding:0.75rem 0; border-bottom:1px solid #eee }
    .info { display:flex; gap:1rem; align-items:center }
    .name { font-weight: 600 }
    .price { color: #666 }
    .actions { display:flex; gap:0.5rem; align-items:center }
    .actions button { padding: 0.25rem 0.5rem; border:1px solid #ccc; background:#fff; cursor:pointer; border-radius:4px }
    .actions button:hover { background:#f4f4f4 }
    .remove { color: #b00; border-color: #f2c6c6 }
    .qty { min-width: 2rem; text-align:center }
    .total { margin-top: 1rem; font-weight: bold; text-align:right }
    @media (max-width:600px) {
      .cart-item { flex-direction: column; align-items:flex-start }
      .actions { margin-top: 0.5rem }
    }
  `,
  ],
})
export class CartComponent {
  readonly store = inject(CartStore);
  readonly user = inject(UserIdService);

  readonly items = computed(() => this.store.items());
  readonly total = computed(() => this.store.total());

  private getUserId(): string {
    const id = this.user.userId();
    // fallback para desenvolvimento se não houver userId
    return id ?? '00000000-0000-0000-0000-000000000001';
  }

  increase(item: { productId: string; quantity: number }) {
    const userId = this.getUserId();
    this.store.update(item.productId, item.quantity + 1, userId);
  }

  decrease(item: { productId: string; quantity: number }) {
    const userId = this.getUserId();
    const next = Math.max(0, item.quantity - 1);
    this.store.update(item.productId, next, userId);
  }

  remove(item: { productId: string }) {
    const userId = this.getUserId();
    this.store.remove(item.productId, userId);
  }

  checkout() {
    const userId = this.getUserId();
    this.store.checkout(userId);
  }
}
