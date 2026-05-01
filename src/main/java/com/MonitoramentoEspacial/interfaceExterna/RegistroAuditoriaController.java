package com.MonitoramentoEspacial.interfaceExterna;

import com.MonitoramentoEspacial.aplicacao.RegistroAuditoriaService;
import com.MonitoramentoEspacial.security.audit.AuditableAction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auditoria")
public class RegistroAuditoriaController {

    private final RegistroAuditoriaService registroAuditoriaService;

    public RegistroAuditoriaController(RegistroAuditoriaService registroAuditoriaService) {
        this.registroAuditoriaService = registroAuditoriaService;
    }

    @GetMapping
    @AuditableAction("Consulta de trilha de auditoria")
    public ResponseEntity<List<RegistroAuditoriaDTO>> listarRecentes() {
        return ResponseEntity.ok(registroAuditoriaService.listarRecentes());
    }
}
