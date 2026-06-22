package com.MonitoramentoEspacial.interfaceExterna;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AtualizarUsuarioAcessoRequest {

    @NotBlank(message = "Perfil e obrigatorio")
    private String role;

    @NotNull(message = "Status ativo e obrigatorio")
    private Boolean ativo;

    @Size(min = 6, max = 120, message = "Senha deve ter entre 6 e 120 caracteres")
    private String novaSenha;

    private Long operadorMissaoId;

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

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }

    public Long getOperadorMissaoId() {
        return operadorMissaoId;
    }

    public void setOperadorMissaoId(Long operadorMissaoId) {
        this.operadorMissaoId = operadorMissaoId;
    }
}
