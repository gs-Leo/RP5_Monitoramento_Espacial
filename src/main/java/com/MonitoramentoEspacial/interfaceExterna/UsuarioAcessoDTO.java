package com.MonitoramentoEspacial.interfaceExterna;

public class UsuarioAcessoDTO {
    private Long id;
    private String username;
    private String role;
    private boolean ativo;
    private Long operadorMissaoId;
    private String operadorMissaoNome;

    public UsuarioAcessoDTO(Long id, String username, String role, boolean ativo, Long operadorMissaoId, String operadorMissaoNome) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.ativo = ativo;
        this.operadorMissaoId = operadorMissaoId;
        this.operadorMissaoNome = operadorMissaoNome;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Long getOperadorMissaoId() {
        return operadorMissaoId;
    }

    public String getOperadorMissaoNome() {
        return operadorMissaoNome;
    }
}
