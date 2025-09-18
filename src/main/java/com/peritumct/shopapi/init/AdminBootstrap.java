package com.peritumct.shopapi.init;

import com.peritumct.shopapi.domain.user.Role;
import com.peritumct.shopapi.domain.user.User;
import com.peritumct.shopapi.domain.user.port.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public AdminBootstrap(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        userRepository.findByUsername("admin").orElseGet(() -> {
            User admin = new User(null, "admin", encoder.encode("admin123"), Role.ADMIN);
            return userRepository.save(admin);
        });
    }
}
