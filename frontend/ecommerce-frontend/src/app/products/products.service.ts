import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';

export type Product = { id: string; name: string; price: number };

@Injectable({ providedIn: 'root' })
export class ProductsService {
  private readonly baseUrl = 'http://localhost:8080';
  readonly loading = signal(false);

  constructor(private http: HttpClient) {}

  list() {
    this.loading.set(true);
    return this.http.get<Product[]>(`${this.baseUrl}/api/products`);
  }
}
