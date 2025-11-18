# Implementation Notes

Resumo das mudanças realizadas até agora e passos para rodar localmente o sistema.

## O que foi feito
- Ajustes na descoberta de beans do Spring Boot para que classes de `infrastructure` sejam
  corretamente encontradas (`@EnableJpaRepositories` e `@EntityScan` adicionadas em `EcommerceApplication`).
- Comentários e JavaDoc adicionados em classes do backend (ex.: adapters, use-cases, `CartController`).
- `docker/docker-compose.yml` ajustado para usar imagens com tags explícitas e sem o campo `version` obsoleto.
- Comentários explicativos adicionados nos arquivos front-end principais (ver seção abaixo).

## Como rodar localmente (passo-a-passo)

Requisitos:
- Java 17+ instalado (foi testado com Java 17 no ambiente atual). Para migrar para Java 21 siga o plano separado.
- Maven instalado.
- Docker Desktop rodando no Windows (daemon ativo).

1) Startar Docker Desktop (GUI do Windows) e confirmar que o daemon está ativo.

2) Subir Postgres e pgAdmin (a partir da raiz do repositório):

```bash
docker compose -f docker/docker-compose.yml up -d
docker compose -f docker/docker-compose.yml ps
```

3) Opcional: ajustar `backend/boot/src/main/resources/application.yaml` para usar o mesmo nome de banco
   definido no `docker-compose` (`ecommerce`) ou manter `postgres` e alterar `docker-compose.yml`.

4) Build / rodar backend (do diretório `backend`):

```bash
cd backend
mvn -am -pl boot -DskipTests package
java -jar boot/target/*.jar
```

Ou para desenvolvimento com hot reload (requer Maven e dependências):

```bash
mvn -am -pl boot spring-boot:run
```

5) Rodar frontend (do diretório `frontend/ecommerce-frontend`):

```bash
cd frontend/ecommerce-frontend
npm install
npm start
```

## Endpoints principais
- `GET /api/products` - lista de produtos
- `GET /api/cart` - retorna o carrinho do usuário (espera header `X-User-Id`)
- `POST /api/cart/items` - adiciona item ao carrinho (header `X-User-Id`)

- `PUT /api/cart/items` - atualiza a quantidade de um item (body: `{productId, quantity}`)
- `DELETE /api/cart/items/{productId}` - remove um item do carrinho
- `POST /api/cart/checkout` - finaliza o pedido, limpa o carrinho e retorna `{ orderId, total }`

## Documentação (Swagger/OpenAPI)
- Após adicionar a dependência `springdoc-openapi-starter-webmvc-ui`, a UI do Swagger estará disponível em:
  - `http://localhost:8080/swagger-ui/index.html`
  - O JSON OpenAPI estará em `http://localhost:8080/v3/api-docs`

Obs: é necessário que a aplicação backend esteja rodando para acessar essas rotas.

## Autenticação para testes (Bearer) — Dev tokens
- O projeto agora valida JWTs via OAuth2 Resource Server. Para facilitar testes locais há um endpoint de desenvolvimento:
  - `POST /dev/token` — gera um JWT HS256 assinado com a chave definida em `security.jwt.secret`.
  - Corpo (opcional): `{ "sub": "dev-user", "roles": ["ROLE_USER"], "expiresInSeconds": 3600 }`
  - Resposta: `{ "token": "<JWT>", "expiresAt": <epoch_seconds> }`

- Como usar:
  1. Gere um token com `POST /dev/token` (ou use `jwt.io` / `node` conforme a seção abaixo).
  2. Na Swagger UI (`/swagger-ui/index.html`) clique em `Authorize` e informe `Bearer <seu-token>`.
 3. Endpoints sob `/api/**` exigem autenticação e aceitarão tokens válidos assinados com a chave definida.

- Observação: o endpoint `/dev/token` é apenas para ambientes de desenvolvimento. Em produção, desative este endpoint e use um provedor de identidade (OIDC/JWKs). Para produzir tokens seguros, use chaves assimétricas (RS256) ou um servidor de autorização.

## Autenticação JWT (nova configuração)
- Agora o backend suporta validação de tokens JWT por meio do Spring OAuth2 Resource Server.
- A implementação usa uma chave simétrica HS256 definida em `backend/boot/src/main/resources/application.yaml` em `security.jwt.secret`.

Gerar um token de teste (HS256)
- Você pode criar um token JWT assinado com HS256 usando `node` + `jsonwebtoken`, ou o site `https://jwt.io/`.

Exemplo rápido usando `node` (requere `npm` + `npx`):

```bash
# instala temporariamente o jsonwebtoken e gera um token com payload simples
npx -y jsonwebtoken@9.0.0 -e "console.log(require('jsonwebtoken').sign({sub:'test-user', roles:['ROLE_USER']}, 'secret-change-me-for-production', {expiresIn:'1h'}))"
```

- Copie o token gerado e, na Swagger UI (`/swagger-ui/index.html`), clique em `Authorize` e informe `Bearer <seu-token>`.
- Endpoints sob `/api/**` exigem autenticação e aceitarão tokens válidos assinados com a chave definida.

Exemplo de token (temporário)

> Obs: token de exemplo gerado localmente com a chave atual de desenvolvimento. Este token é apenas para documentação.

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJleGFtcGxlLXVzZXIiLCJleHAiOjE3NjM0NDU4MzgsImlhdCI6MTc2MzQ0MjIzOCwicm9sZXMiOlsiUk9MRV9VU0VSIl19.YtkqAVD9y3omT-3xLyxjFFEiweLZxtbpzpsROVaILx4
```

Tempo de expiração: 1 hora a partir da geração.

Gere sempre um token fresco para testes via `POST /dev/token`.

Segurança e produção
- Troque a chave simétrica por um JWK Set (URI) ou use chaves assimétricas (RS256) em produção.
- Para usar um JWK Set remoto configure `spring.security.oauth2.resourceserver.jwt.jwk-set-uri` apontando para o provedor de identidade.

## Notas sobre o desenvolvimento
- O backend é um projeto Maven multi-módulo (modules: `domain`, `application`, `infrastructure`, `boot`).
- Use `mvn -am -pl boot ...` para construir `boot` junto com dependências locais.
- O frontend envia o header `X-User-Id` via serviços/store; o backend exige esse header para operações de carrinho.

## Próximos passos sugeridos
- Finalizar comentários restantes no backend (revisar controladores e classes expostas).
- Completar cobertura de testes de integração com Testcontainers para validar contrato com Postgres.
- Planejar e aplicar migração para Java 21 (atualizar `maven.compiler.source`/`target`, testar dependências compatíveis).

## Contato
Se quiser que eu aplique as próximas etapas (ex.: atualizar POM para Java 21, gerar testes, ou finalizar comentários restantes), diga qual ação prefere.
