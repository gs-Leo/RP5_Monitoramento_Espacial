package com.MonitoramentoEspacial.interfaceExterna;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CriarUsuarioAcessoRequest {

    @NotBlank(message = "Username e obrigatorio")
    @Size(min = 3, max = 80, message = "Username deve ter entre 3 e 80 caracteres")
    private String username;

    @NotBlank(message = "Senha e obrigatoria")
    @Size(min = 6, max = 120, message = "Senha deve ter entre 6 e 120 caracteres")
    private String senha;

    @NotBlank(message = "Perfil e obrigatorio")
    private String role;

    @NotNull(message = "Status ativo e obrigatorio")
    private Boolean ativo;

    private Long operadorMissaoId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Long getOperadorMissaoId() {
        return operadorMissaoId;
    }

    public void setOperadorMissaoId(Long operadorMissaoId) {
        this.operadorMissaoId = operadorMissaoId;
    }
}
