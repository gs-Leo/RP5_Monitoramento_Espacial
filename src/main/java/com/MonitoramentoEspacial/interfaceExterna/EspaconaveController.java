package com.MonitoramentoEspacial.interfaceExterna;

import com.MonitoramentoEspacial.aplicacao.EspaconaveServiceInterface;
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
@RequestMapping("/espaconaves")
@Tag(name = "Espaconaves")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class EspaconaveController {

    @Autowired
    private EspaconaveServiceInterface espaconaveService;

    @PostMapping
    @AuditableAction("Criacao de espaconave")
    @Operation(summary = "Cria uma espaconave", description = "Requer perfil ADMIN.")
    public ResponseEntity<EspaconaveDTO> criar(@Valid @RequestBody SalvarEspaconaveRequest request) {
        EspaconaveDTO dto = espaconaveService.criarEspaconave(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();
        return ResponseEntity.created(location).body(dto);
    }

    @PutMapping("/{id}")
    @AuditableAction("Atualizacao de espaconave")
    @Operation(summary = "Atualiza uma espaconave", description = "Requer perfil ADMIN.")
    public ResponseEntity<EspaconaveDTO> atualizar(@PathVariable Long id, @Valid @RequestBody SalvarEspaconaveRequest request) {
        EspaconaveDTO dto = espaconaveService.atualizarEspaconave(id, request);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca espaconave por identificador")
    public ResponseEntity<EspaconaveDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(espaconaveService.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Lista espaconaves", description = "Permite filtrar por nome.")
    public ResponseEntity<List<EspaconaveDTO>> listar(@RequestParam(required = false) String nome) {
        return ResponseEntity.ok(espaconaveService.listarEspaconaves(nome));
    }

    @DeleteMapping("/{id}")
    @AuditableAction("Exclusao de espaconave")
    @Operation(summary = "Remove uma espaconave", description = "Requer perfil ADMIN.")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        espaconaveService.deletarEspaconave(id);
        return ResponseEntity.noContent().build();
    }
}
