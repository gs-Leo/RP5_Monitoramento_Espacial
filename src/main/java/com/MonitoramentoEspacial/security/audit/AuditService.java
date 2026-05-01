package com.MonitoramentoEspacial.security.audit;

import com.MonitoramentoEspacial.aplicacao.dominio.RegistroAuditoria;
import com.MonitoramentoEspacial.aplicacao.dominio.TipoAuditoria;
import com.MonitoramentoEspacial.middleware.RegistroAuditoriaRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AuditService {

    private final RegistroAuditoriaRepository registroAuditoriaRepository;

    public AuditService(RegistroAuditoriaRepository registroAuditoriaRepository) {
        this.registroAuditoriaRepository = registroAuditoriaRepository;
    }

    public void registrar(TipoAuditoria tipo, String evento, String detalhe, Authentication authentication, HttpServletRequest request, boolean sucesso) {
        RegistroAuditoria registro = criarBase(tipo, evento, detalhe, request, sucesso);

        if (authentication != null) {
            registro.setUsername(authentication.getName());
            registro.setRole(extrairRole(authentication.getAuthorities()));
        } else {
            registro.setUsername("ANONIMO");
        }

        registroAuditoriaRepository.save(registro);
    }

    public void registrar(TipoAuditoria tipo, String evento, String detalhe, String username, String role, HttpServletRequest request, boolean sucesso) {
        RegistroAuditoria registro = criarBase(tipo, evento, detalhe, request, sucesso);
        registro.setUsername(username == null || username.isBlank() ? "ANONIMO" : username);
        registro.setRole(role);
        registroAuditoriaRepository.save(registro);
    }

    private RegistroAuditoria criarBase(TipoAuditoria tipo, String evento, String detalhe, HttpServletRequest request, boolean sucesso) {
        RegistroAuditoria registro = new RegistroAuditoria();
        registro.setTipo(tipo);
        registro.setEvento(evento);
        registro.setDetalhe(detalhe);
        registro.setSucesso(sucesso);

        if (request != null) {
            registro.setMetodoHttp(request.getMethod());
            registro.setCaminho(request.getRequestURI());
        }

        return registro;
    }

    private String extrairRole(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(null);
    }
}
