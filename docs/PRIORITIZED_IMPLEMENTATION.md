# Plano Prioritário de Implementação — O quê, Como e Por quê

Este documento lista, em ordem de prioridade, as tarefas necessárias para levar a aplicação até um estado pronto para produção. Para cada item você encontra: o que implementar, como fazer (passos/trechos de código/comandos), e por que eu recomendo essa ordem (justificativa técnica e de processo).

> Nota: priorizei tarefas que reduzem risco, habilitam validação rápida e garantem segurança/observabilidade. Siga a ordem e marque cada item como concluído antes de prosseguir para o próximo.

---

## 1) Configuração de ambientes, perfis e secrets (Prioridade Alta)

- O que:
  - Definir perfis Spring: `local`, `dev`, `staging`, `prod`.
  - Garantir que segredos (DB passwords, JWT keys) não estejam no repositório — usar variáveis de ambiente ou secret manager.

- Como:
  1. Adicionar `application-local.yaml` (ex.: `backend/boot/src/main/resources/application-local.yaml`) com configurações para PostgreSQL local (veja snippet abaixo).
  2. Ler secrets via environment variables (ex.: `${SECURITY_JWT_SECRET}`) e documentar variáveis necessárias no README.

  Exemplo trecho `application-local.yaml`:

  ```yaml
  spring:
    datasource:
      url: jdbc:postgresql://localhost:5432/ecommerce
      username: ecommerce_user
      password: ecommerce_pass
    jpa:
      hibernate:
        ddl-auto: update
  security:
    jwt:
      secret: ${SECURITY_JWT_SECRET:local-32-byte-secret}
  ```

- Por quê (justificativa):
  - Permite você rodar local sem alterar arquivos globais do projeto e evita vazar credenciais.
  - Definir perfis separa comportamentos (ex.: H2 para dev vs Postgres para prod) e facilita CI.

- Critério de aceite:
  - `mvn -am -pl boot spring-boot:run -Dspring-boot.run.profiles=local` inicia e conecta ao Postgres local (credenciais via env ou arquivo local não comitado).

---

## 2) Migrations e seed (Alta)

- O que:
  - Introduzir Flyway (ou Liquibase) e criar migrations versionadas (DDL) + scripts de seed para ambiente dev.

- Como:
  1. Adicionar dependência Flyway ao `boot/pom.xml`.
  2. Criar `src/main/resources/db/migration/V1__init.sql` com DDL (tabelas: product, cart, cart_item, order, user).
  3. (Opcional) `V2__seed_products.sql` para dados de dev.

  Exemplo minimal V1:

  ```sql
  CREATE TABLE product (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price NUMERIC(10,2) NOT NULL
  );
  -- criar tabelas cart, cart_item, orders, users conforme modelo
  ```

- Por quê:
  - Migrations versionadas previnem divergências entre ambientes e permitem rollback controlado.
  - Facilita CI/CD: ao subir um ambiente, as migrations são aplicadas automaticamente.

- Critério de aceite:
  - Rodando a aplicação, Flyway aplica V1 e V2 sem erros.

---

## 3) Segurança JWT robusta e autorização por roles (Alta)

- O que:
  - Configurar OAuth2 Resource Server (JWT validation) com JWK (RS256) em produção, HMAC apenas para dev.
  - Mapear claims (`roles`) para `GrantedAuthority` e declarar regras por endpoint.

- Como:
  1. Usar `spring-boot-starter-oauth2-resource-server` e configurar `spring.security.oauth2.resourceserver.jwt.jwk-set-uri` em `prod`.
  2. No `SecurityConfig`, proteger rotas: `/api/products/**` permitAll, `/api/cart/**` authenticated, `/api/admin/**` hasRole('ADMIN').

  Exemplo (trecho):

  ```java
  http
    .authorizeHttpRequests(auth -> auth
      .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
      .requestMatchers("/api/products/**").permitAll()
      .requestMatchers("/api/admin/**").hasRole("ADMIN")
      .requestMatchers("/api/**").authenticated()
    )
    .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
  ```

- Por quê:
  - Segurança é prioridade alta: proteger recursos evita vazamento e abuso.
  - JWK/RS256 permite delegar autenticação a um Identity Provider e validar assinaturas em produção.

- Critério de aceite:
  - Requisições sem token => 401; com token válido e claim `roles` apropriado => acesso permitido.

---

## 4) Model/Domain e casos de uso (Média-Alta)

- O que:
  - Finalizar entidades (`Product`, `Cart`, `CartItem`, `Order`, `User`), repositórios e casos de uso (Add, Update, Remove, Checkout).
  - Assegurar invariantes (ex.: quantidade >= 0, produto existe).

- Como:
  - Implementar use-cases no módulo `application` como serviços puros (stateless) com injeção por construtor.
  - Escrever testes unitários (JUnit + Mockito) cobrindo regras de negócio e exceções.

- Por quê:
  - Camada de domínio correta reduz bugs raciais e facilita testes unitários isolados.
  - Separação limpa entre domínio e infraestrutura facilita mudanças (DB, cache).

- Critério de aceite:
  - Testes unitários passam e cobrem regras críticas (ex.: checkout calcula total corretamente e limpa o carrinho).

---

## 5) API REST, validação e OpenAPI (Média)

