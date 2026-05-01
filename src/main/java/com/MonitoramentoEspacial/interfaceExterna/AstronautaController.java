package com.MonitoramentoEspacial.interfaceExterna;

import com.MonitoramentoEspacial.aplicacao.AstronautaServiceInterface;
import com.MonitoramentoEspacial.security.audit.AuditableAction;
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
public class AstronautaController {

    @Autowired
    private AstronautaServiceInterface astronautaService;

    @PostMapping
    @AuditableAction("Criacao de astronauta")
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
    public ResponseEntity<AstronautaDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(astronautaService.buscarPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<AstronautaDTO>> listarTodos(@RequestParam(required = false) String nome) {
        return ResponseEntity.ok(astronautaService.listarAstronautas(nome));
    }

    @PutMapping("/{id}")
    @AuditableAction("Atualizacao de astronauta")
    public ResponseEntity<AstronautaDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody AtualizaAstronautaRequest request
    ) {
        return ResponseEntity.ok(astronautaService.atualizarAstronauta(id, request));
    }

    @DeleteMapping("/{id}")
    @AuditableAction("Exclusao de astronauta")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        astronautaService.deletarAstronauta(id);
        return ResponseEntity.noContent().build();
    }
}
