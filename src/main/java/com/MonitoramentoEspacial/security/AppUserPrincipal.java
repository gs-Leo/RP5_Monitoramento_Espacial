package com.MonitoramentoEspacial.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class AppUserPrincipal implements UserDetails {

    private final Long userId;
    private final Long operadorMissaoId;
    private final String username;
    private final String password;
    private final boolean enabled;
    private final Collection<? extends GrantedAuthority> authorities;

    public AppUserPrincipal(
            Long userId,
            Long operadorMissaoId,
            String username,
            String password,
            boolean enabled,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.userId = userId;
        this.operadorMissaoId = operadorMissaoId;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getOperadorMissaoId() {
        return operadorMissaoId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
