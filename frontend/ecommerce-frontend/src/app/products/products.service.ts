import { HttpClient } from '@angular/common/http';
import { Injectable, signal } from '@angular/core';

/**
 * Modelo simplificado de produto usado no frontend.
 */
export type Product = { id: string; name: string; price: number };

/**
 * Serviço responsável por consultar endpoints de produtos.
 *
 * Observações:
 * - `baseUrl` aponta para o backend em `localhost:8080` durante desenvolvimento.
 * - `loading` é um sinal usado para indicar que uma chamada está em andamento.
 */
@Injectable({ providedIn: 'root' })
export class ProductsService {
  private readonly baseUrl = 'http://localhost:8080';
  readonly loading = signal(false);

  constructor(private http: HttpClient) {}

  /**
   * Retorna um Observable com a lista de produtos do backend.
   * O `loading` é marcado como `true` antes da chamada — o componente
   * que consumir esse serviço pode combinar com `finalize`/`tap` localmente
   * para limpar o estado de loading se desejar (não modificado aqui para
   * evitar alterar o contrato atual).
   */
  list() {
    this.loading.set(true);
    return this.http.get<Product[]>(`${this.baseUrl}/api/products`);
  }
}
