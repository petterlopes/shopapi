package com.peritumct.shopapi.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cfg = new CorsConfiguration();

    // Origens de dev (Vite e cia)
    cfg.setAllowedOriginPatterns(Arrays.asList(
        "http://localhost:*", "http://127.0.0.1:*"
    ));

    // Métodos e headers amplos (inclui o que o browser pedir)
    cfg.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
    cfg.setAllowedHeaders(Arrays.asList("*"));
    // Se você precisar ler Authorization no client (não é necessário p/ login, mas útil)
    cfg.setExposedHeaders(Arrays.asList("Authorization","Location"));

    // Se usar cookies/sessão (não é o caso aqui, mas não atrapalha)
    cfg.setAllowCredentials(true);

    cfg.setMaxAge(3600L); // cacheia o preflight

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
  }
}
