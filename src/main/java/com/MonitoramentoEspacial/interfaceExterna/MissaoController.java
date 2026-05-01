package com.MonitoramentoEspacial.interfaceExterna;

import com.MonitoramentoEspacial.aplicacao.MissaoServiceInterface;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/missoes")
public class MissaoController {

    @Autowired
    private MissaoServiceInterface missaoService;

    @PostMapping
    @AuditableAction("Criacao de missao")
    public ResponseEntity<MissaoDTO> criar(@Valid @RequestBody CriarMissaoRequest request) {
        MissaoDTO missaoCriada = missaoService.criarMissao(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(missaoCriada.getId()).toUri();
        return ResponseEntity.created(location).body(missaoCriada);
    }

    @PutMapping("/{id}")
    @AuditableAction("Atualizacao de missao")
    public ResponseEntity<MissaoDTO> atualizar(@PathVariable Long id, @RequestBody AtualizarMissaoRequest request) {
        return ResponseEntity.ok(missaoService.atualizarMissao(id, request));
    }

    @GetMapping
    public ResponseEntity<List<MissaoDTO>> listarTodas() {
        return ResponseEntity.ok(missaoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MissaoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(missaoService.buscarPorId(id));
    }

    @DeleteMapping("/{id}")
    @AuditableAction("Exclusao de missao")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        missaoService.deletarMissao(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/iniciar-simulacao")
    @AuditableAction("Inicio de simulacao de missao")
    public ResponseEntity<MissaoDTO> iniciarSimulacao(@PathVariable Long id) {
        return ResponseEntity.ok(missaoService.iniciarSimulacao(id));
    }

    @GetMapping("/{id}/eventos")
    public ResponseEntity<List<EventoDTO>> listarEventos(@PathVariable Long id) {
        return ResponseEntity.ok(missaoService.listarEventosPorMissao(id));
    }

    @GetMapping("/{id}/protocolos")
    public ResponseEntity<List<ProtocoloEmergencialDTO>> listarProtocolos(@PathVariable Long id) {
        return ResponseEntity.ok(missaoService.listarProtocolosPorMissao(id));
    }

    @PostMapping("/{id}/protocolos")
    @AuditableAction("Acionamento de protocolo emergencial")
    public ResponseEntity<ProtocoloEmergencialDTO> acionarProtocolo(@PathVariable Long id, @Valid @RequestBody AcionarProtocoloRequest request) {
        return ResponseEntity.status(201).body(missaoService.acionarProtocolo(id, request));
    }

    @PostMapping("/{id}/concluir")
    @AuditableAction("Conclusao de missao")
    public ResponseEntity<MissaoDTO> concluirMissao(@PathVariable Long id) {
        return ResponseEntity.ok(missaoService.concluirMissao(id));
    }
}
