# Exercícios (TDD, Arquitetura Limpa e Angular 17)

Estes exercícios são incrementais. Faça sempre em TDD: escreva o teste primeiro, rode, veja falhar, implemente o mínimo para passar, refatore.

## Back-end (Spring Boot)

1) Domínio: Cupom de desconto
- Regras: percentual (0-50%), data de validade, aplicável por produto ou carrinho inteiro.
- Testes: aplicar cupom no `Cart` recalcula total; cupom expirado deve ser rejeitado.
- Dica: crie `Discount` VO e `Coupon` entidade.

2) Usuário e cadastro
- Crie `User` (id, name, email, passwordHash). Validações no domínio.
- Teste de aplicação: `CreateUserUseCase` (porta de saída `SaveUserPort`).
- Integração: persistência com Testcontainers.

3) Pagamento mockado
- Defina porta `PaymentGatewayPort` e caso de uso `CheckoutCartUseCase`.
- Teste duplo: fake de gateway para aprovar/recusar.
- Regra: carrinho não pode estar vazio; total deve bater com soma dos itens.

4) Consultas eficientes
- Adicione teste de repositório usando `@DataJpaTest` para paginação e ordenação.
- Anti-exemplo: N+1 (mostre e depois corrija com `@EntityGraph` ou `join fetch`).

5) Observabilidade
- Teste de `@RestControllerAdvice` para mapear exceptions → HTTP 400/404/422.
- Teste de logs estruturados (usar `assertThat(logs)`).

## Front-end (Angular 17)

6) Standalone Components + Rotas
- Crie app standalone com rotas: `/`, `/produtos`, `/carrinho`, `/login`.
- Exercício: implementar `@for`, `@if`, `@defer` e `Signals` no catálogo.
- Anti-exemplo: two-way binding desnecessário causando detecção de mudanças custosa.

7) Estados com Signals
- Crie `CartStore` com `signal`, `computed` e `effect`.
- Teste: unit tests com `@angular/core/testing` para computar total.

8) Comunicação HTTP + interceptors
- Serviço `ProductsApi` usando `fetch` via `HttpClient`.
- Interceptor de retry/backoff. Teste com `HttpTestingController`.

9) A11y e UX
- Exercício: navegação somente teclado, aria-labels, foco gerenciado.
- Anti-exemplo: modal travando rolagem sem foco adequado.

10) Performance
- Defer carregamento de componentes de pagamento.
- Medir FID/TTI com Web Vitals (exemplo e leitura).