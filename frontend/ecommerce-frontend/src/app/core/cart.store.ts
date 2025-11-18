import { computed, effect, inject, Injectable, signal } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ToastService } from './toast.service';

export type CartItem = {
  productId: string;
  productName: string;
  unitPrice: number;
  quantity: number;
};

type ApiCartItem = {
  productId: string;
  productName: string;
  unitPrice: number; // backend envia número/BigDecimal como string/number
  quantity: number;
  lineTotal?: number;
};

type ApiCartView = {
  items: ApiCartItem[];
  total: number;
};

@Injectable({ providedIn: 'root' })
export class CartStore {
  private readonly http = inject(HttpClient);
  private readonly toast = inject(ToastService);
  private readonly itemsSig = signal<CartItem[]>([]);
  readonly loading = signal(false);
  readonly lastError = signal<string | null>(null);

  readonly items = this.itemsSig.asReadonly();
  readonly total = computed(() => this.itemsSig().reduce((acc, i) => acc + i.unitPrice * i.quantity, 0));

  constructor() {
    // Persistência local leve: ajuda durante desenvolvimento offline.
    effect(() => {
      const snapshot = this.itemsSig();
      localStorage.setItem('cart', JSON.stringify(snapshot));
    });

    const cached = localStorage.getItem('cart');
    if (cached) this.itemsSig.set(JSON.parse(cached));
  }

  /** Carrega o carrinho do backend; userId é obrigatório pelo servidor. */
  loadCart(userId: string) {
    if (!userId) return;
    const headers = new HttpHeaders({ 'X-User-Id': userId });
    this.loading.set(true);
    this.http.get<ApiCartView>('/api/cart', { headers }).subscribe({
      next: (res) => {
        const items: CartItem[] = (res.items || []).map(i => ({
          productId: String(i.productId),
          productName: i.productName,
          unitPrice: Number(i.unitPrice),
          quantity: Number(i.quantity),
        }));
        this.itemsSig.set(items);
        this.lastError.set(null);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Falha ao carregar carrinho', err);
        const msg = err?.message ?? 'Erro ao carregar carrinho';
        this.lastError.set(msg);
        this.toast.showError('Falha ao carregar carrinho');
        this.loading.set(false);
      }
    });
  }

  /** Adiciona item no backend e atualiza store com resposta (fonte de verdade). */
  add(item: { productId: string; quantity: number }, userId: string) {
    if (!userId) throw new Error('userId é obrigatório');
    const headers = new HttpHeaders({ 'X-User-Id': userId });
    const body = { productId: item.productId, quantity: item.quantity };
    this.loading.set(true);
    this.http.post<ApiCartView>('/api/cart/items', body, { headers }).subscribe({
      next: (res) => {
        const items: CartItem[] = (res.items || []).map(i => ({
          productId: String(i.productId),
          productName: i.productName,
          unitPrice: Number(i.unitPrice),
          quantity: Number(i.quantity),
        }));
        this.itemsSig.set(items);
        this.lastError.set(null);
        this.toast.showSuccess('Item adicionado ao carrinho');
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Falha ao adicionar item', err);
        const msg = err?.message ?? 'Erro ao adicionar item';
        this.lastError.set(msg);
        this.toast.showError('Falha ao adicionar item');
        this.loading.set(false);
      }
    });
  }

  clear() { this.itemsSig.set([]); }
}
