package com.guarani.shopapi.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.guarani.shopapi.model.Role;
import com.guarani.shopapi.model.User;
import com.guarani.shopapi.repository.IUserRepository;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private final IUserRepository repo;
    private final PasswordEncoder encoder;

    public AdminBootstrap(IUserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) throws Exception {
        repo.findByUsername("admin").orElseGet(() -> {
            User u = new User();
            u.setUsername("admin");
            u.setPassword(encoder.encode("admin123"));
            u.setRole(Role.ADMIN);
            return repo.save(u);
        });
    }
}