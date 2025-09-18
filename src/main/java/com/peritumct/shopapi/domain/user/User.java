package com.peritumct.shopapi.domain.user;

import java.util.Objects;

public class User {
    private final Long id;
    private final String username;
    private final String password;
    private final Role role;

    public User(Long id, String username, String password, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public User withPassword(String encodedPassword) {
        return new User(id, username, encodedPassword, role);
    }

    public User withRole(Role newRole) {
        return new User(id, username, password, newRole);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
