package com.guarani.shopapi.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.guarani.shopapi.security.JwtService;

record LoginRequest(String username, String password) {}

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager am, JwtService jwtService) {
        this.authenticationManager = am;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );
        UserDetails user = (UserDetails) auth.getPrincipal();
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(Map.of("token", token));
    }
}