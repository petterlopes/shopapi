# Shop API (Spring Boot 3 · Java 17 · JWT · Postgres)

API para gestão de **produtos**, **pedidos** e **pagamentos**, com autenticação **JWT**, autorização por **roles**, documentação **OpenAPI** e execução local no Windows **sem Docker**.

- **Stack**: Spring Boot 3.3.x, Spring Security 6, Spring Data JPA, PostgreSQL, Jackson, JJWT.
- **Pacote raiz**: `com.guarani.shopapi`
- **Porta padrão**: `8080`

---

## Requisitos

- **JDK 17**
- **PostgreSQL 13+** rodando localmente
- **Maven Wrapper** (já incluso: `mvnw`, `mvnw.cmd`, `.mvn/`)

> Mantenha o **wrapper** versionado para builds reprodutíveis em qualquer máquina.

---

## Configuração do Banco (Postgres)

Crie um banco de dados e um usuário (exemplo):

```sql
CREATE DATABASE shopdb;
CREATE USER shop WITH ENCRYPTED PASSWORD 'shop';
GRANT ALL PRIVILEGES ON DATABASE shopdb TO shop;
```

---

## Perfis e propriedades

O projeto utiliza **perfis** do Spring. Para desenvolvimento local use o perfil **`local`**.

Crie o arquivo `src/main/resources/application-local.yml` (ou `.properties`) com suas credenciais:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/shopdb
    username: shop
    password: shop
  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: update   # dev: update | prod: validate
  jackson:
    serialization:
      WRITE_DATES_AS_TIMESTAMPS: false

# JWT (altere esse segredo para algo forte em prod)
app:
  jwt:
    secret: change-me-super-secret-256
    expiration: 3600000    # 1h em milissegundos
```

> Caso já exista `application.yml`, apenas garanta que o **profile** correto será ativado ao executar.

---

## Como executar (Windows, sem Docker)

### Pela linha de comando

```powershell
# compilar e empacotar (sem executar testes)
.\mvnw.cmd clean package -DskipTests

# executar com o perfil local (PostgreSQL)
java -jar target\shopapi-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

### Pelo VS Code (debug)

1. Instale o **Extension Pack for Java**.  
2. Abra a classe `ShopApiApplication`.  
3. Clique em **Run** / **Debug**.  
4. Adicione nos argumentos: `--spring.profiles.active=local`.

---

## Usuários e perfis (roles)

Para desenvolvimento você pode manter usuários seedados (exemplo):

- **admin / admin** → `ROLE_ADMIN`
- **client / client** → `ROLE_USER`

> Ajuste o carregamento de usuários conforme sua implementação (ex.: `data.sql` ou `CommandLineRunner`).

**Autorização por role** (exemplos):

- `ROLE_ADMIN` / `ROLE_OPERATOR`: CRUD de produtos; aprovar pagamentos.
- `ROLE_USER`: operações do cliente (criar pagamento do próprio pedido, listar seus pedidos etc.).

---

## Autenticação (JWT)

### Login
`POST /api/auth/login`

```json
{
  "username": "admin",
  "password": "admin"
}
```

**Resposta (exemplo):**
```json
{ "token": "<JWT>" }
```

Envie o token nas chamadas protegidas:

```
Authorization: Bearer <JWT>
```

> No parse do token, garanta que as **authorities** tenham o prefixo `ROLE_` (ex.: `ROLE_ADMIN`) para que `@PreAuthorize("hasRole('ADMIN')")` funcione.

---

## Endpoints principais

### Produtos

- `GET /api/products` — público
- `GET /api/products/search` — público; filtros: `name`, `category`, `minPrice`, `maxPrice`, `page`, `size`, `sort`
- `POST /api/products` — **ADMIN/OPERATOR**
- `PUT /api/products/{id}` — **ADMIN/OPERATOR**
- `DELETE /api/products/{id}` — **ADMIN/OPERATOR**

**Modelo (exemplo):**
```json
{
  "name": "Notebook Pro 14",
  "description": "i7/16GB/512GB",
  "category": "eletronicos",
  "price": 7999.90
}
```

### Pedidos

- `GET /api/orders` — **ADMIN** (todos) | **USER** (próprios), com filtros `status`, `from`, `to`, `minTotal`, `maxTotal`, além de paginação/sort.
- `GET /api/orders/{id}` — dono (USER) ou ADMIN
- `PUT /api/orders/{id}` — dono (USER): atualiza itens/valores (o serviço **recalcula** subtotal/total)
- `PATCH /api/orders/{id}/status` — **ADMIN**
- `DELETE /api/orders/{id}` — **ADMIN**

### Pagamentos

- `POST /api/orders/{orderId}/payments` — **USER** (cria pagamento do próprio pedido)  
  Body:
  ```json
  { "method": "CREDIT_CARD" }   // ou PIX, etc.
  ```
- `POST /api/orders/{orderId}/payments/{paymentId}/approve` — **ADMIN/OPERATOR**  
  Ao aprovar, o pedido passa para `PAID` (conforme regra de negócios).

---

## Regras de negócio (implementadas)

- **Recalcular totais** do pedido sempre que itens/valores mudarem.
- **Total ≥ 0** (desconto não pode exceder o subtotal).
- **Filtro de pedidos** por `status`, intervalo de datas (`from/to`) e faixa de total (`minTotal/maxTotal`) com paginação/sort.
- **Pagamentos**: cliente cria; **admin/operator** aprova; pedido muda para `PAID`.
- **Autorização por role** com `@PreAuthorize`.

> Se houver regras adicionais no documento de requisitos, me avise que eu descrevo e ligo aos endpoints/serviços.

---

## Documentação da API

- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`  
- **OpenAPI (JSON)**: `http://localhost:8080/v3/api-docs`

---

## Coleção do Insomnia

Coleção pronta para importação (com variáveis e encadeamento de token):

- **ShopAPI-insomnia.json** — contém login de **admin** e **client**, rotas protegidas separadas por perfil, exemplos de corpo e variáveis.
  - Faça login primeiro para popular o token.

**Dicas Insomnia:**
- No header, use o tag **Response → Body Attribute** para puxar `token` da resposta do login — evite o modo “External Vault”.
- Exemplo de header:
  ```
  Authorization: Bearer {% response 'body', '<ID_DO_REQUEST_DE_LOGIN>', 'json', 'token' %}
  ```

---

## Estrutura de pastas (resumo)

```
src/
  main/
    java/com/guarani/shopapi/...
    resources/
      application.yml
      application-local.yml      # (não versionar em repositório público)
.mvn/wrapper/                    # Maven Wrapper (versione tudo)
mvnw / mvnw.cmd
pom.xml
```

---

## Limpeza para versionamento

```bash
mvn clean
```

`.gitignore` sugerido (trecho):
```
target/
*.log
.idea/ *.iml
.vscode/
.DS_Store
Thumbs.db
.env*
src/main/resources/application-local.*
```

`.gitattributes` sugerido:
```
* text=auto
*.sh  text eol=lf
mvnw  text eol=lf
*.cmd text eol=crlf
*.bat text eol=crlf
*.ps1 text eol=crlf
```

---

## Build para produção

```bash
# gera o fat-jar
mvn clean package

# executar (ajuste as variáveis/props de prod)
java -jar target/shopapi-0.0.1-SNAPSHOT.jar   --spring.profiles.active=prod   --app.jwt.secret=<seu-secret>   --spring.datasource.url=jdbc:postgresql://<host>:5432/<db>   --spring.datasource.username=<user>   --spring.datasource.password=<pass>
```

---

## Licença

Uso livre para fins de estudo e avaliação.
