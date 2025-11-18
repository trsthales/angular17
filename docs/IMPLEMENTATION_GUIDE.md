# Guia de Implementação e Melhores Práticas

Este documento descreve, passo a passo, o que implementar para levar a aplicação até um estado pronto para produção, incluindo melhores práticas, comandos e critérios de aceite.

---

## Sumário
- Configuração de ambientes e secrets
- Migrations e seed
- Finalizar domínio e casos de uso
- API REST, DTOs, validação e OpenAPI
- Segurança (JWT/OAuth2)
- Testes de integração com Postgres
- Frontend: integração e proteção
- Qualidade de código e lint
- CI/CD e imagens
- Observabilidade e health
- Performance e otimizações
- Documentação e runbook
- Preparar release e produção
- Revisão final e checklist de segurança

---

## 1. Configuração de ambientes e secrets

- Defina perfis Spring: `local`, `dev`, `staging`, `prod`.
- Nunca commite segredos: use variáveis de ambiente ou um secret manager (Azure Key Vault, AWS Secrets Manager, Vault).
- Crie `application-local.yaml` para desenvolvimento e `application-prod.yaml` para produção (com placeholders).

Exemplo `application-local.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ecommerce
    username: ecommerce_user
    password: ecommerce_pass
  jpa:
    hibernate:
      ddl-auto: update
server:
  port: 8080
security:
  jwt:
    secret: ${SECURITY_JWT_SECRET:your-local-32-byte-secret}
```

Critério de aceite:
- A aplicação inicia com `-Dspring-boot.run.profiles=local` e lê configuração sem que segredos sejam versionados.

---

## 2. Migrations e seed (boa prática)

- Adicionar Flyway ou Liquibase para versionamento do schema.
- Criar migração inicial `V1__init.sql` (tabelas: users, products, cart, cart_items, orders) e scripts de seed (`V2__seed_products.sql`).

Exemplo de dependência (pom.xml):

```xml
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-core</artifactId>
</dependency>
```

Critério de aceite:
- Flyway aplica as migrations na inicialização e o banco fica com o schema esperado.

---

## 3. Finalizar domínio e casos de uso

- Revisar entidades e invariantes (ex.: Cart não permite item negativo), implementar repositórios e casos de uso (Add/Update/Remove/Checkout).
- Escrever testes unitários cobrindo caminhos felizes e de erro.

Melhores práticas:
- Injeção por construtor, classes imutáveis quando aplicável, separar DTOs de entidades.

Critério de aceite:
- Testes unitários cobrem regras de negócio críticas e falham quando invariantes são violadas.

---

## 4. API REST, DTOs, validação e OpenAPI

- Validar requests com JSR-303 (`@Valid`, `@NotNull`, `@Positive`).
- Separar DTOs de entidades; usar MapStruct (opcional) para mapeamento.
- Documentar com `@Operation`, `@Schema`, `@ApiResponse` e fornecer exemplos.

Exemplo de controller:

```java
@Operation(summary = "Adiciona item ao carrinho")
@PostMapping("/api/cart/items")
public ResponseEntity<CartView> addItem(@RequestHeader("X-User-Id") String userId, @Valid @RequestBody AddItemRequest req) { ... }
```

Critério de aceite:
- Swagger UI apresenta schemas e exemplos; chamadas inválidas retornam 400 com mensagens claras.

---

## 5. Segurança (JWT robusto)

- Implementar OAuth2 Resource Server para validar JWTs.
- Use RS256/JWK Set em produção; HMAC (HS256) somente para dev/test.
- Mapear claims para authorities e proteger endpoints por role.

Exemplo de regra de autorização:

```java
http
  .authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/products/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/**").authenticated()
  )
  .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
```

Critério de aceite:
- Sem token => 401; token inválido => 401; token válido com role correta => acesso permitido.

---

## 6. Testes de integração com Postgres (Testcontainers)

- Use Testcontainers para testes que precisam de Postgres real.
- Garanta que Flyway rode dentro do container de teste.

Exemplo breve:

