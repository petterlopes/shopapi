package com.guarani.shopapi.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.guarani.shopapi.dto.LoginRequest;
import com.guarani.shopapi.dto.LoginResponse;
import com.guarani.shopapi.dto.RegisterRequest;
import com.guarani.shopapi.model.Role;
import com.guarani.shopapi.model.User;
import com.guarani.shopapi.repository.IUserRepository;
import com.guarani.shopapi.security.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final IUserRepository userRepo;
    private final PasswordEncoder encoder;

    public AuthController(AuthenticationManager am, JwtUtil jwtUtil, IUserRepository ur, PasswordEncoder enc) {
        this.authManager = am;
        this.jwtUtil = jwtUtil;
        this.userRepo = ur;
        this.encoder = enc;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepo.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body("Usuário já existe");
        }
        User u = new User(req.getUsername(), encoder.encode(req.getPassword()), Role.CLIENT);
        userRepo.save(u);
        String token = jwtUtil.generateToken(u.getUsername(), u.getRole().name());
        return ResponseEntity.ok(new LoginResponse(token, u.getRole().name()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        User u = userRepo.findByUsername(req.getUsername()).orElseThrow();
        String token = jwtUtil.generateToken(u.getUsername(), u.getRole().name());
        return ResponseEntity.ok(new LoginResponse(token, u.getRole().name()));
    }
}
