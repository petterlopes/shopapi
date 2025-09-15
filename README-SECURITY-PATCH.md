# ShopAPI — Security Patch Pack (JWT + CORS)

Passos:
1. Copie `src/main/java/com/guarani/shopapi/**` para o seu projeto.
2. Em `application.properties` adicione:
```
app.jwt.secret=OVMjPEeO2/jDqlixTmfbrQUyxbuQYxXcah8r2ZX3zLc=
app.jwt.exp-minutes=120
logging.level.org.springframework.security=DEBUG
logging.level.com.guarani.shopapi=DEBUG
```
3. Garanta no `SecurityConfig`:
```java
http.csrf(csrf->csrf.disable()).cors(cors->{{}})
    .sessionManagement(sess->sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .authorizeHttpRequests(auth->auth
        .requestMatchers("/api/auth/**","/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html").permitAll()
        .anyRequest().authenticated())
    .authenticationProvider(authenticationProvider())
    .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
```
4. `mvn clean package -DskipTests` e rode.
5. Teste login: `POST /api/auth/login` com `{{"username":"admin","password":"admin123"}}`.