# Shop API — Documentação Detalhada
*Versão do projeto: 0.0.1-SNAPSHOT*  
*Última atualização: 2025-08-11*

## 1. Visão Geral
API de e-commerce minimalista construída com **Spring Boot 3 (Java 17)**, **JWT** para autenticação stateless e **PostgreSQL** como banco de dados. Inclui CRUD de **Produtos**, fluxo de **Autenticação/Registro** e **Pedidos** com itens.

### Objetivos
- Servir de base limpa para projetos REST.
- Demonstrar **RBAC** simples com `ROLE_ADMIN`, `ROLE_OPERATOR`, `ROLE_CLIENT`.
- Entregar exemplos práticos de **JWT** e documentação via **Swagger UI**.

## 2. Arquitetura & Stack
- **Runtime**: Java 17
- **Framework**: Spring Boot 3.3.x (Web, Data JPA, Security, Validation)
- **DB**: PostgreSQL
- **Auth**: JWT (JJWT 0.11.x)
- **Docs**: springdoc-openapi (Swagger UI)
- **Build**: Maven (com suporte a Maven Wrapper)
- **Empacotamento**: JAR executável (Spring Boot repackage)

### Camadas (pacotes)
```
com.example.shopapi
 ├─ config        # SecurityFilterChain, OpenAPI
 ├─ controller    # Auth, Product, Order controllers
 ├─ dto           # DTOs de entrada/saída
 ├─ model         # Entidades JPA (User, Product, Order, OrderItem, Role)
 ├─ repository    # Spring Data JPA repositories
 ├─ security      # JwtUtil, JwtAuthenticationFilter, UserDetailsService
 └─ service       # DbSeeder (seed inicial)
```

## 3. Modelo de Dados
### Entidades
- **User** (id, username*, password, role)
- **Product** (id, name, description, price)
- **Order** (id, user, createdAt, items[])
- **OrderItem** (id, order, product, quantity, unitPrice)

### Relacionamentos (ERD simplificado)
```
User (1) ── (N) Order
Order (1) ── (N) OrderItem
Product (1) ── (N) OrderItem
```

### Regras
- `username` é **único**.
- `OrderItem.unitPrice` é congelado no momento do pedido (copiado do `Product.price`).

## 4. Segurança & Autorização
- **JWT Bearer** no header `Authorization: Bearer <token>`.
- Endpoints **públicos**: `/api/auth/**`, `/swagger-ui/**`, `/v3/api-docs/**`.
- Demais endpoints exigem autenticação.
- RBAC:
  - `POST|PUT|DELETE /api/products/**` → `ROLE_ADMIN` ou `ROLE_OPERATOR`.
  - Demais (ex.: `GET /api/products`, `POST /api/orders`) → usuário autenticado.

### Tokens
- Assinatura: **HS256** com `security.jwt.secret` (Base64, 256-bit recomendado).
- Padrão: validade 24h (`security.jwt.expiration-ms=86400000`).

## 5. Endpoints (Resumo)
### Autenticação
- `POST /api/auth/register` — cria usuário `ROLE_CLIENT` e retorna `{ token, role }`.
- `POST /api/auth/login` — autentica e retorna `{ token, role }`.

### Produtos
- `GET /api/products` — lista.
- `GET /api/products/{id}` — detalhe.
- `POST /api/products` — cria (**ADMIN/OPERATOR**).
- `PUT /api/products/{id}` — atualiza (**ADMIN/OPERATOR**).
- `DELETE /api/products/{id}` — remove (**ADMIN/OPERATOR**).

### Pedidos
- `POST /api/orders` — cria pedido a partir de itens.
- `GET /api/orders` — lista pedidos.

## 6. Contratos de Requisição/Resposta
### Register
**POST** `/api/auth/register`
```json
{
  "username": "alice",
  "password": "StrongPass@123"
}
```
**200 OK**
```json
{ "token": "<JWT>", "role": "CLIENT" }
```

