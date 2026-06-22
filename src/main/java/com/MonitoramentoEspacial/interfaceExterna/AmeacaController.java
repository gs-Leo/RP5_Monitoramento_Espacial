package com.MonitoramentoEspacial.interfaceExterna;

import com.MonitoramentoEspacial.aplicacao.AmeacaServiceInterface;
import com.MonitoramentoEspacial.config.OpenApiConfig;
import com.MonitoramentoEspacial.security.audit.AuditableAction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/ameacas")
@Tag(name = "Ameacas")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class AmeacaController {

    @Autowired
    private AmeacaServiceInterface ameacaService;

    @PostMapping
    @AuditableAction("Registro de ameaca")
    @Operation(summary = "Registra uma ameaca", description = "Requer perfil ADMIN ou OPERADOR.")
    public ResponseEntity<AmeacaDTO> registrar(@Valid @RequestBody RegistrarAmeacaRequest request) {
        AmeacaDTO dto = ameacaService.registrarAmeaca(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.id())
                .toUri();
        return ResponseEntity.created(location).body(dto);
    }

    @GetMapping("/missao/{missaoId}")
    @Operation(summary = "Lista ameacas de uma missao")
    public ResponseEntity<List<AmeacaDTO>> listarPorMissao(@PathVariable Long missaoId) {
        return ResponseEntity.ok(ameacaService.listarPorMissao(missaoId));
    }

    @GetMapping("/criticas")
    @Operation(summary = "Lista ameacas criticas")
    public ResponseEntity<List<AmeacaDTO>> listarCriticas() {
        return ResponseEntity.ok(ameacaService.listarAmeacasCriticas());
    }
}
