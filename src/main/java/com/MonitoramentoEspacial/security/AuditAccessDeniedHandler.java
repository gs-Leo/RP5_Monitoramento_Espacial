package com.MonitoramentoEspacial.security;

import com.MonitoramentoEspacial.aplicacao.dominio.TipoAuditoria;
import com.MonitoramentoEspacial.interfaceExterna.ApiErrorResponse;
import com.MonitoramentoEspacial.security.audit.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class AuditAccessDeniedHandler implements AccessDeniedHandler {

    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public AuditAccessDeniedHandler(AuditService auditService, ObjectMapper objectMapper) {
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        auditService.registrar(TipoAuditoria.ACESSO_NEGADO, "Acesso negado", accessDeniedException.getMessage(), authentication, request, false);

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), new ApiErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Acesso Negado",
                "Voce nao possui permissao para executar esta operacao.",
                Instant.now()
        ));
    }
}
