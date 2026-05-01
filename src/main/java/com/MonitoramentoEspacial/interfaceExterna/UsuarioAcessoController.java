package com.MonitoramentoEspacial.interfaceExterna;

import com.MonitoramentoEspacial.aplicacao.UsuarioAcessoServiceInterface;
import com.MonitoramentoEspacial.security.audit.AuditableAction;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/usuarios-acesso")
public class UsuarioAcessoController {

    private final UsuarioAcessoServiceInterface usuarioAcessoService;

    public UsuarioAcessoController(UsuarioAcessoServiceInterface usuarioAcessoService) {
        this.usuarioAcessoService = usuarioAcessoService;
    }

    @PostMapping
    @AuditableAction("Criacao de usuario de acesso")
    public ResponseEntity<UsuarioAcessoDTO> criar(@Valid @RequestBody CriarUsuarioAcessoRequest request) {
        UsuarioAcessoDTO dto = usuarioAcessoService.criarUsuario(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(dto.getId())
                .toUri();
        return ResponseEntity.created(location).body(dto);
    }

    @PutMapping("/{id}")
    @AuditableAction("Atualizacao de usuario de acesso")
    public ResponseEntity<UsuarioAcessoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarUsuarioAcessoRequest request) {
        return ResponseEntity.ok(usuarioAcessoService.atualizarUsuario(id, request));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioAcessoDTO>> listar() {
        return ResponseEntity.ok(usuarioAcessoService.listarUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioAcessoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioAcessoService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    @AuditableAction("Exclusao de usuario de acesso")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        usuarioAcessoService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
