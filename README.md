# Shop API

REST API para gestao de produtos, pedidos e pagamentos construída com Spring Boot 3.3, Java 17 e PostgreSQL. O projeto demonstra autenticacao stateless com JWT, autorizacao baseada em roles e filtros dinamicos com Specifications.

## Destaques

- Autenticacao com JWT e filtro customizado (`JwtAuthenticationFilter`) que remonta as roles embutidas no token.
- Camada de aplicacao orientada a casos de uso (`service.usecase`) que instancia servicos dedicados (`*ApplicationService`) para orquestrar regras e portas de dominio.
- Dominio isolado em `domain.*`, sem annotations de frameworks, com repositorios como portas (interfaces) e servicos puros como `PriceCalculator`.
- Adaptadores de infraestrutura em `infrastructure.persistence.*` traduzem entre entidades JPA e modelos de dominio via mapeadores dedicados.
- Controladores REST mapeiam DTOs externos para comandos de dominio, mantendo o core independente.
- Tratamento de erros consistente via `ApiExceptionHandler`, retornando `ApiError` para recursos inexistentes (`ResourceNotFoundException`) e violacoes de negocio.
- Seeds opcionais para usuarios e produtos (`DbSeeder`, `AdminBootstrap`) e documentacao OpenAPI via Springdoc.

## Stack principal

- Java 17
- Spring Boot 3.3.x (Web, Security, Data JPA, Validation)
- PostgreSQL 13+
- JJWT 0.11.5
- Springdoc OpenAPI 2.5.0
- Maven Wrapper (`mvnw`, `mvnw.cmd`)
- Lombok (opcional para a IDE)

## Estrutura e pacotes

- Pacote raiz: `com.peritumct.shopapi`
- Pastas relevantes:
  - `controller` para endpoints REST e mapeamento DTO <-> dominio.
  - `dto` para modelos de transferencia expostos externamente.
  - `domain` para modelos, enums, comandos e portas independentes de framework.
  - `config` para configuracoes cruzadas (ex.: registro de `PriceCalculator`).
  - `infrastructure.persistence` para entidades JPA, mapeadores e adaptadores das portas de dominio.
  - `security` para JWT, filtros, services e helpers de autorizacao.
  - `service` para casos de uso (aplicacao), specs reutilizaveis e seeds.
  - `service.exception` para excecoes de dominio.
  - `service.spec` para Specifications de filtros dinâmicos.
  - `init` para bootstrap de dados.

## Requisitos

- JDK 17
- PostgreSQL 13 ou superior rodando localmente
- Maven Wrapper (incluido)

## Banco de dados

Crie o banco e usuario padrao:

```sql
CREATE DATABASE shopdb;
CREATE USER shop WITH ENCRYPTED PASSWORD 'shop';
GRANT ALL PRIVILEGES ON DATABASE shopdb TO shop;
```

## Configuracao de perfis

Use o perfil `local` para desenvolvimento. Copie `src/main/resources/application-local.properties` (nao versionado publicamente) e ajuste conforme necessario:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/shopdb
spring.datasource.username=shop
spring.datasource.password=shop
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
spring.jpa.show-sql=true

app.jwt.secret=<chave-base64-256bits>
app.jwt.exp-minutes=1440

springdoc.swagger-ui.path=/swagger-ui.html
```

> Gere uma chave forte para `app.jwt.secret`, por exemplo `openssl rand -base64 32`. O valor deve ser Base64 de pelo menos 256 bits.

## Execucao local

```powershell
# instalar dependencias e compilar
.\mvnw.cmd clean package -DskipTests

# executar em modo dev
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local

# ou via jar
java -jar target\shopapi-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

## Usuarios seeds

| usuario  | senha       | role     | origem                    |
|----------|-------------|----------|---------------------------|
| admin    | admin123    | ADMIN    | AdminBootstrap / DbSeeder |
| operator | operator123 | OPERATOR | DbSeeder                  |
| client   | client123   | USER     | DbSeeder                  |

As seeds sao criadas somente quando nao ha registros nas tabelas.

## Autenticacao JWT

1. `POST /api/auth/login`
   ```json
   {
     "username": "admin",
     "password": "admin123"
   }
   ```
2. Resposta:
   ```json
   { "token": "<jwt>" }
   ```
3. Envie `Authorization: Bearer <jwt>` nas requisicoes protegidas.
4. As roles sao adicionadas ao token e remontadas pelo `JwtAuthenticationFilter`, garantindo o prefixo `ROLE_` para uso com `@PreAuthorize`.

## Endpoints principais

**Produtos**
- `GET /api/products` - lista todos os produtos (usuario autenticado).
- `GET /api/products/{id}` - consulta por id.
- `POST /api/products` - cria produto (ADMIN ou OPERATOR).
- `PUT /api/products/{id}` - atualiza produto (ADMIN ou OPERATOR).
- `DELETE /api/products/{id}` - remove produto (ADMIN ou OPERATOR).
- `GET /api/products/search` - busca paginada com filtros `name`, `category`, `minPrice`, `maxPrice`.

**Pedidos**
- `GET /api/orders` - paginado; aceita filtros `status`, `from`, `to`, `minTotal`, `maxTotal`.
- `GET /api/orders/{id}` - retorna `OrderDetailDTO` com itens; acesso restrito ao dono ou staff via `SecurityExpressions`.
- `PUT /api/orders/{id}` - atualiza itens, desconto e frete; delega recalculo ao `PriceCalculator`; autorizado para dono ou staff.
- `PATCH /api/orders/{id}/status` - atualiza status (volta a recalcular totais).
- `DELETE /api/orders/{id}` - remove pedido (ADMIN).

**Pagamentos**
- `POST /api/orders/{orderId}/payments` - cria pagamento do pedido (dono ou staff).
- `POST /api/orders/{orderId}/payments/{paymentId}/approve` - aprova pagamento e coloca o pedido em `PAID` (ADMIN ou OPERATOR).

## Regras de negocio

- Recalculo automatico de subtotal, desconto, frete e total garantido por `PriceCalculator` no dominio.
- Validacao de produtos ao reconstruir itens de pedido.
- Specifications JPA reutilizaveis (`OrderSpecifications`, `ProductSpecifications`) isoladas na camada de infraestrutura.
- Atualizacao de status do pedido ao aprovar pagamentos.
- Controladores delegam a casos de uso; estes disparam `ResourceNotFoundException` ou `IllegalArgumentException` convertidas em respostas claras pelo `ApiExceptionHandler`.

## Documentacao da API

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Colecao Insomnia

Importe `Insomnia/ShopAPI-insomnia.json`. A colecao inclui:
- Requests para login e renovacao de token.
- Exemplos de chamadas separadas por role.
- Variavel que injeta o token de login automaticamente.

## Comandos uteis

- `.\mvnw.cmd test` - roda testes (quando existirem).
- `.\mvnw.cmd dependency:tree` - inspeciona a arvore de dependencias.
- `mvn clean` - remove artefatos de build.

## Estrutura resumida

```
src/
  main/
    java/com/peritumct/shopapi/
      controller/
      domain/
      infrastructure/
      security/
      service/
      config/
    resources/
      application.properties
      application-local.properties (perfil dev)
Insomnia/
  ShopAPI-insomnia.json
pom.xml
README.md
```

## Licenca

Uso livre para fins de estudo e avaliacao.
