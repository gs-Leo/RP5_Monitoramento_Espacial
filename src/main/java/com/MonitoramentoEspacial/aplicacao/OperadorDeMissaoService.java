package com.MonitoramentoEspacial.aplicacao;

import com.MonitoramentoEspacial.aplicacao.dominio.OperadorDeMissao;
import com.MonitoramentoEspacial.interfaceExterna.CriarOperadorRequest;
import com.MonitoramentoEspacial.interfaceExterna.OperadorDeMissaoDTO;
import com.MonitoramentoEspacial.middleware.OperadorDeMissaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service("realOperadorService")
public class OperadorDeMissaoService implements OperadorDeMissaoServiceInterface {

    private static final Logger log = LoggerFactory.getLogger(OperadorDeMissaoService.class);

    private final OperadorDeMissaoRepository repository;
    private final OperadorDeMissaoMapper operadorMapper;

    public OperadorDeMissaoService(OperadorDeMissaoRepository repository, OperadorDeMissaoMapper operadorMapper) {
        this.repository = repository;
        this.operadorMapper = operadorMapper;
    }

    @Override
    @Transactional
    public OperadorDeMissaoDTO criarOperador(CriarOperadorRequest request) {
        log.info("Criando novo operador: {}", request.getNome());
        OperadorDeMissao operador = operadorMapper.toEntity(request);
        OperadorDeMissao salvo = repository.save(operador);
        return operadorMapper.toDTO(salvo);
    }

    @Override
    @Transactional
    public OperadorDeMissaoDTO atualizarOperador(Long id, CriarOperadorRequest request) {
        OperadorDeMissao operador = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Operador nao encontrado com ID: " + id));

        operador.setNome(request.getNome());
        operador.setIdade(request.getIdade());
        operador.setAtivo(Boolean.TRUE.equals(request.getAtivo()));
        operador.setTurno(request.getTurno());
        operador.setAreaEspecializacao(request.getAreaEspecializacao());

        OperadorDeMissao salvo = repository.save(operador);
        return operadorMapper.toDTO(salvo);
    }

    @Override
    @Transactional(readOnly = true)
    public OperadorDeMissaoDTO buscarPorId(Long id) {
        OperadorDeMissao operador = repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Operador nao encontrado com ID: " + id));
        return operadorMapper.toDTO(operador);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OperadorDeMissaoDTO> listarOperadores(String nome) {
        List<OperadorDeMissao> operadores;
        if (nome != null && !nome.isBlank()) {
            operadores = repository.findByNomeContainingIgnoreCase(nome);
        } else {
            operadores = repository.findAll();
        }
        return operadores.stream()
                .map(operadorMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletarOperador(Long id) {
        log.info("Tentando deletar operador ID: {}", id);
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Operador nao encontrado para exclusao (ID: " + id + ")");
        }
        repository.deleteById(id);
        log.info("Operador ID: {} deletado com sucesso", id);
    }
}
