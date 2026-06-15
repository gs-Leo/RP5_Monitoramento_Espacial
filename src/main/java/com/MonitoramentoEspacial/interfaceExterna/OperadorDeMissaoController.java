package com.MonitoramentoEspacial.interfaceExterna;

import com.MonitoramentoEspacial.aplicacao.OperadorDeMissaoServiceInterface;
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
@RequestMapping("/operadores")
@Tag(name = "Operadores")
@SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
public class OperadorDeMissaoController {

    @Autowired
    private OperadorDeMissaoServiceInterface operadorService;

    @PostMapping
    @AuditableAction("Criacao de operador")
    @Operation(summary = "Cria um operador de missao", description = "Requer perfil ADMIN.")
    public ResponseEntity<OperadorDeMissaoDTO> criar(@Valid @RequestBody CriarOperadorRequest request) {
        OperadorDeMissaoDTO dto = operadorService.criarOperador(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();
        return ResponseEntity.created(location).body(dto);
    }

    @PutMapping("/{id}")
    @AuditableAction("Atualizacao de operador")
    @Operation(summary = "Atualiza um operador de missao", description = "Requer perfil ADMIN.")
    public ResponseEntity<OperadorDeMissaoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody CriarOperadorRequest request) {
        return ResponseEntity.ok(operadorService.atualizarOperador(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca operador de missao por identificador")
    public ResponseEntity<OperadorDeMissaoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(operadorService.buscarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Lista operadores de missao", description = "Permite filtrar por nome.")
    public ResponseEntity<List<OperadorDeMissaoDTO>> listar(@RequestParam(required = false) String nome) {
        return ResponseEntity.ok(operadorService.listarOperadores(nome));
    }

    @DeleteMapping("/{id}")
    @AuditableAction("Exclusao de operador")
    @Operation(summary = "Remove um operador de missao", description = "Requer perfil ADMIN.")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        operadorService.deletarOperador(id);
        return ResponseEntity.noContent().build();
    }
}
