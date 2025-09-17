package com.peritumct.shopapi.security;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.peritumct.shopapi.model.Role;
import com.peritumct.shopapi.repository.IUserRepository;

@Service
@Primary
public class DbUserDetailsService implements UserDetailsService {

    private final IUserRepository repo; 

    public DbUserDetailsService(IUserRepository repo) { 
        this.repo = repo;
    }

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        var u = repo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("usuário não encontrado: " + username));

        // Enum -> ROLE_...
        Role role = u.getRole() == null ? Role.USER : u.getRole();
        String authority = "ROLE_" + role.name();

        return new User(u.getUsername(), u.getPassword(),
                List.of(new SimpleGrantedAuthority(authority)));
    }
}
