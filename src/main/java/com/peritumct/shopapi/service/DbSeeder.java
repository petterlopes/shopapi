package com.peritumct.shopapi.service;

import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.peritumct.shopapi.model.Product;
import com.peritumct.shopapi.model.Role;
import com.peritumct.shopapi.model.User;
import com.peritumct.shopapi.repository.IProductRepository;
import com.peritumct.shopapi.repository.IUserRepository;

@Configuration
public class DbSeeder {

    @Bean
    CommandLineRunner seed(IUserRepository users, IProductRepository products, PasswordEncoder encoder) {
        return args -> {
            if (users.count() == 0) {
                users.save(new User("admin", encoder.encode("admin123"), Role.ADMIN));
                users.save(new User("operator", encoder.encode("operator123"), Role.OPERATOR));
                users.save(new User("client", encoder.encode("client123"), Role.USER));
            }
            if (products.count() == 0) {
                products.save(new Product("Notebook", "Inspiron 16GB", new BigDecimal("7999.90")));
                products.save(new Product("Mouse", "Sem fio", new BigDecimal("199.00")));
                products.save(new Product("Teclado", "Mecânico", new BigDecimal("499.00")));
            }
        };
    }
}
