# E-commerce (Angular 17 + Spring Boot 3 + Clean Architecture)

Objetivo: aprender Angular 17 (com exemplos bons/ótimos/ruins) e construir um e-commerce guiado por TDD, com backend em Spring Boot (JDK 17, Maven), Postgres, e decisões arquiteturais e de negócio bem justificadas.

## Estrutura
- `backend/` Maven multimódulo:
  - `domain/` regras de negócio puras (sem frameworks)
  - `application/` casos de uso e portas (input/output)
  - `infrastructure/` adapters (JPA, Postgres, etc.)
  - `boot/` aplicação Spring Boot (controllers, config)
- `docs/` exercícios e comparativos bons/ótimos/ruins
- `docker/` Postgres + pgAdmin para dev

## Requisitos
- JDK 17 e Maven
- Docker (opcional para Postgres local)

## Como rodar
```bash
# Subir Postgres para dev
cd docker
docker compose up -d

# Rodar todos os testes (TDD!)
cd ../backend
mvn -DskipTests=false test

# Executar a API
cd boot
mvn spring-boot:run
```

API inicial:
- `GET http://localhost:8080/api/products` → lista produtos (vazio até inserir dados)

## Decisões principais
- **Arquitetura Limpa**: domínio independente, aplicação orquestra, infra adapta.
- **TDD por padrão**: testes de domínio, de aplicação, de integração (Testcontainers) e de web (MockMvc).
- **Postgres real nos testes**: evita falsos-positivos de persistência.
- **Controllers finos**: sem regra de negócio — melhor testabilidade e evolução.

## Próximos passos (frontend Angular 17)
- Criar `frontend/` com Angular 17 standalone, `signals`, `@for/@if`, `@defer`.
- Exemplos bons/ótimos/ruins no template e estado.
- Integração com API e store do carrinho.

Consulte `docs/exercicios.md` e `docs/bons-otimos-ruins.md` para a trilha de estudos e desafios.