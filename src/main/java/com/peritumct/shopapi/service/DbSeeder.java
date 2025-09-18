package com.peritumct.shopapi.service;

import com.peritumct.shopapi.domain.product.Product;
import com.peritumct.shopapi.domain.product.port.ProductRepository;
import com.peritumct.shopapi.domain.user.Role;
import com.peritumct.shopapi.domain.user.User;
import com.peritumct.shopapi.domain.user.port.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

@Configuration
public class DbSeeder {

    @Bean
    CommandLineRunner seed(UserRepository users, ProductRepository products, PasswordEncoder encoder) {
        return args -> {
            if (users.findByUsername("admin").isEmpty()) {
                users.save(new User(null, "admin", encoder.encode("admin123"), Role.ADMIN));
                users.save(new User(null, "operator", encoder.encode("operator123"), Role.OPERATOR));
                users.save(new User(null, "client", encoder.encode("client123"), Role.USER));
            }
            if (products.findAll().isEmpty()) {
                products.save(new Product(null, "Notebook", "Inspiron 16GB", null, new BigDecimal("7999.90")));
                products.save(new Product(null, "Mouse", "Sem fio", null, new BigDecimal("199.00")));
                products.save(new Product(null, "Teclado", "Mecanico", null, new BigDecimal("499.00")));
            }
        };
    }
}
