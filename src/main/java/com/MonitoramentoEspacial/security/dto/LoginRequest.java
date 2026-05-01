package com.MonitoramentoEspacial.security.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Usuario e obrigatorio")
        String username,
        @NotBlank(message = "Senha e obrigatoria")
        String senha
) {
}
