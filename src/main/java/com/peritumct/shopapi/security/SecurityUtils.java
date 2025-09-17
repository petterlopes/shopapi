package com.peritumct.shopapi.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import com.peritumct.shopapi.model.Role;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static boolean hasAnyRole(Authentication authentication, Role... roles) {
        if (authentication == null) {
            return false;
        }
        return hasAnyRole(authentication.getAuthorities(), roles);
    }

    public static boolean hasAnyRole(Collection<? extends GrantedAuthority> authorities, Role... roles) {
        if (authorities == null || roles == null || roles.length == 0) {
            return false;
        }
        Set<String> authoritySet = AuthorityUtils.authorityListToSet(authorities);
        for (Role role : roles) {
            if (authoritySet.contains(asAuthority(role))) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasRole(Authentication authentication, Role role) {
        if (authentication == null || role == null) {
            return false;
        }
        return hasAnyRole(authentication.getAuthorities(), role);
    }

    public static boolean isSameUser(Authentication authentication, String username) {
        if (authentication == null || username == null) {
            return false;
        }
        return Objects.equals(authentication.getName(), username);
    }

    public static boolean isSelfOrHasAnyRole(Authentication authentication, String username, Role... roles) {
        return isSameUser(authentication, username) || hasAnyRole(authentication, roles);
    }

    private static String asAuthority(Role role) {
        return "ROLE_" + role.name();
    }
}
