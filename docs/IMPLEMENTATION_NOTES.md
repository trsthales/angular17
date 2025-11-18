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
