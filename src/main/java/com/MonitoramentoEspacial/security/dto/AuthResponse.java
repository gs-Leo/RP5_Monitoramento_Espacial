package com.MonitoramentoEspacial.security.dto;

public record AuthResponse(
        String token,
        String username,
        String role,
        long expiresAt
) {
}
