package com.MonitoramentoEspacial.interfaceExterna;

import com.MonitoramentoEspacial.aplicacao.AstronautaServiceInterface;
import com.MonitoramentoEspacial.config.OpenApiConfig;
import com.MonitoramentoEspacial.security.audit.AuditableAction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/astronautas")
@Tag(name = "Astronautas")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class AstronautaController {

    @Autowired
    private AstronautaServiceInterface astronautaService;

    @PostMapping
    @AuditableAction("Criacao de astronauta")
    @Operation(summary = "Cria um astronauta", description = "Requer perfil ADMIN.")
    public ResponseEntity<AstronautaDTO> criar(@Valid @RequestBody CriarAstronautaRequest request) {
        AstronautaDTO astronautaCriado = astronautaService.criarAstronauta(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(astronautaCriado.getId())
                .toUri();

        return ResponseEntity.created(location).body(astronautaCriado);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca astronauta por identificador")
    public ResponseEntity<AstronautaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(astronautaService.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Lista astronautas", description = "Permite filtrar por nome.")
    public ResponseEntity<List<AstronautaDTO>> listarTodos(@RequestParam(required = false) String nome) {
        return ResponseEntity.ok(astronautaService.listarAstronautas(nome));
    }

    @PutMapping("/{id}")
    @AuditableAction("Atualizacao de astronauta")
    @Operation(summary = "Atualiza um astronauta", description = "Requer perfil ADMIN.")
    public ResponseEntity<AstronautaDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizaAstronautaRequest request
    ) {
        return ResponseEntity.ok(astronautaService.atualizarAstronauta(id, request));
    }

    @DeleteMapping("/{id}")
    @AuditableAction("Exclusao de astronauta")
    @Operation(summary = "Remove um astronauta", description = "Requer perfil ADMIN.")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        astronautaService.deletarAstronauta(id);
        return ResponseEntity.noContent().build();
    }
}