### Login
**POST** `/api/auth/login`
```json
{
  "username": "admin",
  "password": "admin123"
}
```
**200 OK**
```json
{ "token": "<JWT>", "role": "ADMIN" }
```

### Criar Produto (ADMIN/OPERATOR)
**POST** `/api/products`
```json
{
  "name": "Notebook",
  "description": "16GB RAM",
  "price": 7999.90
}
```

### Criar Pedido
**POST** `/api/orders`
```json
{
  "items": [
    {"productId": 1, "quantity": 2},
    {"productId": 2, "quantity": 1}
  ]
}
```

## 7. Como Rodar
### Banco de Dados
```sql
CREATE ROLE shop WITH LOGIN PASSWORD 'shop';
CREATE DATABASE shopdb OWNER shop;
```
Verifique o serviço do PostgreSQL e credenciais condizentes com `application-local.properties`.

### Build & Run
Com Maven instalado:
```powershell
mvn clean package -DskipTests
java -jar target/shopapi-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```
Com Maven Wrapper (se gerado):
```powershell
.\mvnw.cmd clean package -DskipTests
java -jar target/shopapi-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

### Swagger
- `http://localhost:8080/swagger-ui/index.html`

## 8. Configuração (Profiles & Propriedades)
Arquivo: `src/main/resources/application-local.properties` (exemplo)
```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/shopdb
spring.datasource.username=shop
spring.datasource.password=shop
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
spring.jpa.show-sql=true

# JWT
security.jwt.secret=<BASE64-256bit>
security.jwt.expiration-ms=86400000

# Swagger
springdoc.swagger-ui.path=/swagger-ui.html
```
> Em produção, **não** use `ddl-auto=update`; prefira migrações (Flyway/Liquibase). O `secret` deve ser forte e mantido fora do repositório (env var).

## 9. Exemplos cURL
```bash
# Login (admin)
curl -s -X POST http://localhost:8080/api/auth/login   -H "Content-Type: application/json"   -d '{"username":"admin","password":"admin123"}'

# Listar produtos
curl -s http://localhost:8080/api/products

# Criar produto (exige Bearer)
curl -s -X POST http://localhost:8080/api/products   -H "Content-Type: application/json"   -H "Authorization: Bearer <JWT>"   -d '{"name":"Mouse","description":"Ergonomic","price":199.00}'

# Criar pedido (autenticado)
curl -s -X POST http://localhost:8080/api/orders   -H "Content-Type: application/json"   -H "Authorization: Bearer <JWT>"   -d '{"items":[{"productId":1,"quantity":2}]}'
```

## 10. Seeds (primeiro boot)
Criados automaticamente se tabelas vazias:
- Usuários: `admin/admin123`, `operator/operator123`, `client/client123`
- Produtos: `Laptop`, `Mouse`, `Keyboard`

## 11. Tratamento de Erros (padrões)
- 400 — validação/entrada inválida (ex.: produto inexistente ao montar pedido).
- 401 — token ausente/inválido/expirado.
- 403 — usuário autenticado sem role suficiente.
- 404 — recurso não encontrado.
- 500 — erro interno inesperado.

> Para padronizar payloads de erro (RFC 7807 / Problem Details), sugere-se um `@ControllerAdvice` futuro.

## 12. Testes
- (Não inclusos no esqueleto). Sugestão:
  - **Unit**: JUnit 5 + Mockito.
  - **Integração**: Spring Boot Test com **Testcontainers** (PostgreSQL).

## 13. Roadmap de Melhorias
- Migrations (Flyway/Liquibase)
- Paginação/ordenação em listagens
- Filtros de produtos
- Refresh token / logout
- CORS configurável por ambiente
- Auditoria (createdBy/updatedBy) e soft delete
- Observabilidade (logs estruturados, tracing)

## 14. Estrutura de Diretórios
```
shopapi/
 ├─ pom.xml
 ├─ build-run.ps1
 └─ src/
     ├─ main/java/com/example/shopapi/...
     └─ main/resources/
         ├─ application.properties
         └─ application-local.properties
```
