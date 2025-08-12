# Shop API (Spring Boot 3 + Java 17 + Postgres + JWT)

API simples de e-commerce com autenticação JWT.

## Requisitos
- JDK 17
- PostgreSQL (local)
- Maven (ou use o Maven Wrapper: gere com `mvn -N wrapper:wrapper -Dmaven=3.9.9 -Dtype=bin`)

## Banco de dados
Crie o banco/usuário (ou ajuste `application-local.properties`):
```
CREATE ROLE shop WITH LOGIN PASSWORD 'shop';
CREATE DATABASE shopdb OWNER shop;
```

## Rodando em DEV (Windows)
```powershell
# no diretório do projeto (onde está o pom.xml)
mvn clean package -DskipTests
java -jar target/shopapi-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

Ou, se tiver gerado o wrapper:
```powershell
.\mvnw.cmd clean package -DskipTests
java -jar target/shopapi-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

Swagger UI: http://localhost:8080/swagger-ui/index.html

## Autenticação
- Registro: `POST /api/auth/register` body `{ "username": "...", "password": "..." }`
- Login: `POST /api/auth/login` → retorna `{ token, role }`

Use o token no header: `Authorization: Bearer <token>`.

Usuários seed criados no primeiro boot:
- `admin/admin123` (ROLE_ADMIN)
- `operator/operator123` (ROLE_OPERATOR)
- `client/client123` (ROLE_CLIENT)

## Produtos
- `GET /api/products`
- `GET /api/products/{id}`
- `POST /api/products` (ADMIN/OPERATOR)
- `PUT /api/products/{id}` (ADMIN/OPERATOR)
- `DELETE /api/products/{id}` (ADMIN/OPERATOR)

## Pedidos
- `POST /api/orders` (autenticado) — body:
```json
{
  "items": [
    {"productId": 1, "quantity": 2},
    {"productId": 2, "quantity": 1}
  ]
}
```
- `GET /api/orders` (autenticado)
```