- O que:
  - Completar controllers com DTOs validados (`@Valid`, `@NotNull`, `@Positive`) e mapear respostas para views.
  - Documentar operações com `@Operation`, `@Schema` e exemplos para requests/responses.

- Como:
  - Usar DTOs (Record/POJO) e MapStruct para mapeamento (opcional).
  - Atualizar `OpenApiConfig` com security scheme `bearerAuth` e exemplos.

- Por quê:
  - Validação na borda evita processamento desnecessário. OpenAPI ajuda QA, frontend e integrações.

- Critério de aceite:
  - Swagger mostra schemas e exemplos e é possível testar endpoints com o token.

---

## 6) Testes de integração com Postgres (Testcontainers) (Média)

- O que:
  - Escrever testes de integração que usam Testcontainers Postgres para validar interações reais com DB.

- Como:
  - Adicionar dependência Testcontainers (scope test) e usar `@Testcontainers` + `PostgreSQLContainer` e `@DynamicPropertySource`.
  - Cobrir cenários: adicionar item, atualizar quantidade, checkout e persistência/rollback.

- Por quê:
  - Evita falsos positivos que ocorrem com H2; garante que DDL e queries funcionam com Postgres.

- Critério de aceite:
  - CI executa testes integrais que levantam Postgres via Testcontainers e passam.

---

## 7) Frontend: autenticação, interceptors e UX (Média)

- O que:
  - Implementar `AuthInterceptor` para adicionar header `Authorization: Bearer <token>`.
  - Armazenamento do token: preferir cookies `HttpOnly` + SameSite para segurança, ou em memória se necessário.
  - UX: estados loading, empty, erros; tratamento de 401 (redirect/login) global.

- Como:
  - Criar `AuthService` para login/refresh e `AuthInterceptor` para injetar token.
  - Proteger rotas que exigem login com guardas (Guards).

- Por quê:
  - Evita exposição de token via XSS; UX consistente diminui suporte.

- Critério de aceite:
  - Fluxo de login → adicionar ao carrinho → checkout funciona no frontend com tokens válidos.

---

## 8) Qualidade de código, lint e testes automatizados (Média-Baixa)

- O que:
  - Configurar Checkstyle/Spotless para Java; ESLint + Prettier para Angular.
  - Cobertura mínima em módulos críticos e testes unitários no frontend.

- Como:
  - Integrar pre-commit hooks (Husky) e jobs no CI para lint/tests.

- Por quê:
  - Mantém consistência, reduz bugs e facilita revisão de PRs.

- Critério de aceite:
  - PRs passam lint e testes; pipeline de PR bloqueia merges quando falha.

---

## 9) CI/CD, imagens e deployments (Baixa-Média)

- O que:
  - Criar pipelines que façam build/test, build da imagem Docker e deploy para staging.

- Como:
  - GitHub Actions jobs: `lint`, `build-backend`, `test-backend`, `build-frontend`, `test-frontend`, `build-image`, `deploy-staging`.
  - Usar secrets do repositório para credenciais de registry e variáveis de ambiente.

- Por quê:
  - Automatização reduz erro humano, acelera releases e garante repetibilidade.

- Critério de aceite:
  - Merge em `main` dispara build e deploy automáticos (ou por tag) para staging com checks.

---

## 10) Observability, health e logs (Baixa)

- O que:
  - Adicionar Spring Actuator, Micrometer, logs JSON e endpoints de health/metrics.

- Como:
  - Adicionar dependências Actuator e Micrometer, habilitar `/actuator/health`, `/actuator/prometheus`.

- Por quê:
  - Permite monitoramento e alerta em staging/production.

- Critério de aceite:
  - `/actuator/health` retorna `UP` e métricas são coletáveis.

---

## 11) Performance, caching e otimizações (Opcional/Posterior)

- O que:
  - Revisar queries e índices, adicionar cache (Redis) para endpoints de leitura intensiva.

- Por quê:
  - Escala e latência; otimização onde houver necessidade após métricas.

---

## 12) Documentação, runbook e checklist de segurança (Finalizar antes do GA)

- O que:
  - Atualizar README, `IMPLEMENTATION_NOTES.md`, e criar runbook de deploy/rollback.
  - Executar SCA e correções das dependências com CVEs.

- Por quê:
  - Preparar equipe para operar e recuperar o serviço em produção.

---

## Ordem resumida (prioridade)
1. Perfis e secrets (local/dev/prod)
2. Migrations (Flyway) + seed
3. Segurança JWT e autorização por roles
4. Domínio e casos de uso (tests unitários)
5. API REST + validação + OpenAPI
6. Testes de integração com Postgres (Testcontainers)
7. Frontend: auth + interceptors
8. Lint/tests e qualidade
9. CI/CD e deploy para staging
10. Observability + metrics
11. Performance e caching (conforme necessidade)
12. Documentação, runbook e checklist final de segurança

---

## Próximos passos práticos (o que eu posso aplicar agora)
- Criar `application-local.yaml` e atualizar docs.
- Adicionar Flyway e criar `V1__init.sql` com tabelas básicas e `V2__seed_products.sql`.
- Escrever 3 testes de integração com Testcontainers (add/update/checkout cart).

Se desejar, eu aplico automaticamente os três itens acima e executo uma compilação para validar. Quer que eu comece por eles agora? 
