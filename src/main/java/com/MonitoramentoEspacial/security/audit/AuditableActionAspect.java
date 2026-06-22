package com.MonitoramentoEspacial.security.audit;

import com.MonitoramentoEspacial.aplicacao.dominio.TipoAuditoria;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class AuditableActionAspect {

    private final AuditService auditService;

    public AuditableActionAspect(AuditService auditService) {
        this.auditService = auditService;
    }

    @Around("@annotation(auditableAction)")
    public Object audit(ProceedingJoinPoint joinPoint, AuditableAction auditableAction) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        HttpServletRequest request = obterRequestAtual();
        String detalhe = "Metodo: " + joinPoint.getSignature().toShortString();

        try {
            Object result = joinPoint.proceed();
            auditService.registrar(TipoAuditoria.ACAO_SENSIVEL, auditableAction.value(), detalhe, authentication, request, true);
            return result;
        } catch (Throwable throwable) {
            auditService.registrar(TipoAuditoria.ACAO_SENSIVEL, auditableAction.value(), detalhe + " | Falha: " + throwable.getMessage(), authentication, request, false);
            throw throwable;
        }
    }

    private HttpServletRequest obterRequestAtual() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
