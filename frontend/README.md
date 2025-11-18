# Frontend Angular 17 (planejamento)

Vamos inicializar um app Angular 17 com:
- Standalone Components (sem NgModule)
- Novas diretivas de controle: `@for`, `@if`
- `signals`, `computed`, `effect` para estado do carrinho
- `@defer` para lazy carregar o fluxo de pagamento

## Como criar (com Angular CLI)

Certifique-se de ter Node 18+ e Angular CLI 17+.

```bash
# instalar CLI (se precisar)
npm i -g @angular/cli@17

# criar projeto
cd frontend
ng new ecommerce-frontend --standalone --routing --style=scss

cd ecommerce-frontend
# rodar o app
gnpm start
```

Depois disso, adicionaremos componentes:
- `products/` listagem com `@for` e filtros
- `cart/` usando `signals` para total/computed
- `checkout/` carregado com `@defer`

E adicionaremos um diretório `examples/` com versões boas/ótimas/ruins e comentários explicando o porquê.