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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;

@Component
public class AuditAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final AuditService auditService;
    private final ObjectMapper objectMapper;

    public AuditAuthenticationEntryPoint(AuditService auditService, ObjectMapper objectMapper) {
        this.auditService = auditService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        auditService.registrar(TipoAuditoria.ACESSO_NAO_AUTENTICADO, "Acesso sem autenticacao", authException.getMessage(), null, request, false);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), new ApiErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Nao Autenticado",
                "Faca login para acessar este recurso.",
                Instant.now()
        ));
    }
}
