package com.MonitoramentoEspacial.interfaceExterna;

import com.MonitoramentoEspacial.aplicacao.dominio.TipoAuditoria;

import java.time.LocalDateTime;

public record RegistroAuditoriaDTO(
        Long id,
        TipoAuditoria tipo,
        String evento,
        String username,
        String role,
        String metodoHttp,
        String caminho,
        String detalhe,
        boolean sucesso,
        LocalDateTime criadoEm
) {
}
