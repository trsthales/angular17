import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CheckoutComponent } from '../checkout/checkout.component';
import { ProductsService, Product } from './products.service';
import { CartStore } from '../core/cart.store';
import { trigger, transition, style, animate, query, stagger } from '@angular/animations';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, CheckoutComponent],
  templateUrl: './products.component.html',
  animations: [
    // container animation: stagger child items
    trigger('listAnim', [
      transition(':enter', [
        query(':enter', [
          style({ opacity: 0, transform: 'translateY(10px)' }),
          stagger(80, [animate('320ms cubic-bezier(.2,.8,.2,1)', style({ opacity: 1, transform: 'translateY(0)' }))])
        ], { optional: true })
      ])
    ]),
    // per-item animation (for dynamic additions)
    trigger('itemAnim', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(8px)' }),
        animate('300ms 0ms cubic-bezier(.2,.8,.2,1)', style({ opacity: 1, transform: 'translateY(0)' }))
      ]),
      transition(':leave', [
        animate('220ms ease-in', style({ opacity: 0, transform: 'translateY(-6px)' }))
      ])
    ])
  ]
})
export class ProductsComponent implements OnInit {
  private readonly service = inject(ProductsService);
  readonly cart = inject(CartStore); // singleton injetável

  readonly products = signal<Product[]>([]);
  readonly error = signal<string | null>(null);

  // Retorna o userId atual (usado em templates para chamadas da store)
  userId() {
    return localStorage.getItem('userId') || '00000000-0000-0000-0000-000000000001';
  }

  ngOnInit() {
    this.service.list().subscribe({
      next: (data) => this.products.set(data),
      error: () => this.error.set('Falha ao carregar produtos'),
    });
    // Carrega o carrinho ao iniciar (usa userId do localStorage ou UUID dev padrão)
    const userId = localStorage.getItem('userId') || '00000000-0000-0000-0000-000000000001';
    this.cart.loadCart(userId);
  }

  addToCart(p: Product) {
    // chama backend via CartStore, o interceptor injeta X-User-Id automaticamente
    this.cart.add({ productId: p.id, quantity: 1 }, this.userId());
  }
}

/**
 * Componente de listagem de produtos.
 *
 * Observações de implementação:
 * - Usa `ProductsService` para buscar produtos e armazena o resultado em `products`.
 * - Ao inicializar (`ngOnInit`) também carrega o carrinho do usuário via `CartStore`.
 * - O `addToCart` delega toda a lógica para a store; o componente apenas orquestra a ação.
 */