```java
@Testcontainers
@SpringBootTest
class CartIntegrationTest {
  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
  @DynamicPropertySource
  static void props(DynamicPropertyRegistry r) { r.add("spring.datasource.url", postgres::getJdbcUrl); }
}
```

Critério de aceite:
- CI executa testes de integração em container e todos passam.

---

## 7. Frontend: integração e proteção

- Implementar `AuthInterceptor` para incluir `Authorization: Bearer <token>` em requests.
- Preferir HttpOnly cookies para armazenar tokens; se usar `localStorage`, mitigar XSS.
- Tratar 401/403 globalmente (redirect para login).

Exemplo de uso em Angular:

```ts
// AuthInterceptor adiciona header Authorization
```

Critério de aceite:
- Frontend realiza login, obtém token e consome endpoints protegidos com sucesso.

---

## 8. Qualidade de código e lint

- Backend: Checkstyle/Spotless.
- Frontend: ESLint + Prettier + Husky (pre-commit).
- Adicione jobs de lint no CI que bloqueiem PRs.

Critério de aceite:
- PRs só são mesclados com lint e testes passando.

---

## 9. CI/CD e imagens

- Pipelines: build, test, lint, build-image, push, deploy-staging.
- Use secrets no GitHub para credenciais.

Critério de aceite:
- PRs rodando checks e deploy automático para staging após merge com aprovação.

---

## 10. Observabilidade e health

- Adicionar Spring Actuator e Micrometer (Prometheus), logs estruturados.
- Expor health e metrics no `/actuator` e proteger endpoints sensíveis.

Critério de aceite:
- `/actuator/health` retorna `UP` e métricas estão expostas para monitoramento.

---

## 11. Performance e otimizações

- Checar queries (N+1), adicionar índices, considerar cache (Redis) se necessário.
- Fazer testes de carga em staging (k6/JMeter).

Critério de aceite:
- Requisitos de latência definidos e atendidos em staging.

---

## 12. Documentação e runbook

- Atualizar `README.md` e `IMPLEMENTATION_NOTES.md` com quickstart e comandos.
- Criar runbook de deploy/rollback e troubleshooting.

Critério de aceite:
- Novo dev consegue rodar a aplicação em < 30 minutos com README.

---

## 13. Preparar release e produção

- Gerar artefatos imutáveis, tag semântica, smoke tests em staging.
- Validar backups e políticas de recovery.

Critério de aceite:
- Deploy em produção com checklist verificado e smoke tests aprovados.

---

## 14. Revisão final e checklist de segurança

- Rodar SCA (OWASP dependency-check), revisar CORS/CSP e headers de segurança.
- Executar revisão de segurança e, se possível, pentest.

Critério de aceite:
- Sem CVEs críticas e checklist de segurança concluído.

---

## Checklist de execução imediata (prioridade curta)

1. Criar `application-local.yaml` e/ou configurar env vars locais.
2. Adicionar Flyway e criar `V1__init.sql` com tabelas básicas.
3. Garantir endpoints documentados e protegidos por roles.
4. Escrever 3 testes de integração com Testcontainers (add/checkout/update cart).
5. Implementar `AuthInterceptor` no frontend e testar fluxo completo.

---

## Comandos úteis

- Rodar backend (profile local):
```bash
cd backend
mvn -am -pl boot spring-boot:run -Dspring-boot.run.profiles=local
```
- Rodar frontend:
```bash
cd frontend/ecommerce-frontend
npm install
npm start
```
- Gerar token dev (se endpoint `/dev/token` ativo):
```bash
curl -s -X POST http://localhost:8080/dev/token -H "Content-Type: application/json" -d '{"sub":"local-user","roles":["ROLE_USER"],"expiresInSeconds":3600}'
```

---

Se desejar, eu posso aplicar automaticamente os arquivos iniciais (por exemplo: `application-local.yaml`, Flyway `V1__init.sql` e um `data.sql` de seed) e/ou escrever os testes de integração com Testcontainers. Diga qual ação quer que eu execute primeiro.
