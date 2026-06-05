package com.shoestore.security.user;

import com.shoestore.common.enums.user.UserStatus;
import com.shoestore.entity.Role;
import com.shoestore.entity.User;

import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class UserPrincipal implements UserDetails {

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        // 1. Nạp các Role (Ví dụ: ROLE_ADMIN, ROLE_STAFF)
        if (user.getRoles() != null) {
            user.getRoles().forEach(role ->
                    authorities.add(new SimpleGrantedAuthority(role.getName().name()))
            );

            // 2. Nạp toàn bộ các Permission chi tiết thuộc về các Role đó
            user.getRoles().stream()
                    .filter(role -> role.getPermissions() != null) // Tránh NullPointerException
                    .flatMap(role -> role.getPermissions().stream())
                    .forEach(permission ->
                            authorities.add(new SimpleGrantedAuthority(permission.getName().name()))
                    );
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() == UserStatus.ACTIVE;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !Boolean.TRUE.equals(
                user.getAccountLocked()
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}