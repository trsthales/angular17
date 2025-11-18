# Exemplos Bons, Ótimos e Ruins

A ideia é comparar e justificar.

## Back-end

- Bom: `Value Object` `Money` imutável centralizando regras de arredondamento.
- Ótimo: portas e adaptadores explícitos (`LoadProductsPort` + `JpaProductAdapter`), testes de integração com Testcontainers garantindo contrato real com Postgres.
- Ruim: Controller contendo regra de negócio (ex.: calcular total no controller). Justificativa: reduz reuso, dificulta testes, quebra Clean Architecture.

- Bom: `Cart` como agregado controlando invariantes (quantidade > 0).
- Ruim: atualizar quantidade via acesso direto à lista de itens. Justificativa: fere encapsulamento e invariantes.

## Front-end (Angular 17)

- Bom: uso de `@for` e `@if` no template para controle de fluxo mais performático.
- Ótimo: `signals` + `computed` para derivar total do carrinho e `@defer` para carregamento tardio do checkout.
- Ruim: serviços singleton com estado mutável global sem `signals`, causando inconsistência e falta de reatividade.

Cada seção terá snippets no momento de implementar o frontend.