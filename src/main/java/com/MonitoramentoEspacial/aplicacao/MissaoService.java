package com.MonitoramentoEspacial.aplicacao;

import com.MonitoramentoEspacial.aplicacao.dominio.Astronauta;
import com.MonitoramentoEspacial.aplicacao.dominio.Espaconave;
import com.MonitoramentoEspacial.aplicacao.dominio.Missao;
import com.MonitoramentoEspacial.aplicacao.dominio.OperadorDeMissao;
import com.MonitoramentoEspacial.aplicacao.dominio.ProtocoloEmergencial;
import com.MonitoramentoEspacial.aplicacao.dominio.StatusMissao;
import com.MonitoramentoEspacial.aplicacao.dominio.UsuarioAcesso;
import com.MonitoramentoEspacial.interfaceExterna.AcionarProtocoloRequest;
import com.MonitoramentoEspacial.interfaceExterna.AtualizarMissaoRequest;
import com.MonitoramentoEspacial.interfaceExterna.CriarMissaoRequest;
import com.MonitoramentoEspacial.interfaceExterna.EventoDTO;
import com.MonitoramentoEspacial.interfaceExterna.MissaoDTO;
import com.MonitoramentoEspacial.interfaceExterna.ProtocoloEmergencialDTO;
import com.MonitoramentoEspacial.middleware.AstronautaRepository;
import com.MonitoramentoEspacial.middleware.EspaconaveRepository;
import com.MonitoramentoEspacial.middleware.EventoRepository;
import com.MonitoramentoEspacial.middleware.MissaoRepository;
import com.MonitoramentoEspacial.middleware.OperadorDeMissaoRepository;
import com.MonitoramentoEspacial.middleware.ProtocoloEmergencialRepository;
import com.MonitoramentoEspacial.middleware.UsuarioAcessoRepository;
import com.MonitoramentoEspacial.security.AppUserPrincipal;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service("realMissaoService")
public class MissaoService implements MissaoServiceInterface {

    private final MissaoRepository missaoRepository;
    private final AstronautaRepository astronautaRepository;
    private final EspaconaveRepository espaconaveRepository;
    private final OperadorDeMissaoRepository operadorDeMissaoRepository;
    private final UsuarioAcessoRepository usuarioAcessoRepository;
    private final EventoRepository eventoRepository;
    private final ProtocoloEmergencialRepository protocoloRepository;
    private final MissaoMapper missaoMapper;
    private final EventoMapper eventoMapper;
    private final ProtocoloEmergencialMapper protocoloMapper;

    public MissaoService(
            MissaoRepository missaoRepository,
            AstronautaRepository astronautaRepository,
            EspaconaveRepository espaconaveRepository,
            OperadorDeMissaoRepository operadorDeMissaoRepository,
            UsuarioAcessoRepository usuarioAcessoRepository,
            EventoRepository eventoRepository,
            ProtocoloEmergencialRepository protocoloRepository,
            MissaoMapper missaoMapper,
            EventoMapper eventoMapper,
            ProtocoloEmergencialMapper protocoloMapper
    ) {
        this.missaoRepository = missaoRepository;
        this.astronautaRepository = astronautaRepository;
        this.espaconaveRepository = espaconaveRepository;
        this.operadorDeMissaoRepository = operadorDeMissaoRepository;
        this.usuarioAcessoRepository = usuarioAcessoRepository;
        this.eventoRepository = eventoRepository;
        this.protocoloRepository = protocoloRepository;
        this.missaoMapper = missaoMapper;
        this.eventoMapper = eventoMapper;
        this.protocoloMapper = protocoloMapper;
    }

    private Missao getMissaoById(Long missaoId) {
        return missaoRepository.findById(missaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Missao nao encontrada com ID: " + missaoId));
    }

    private AppUserPrincipal getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        return principal instanceof AppUserPrincipal appUserPrincipal ? appUserPrincipal : null;
    }

    private UsuarioAcesso getCurrentUser() {
        AppUserPrincipal principal = getCurrentPrincipal();
        if (principal == null) {
            throw new IllegalStateException("Usuario autenticado nao encontrado.");
        }
        return usuarioAcessoRepository.findById(principal.getUserId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario autenticado nao encontrado."));
    }

    private boolean isAdmin() {
        AppUserPrincipal principal = getCurrentPrincipal();
        return principal != null && principal.getAuthorities().stream().anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    private boolean isOperator() {
        AppUserPrincipal principal = getCurrentPrincipal();
        return principal != null && principal.getAuthorities().stream().anyMatch(authority -> "ROLE_OPERADOR".equals(authority.getAuthority()));
    }

    private Long getCurrentOperadorMissaoId() {
        AppUserPrincipal principal = getCurrentPrincipal();
        return principal != null ? principal.getOperadorMissaoId() : null;
    }

    private Missao getAccessibleMissaoById(Long missaoId) {
        if (isAdmin()) {
            return getMissaoById(missaoId);
        }

        if (isOperator()) {
            Long operadorId = getCurrentOperadorMissaoId();
            if (operadorId == null) {
                throw new IllegalStateException("O usuario OPERADOR precisa estar vinculado a um operador de missao.");
            }
            return missaoRepository.findByIdAndOperadorResponsavelId(missaoId, operadorId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Missao nao encontrada ou sem permissao de acesso."));
        }

        return getMissaoById(missaoId);
    }

    private OperadorDeMissao resolveOperadorResponsavel(CriarMissaoRequest request) {
        if (isAdmin()) {
            if (request.getOperadorId() == null) {
                throw new IllegalArgumentException("O operador responsavel e obrigatorio para criar a missao.");
            }
            return operadorDeMissaoRepository.findById(request.getOperadorId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Operador nao encontrado com ID: " + request.getOperadorId()));
        }

        if (isOperator()) {
            UsuarioAcesso usuario = getCurrentUser();
            if (usuario.getOperadorMissao() == null) {
                throw new IllegalStateException("O usuario OPERADOR precisa estar vinculado a um operador de missao.");
            }
            return usuario.getOperadorMissao();
        }

        throw new IllegalStateException("Usuario sem permissao para criar missao.");
    }

    private OperadorDeMissao resolveOperadorResponsavel(AtualizarMissaoRequest request, Missao missaoAtual) {
        if (request.getOperadorId() == null) {
            if (missaoAtual.getOperadorResponsavel() == null) {
                throw new IllegalArgumentException("A missao precisa manter um operador responsavel definido.");
            }
            return missaoAtual.getOperadorResponsavel();
        }
        return operadorDeMissaoRepository.findById(request.getOperadorId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Operador nao encontrado com ID: " + request.getOperadorId()));
    }

    private boolean sameTripulacao(List<Long> requestedIds, Missao missaoAtual) {
        if (requestedIds == null) {
            return true;
        }

        Set<Long> requested = new HashSet<>(requestedIds);
        Set<Long> current = missaoAtual.getTripulacao().stream()
                .map(Astronauta::getId)
                .collect(Collectors.toSet());

        return requested.equals(current);
    }

    @Override
    @Transactional
    public MissaoDTO criarMissao(CriarMissaoRequest request) {
        Missao missao = missaoMapper.toEntity(request);
        missao.setStatus(StatusMissao.PLANEJADA);
        missao.setOperadorResponsavel(resolveOperadorResponsavel(request));

        if (request.getTripulacaoIds() != null && !request.getTripulacaoIds().isEmpty()) {
            List<Astronauta> tripulacao = astronautaRepository.findAllById(request.getTripulacaoIds());

            if (tripulacao.size() != request.getTripulacaoIds().size()) {
                throw new RecursoNaoEncontradoException("Um ou mais IDs de astronautas nao foram encontrados no banco.");
            }
            missao.associarTripulacao(tripulacao);
        }

        if (request.getEspaconaveId() != null) {
            Espaconave espaconave = espaconaveRepository.findById(request.getEspaconaveId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Espaconave nao encontrada com ID: " + request.getEspaconaveId()));

            if ("DESATIVADA".equalsIgnoreCase(espaconave.getStatusOperacional())
                    || "EM_MANUTENCAO".equalsIgnoreCase(espaconave.getStatusOperacional())) {
                throw new IllegalStateException("A espaconave selecionada (" + espaconave.getNome() + ") nao esta operacional.");
            }
            missao.setEspaconave(espaconave);
        }

        Missao missaoSalva = missaoRepository.save(missao);
        return missaoMapper.toDTO(missaoSalva);
    }

    @Override
    @Transactional
    public MissaoDTO atualizarMissao(Long id, AtualizarMissaoRequest request) {
        Missao missao = getAccessibleMissaoById(id);

        if (!isAdmin()) {
            throw new IllegalStateException("Apenas administradores podem atualizar missoes.");
        }

        if (request.getNome() != null && !request.getNome().isBlank()) {
            missao.setNome(request.getNome());
        }
        if (request.getObjetivo() != null) {
            missao.setObjetivo(request.getObjetivo());
        }
        if (request.getDataInicio() != null) {
            missao.setDataInicio(request.getDataInicio());
        }
        if (request.getDataFim() != null) {
            missao.setDataFim(request.getDataFim());
        }

        if (request.getTripulacaoIds() != null && !sameTripulacao(request.getTripulacaoIds(), missao)) {
            List<Astronauta> novaTripulacao = astronautaRepository.findAllById(request.getTripulacaoIds());
            if (novaTripulacao.size() != request.getTripulacaoIds().size()) {
                throw new RecursoNaoEncontradoException("Um ou mais astronautas da nova lista nao foram encontrados.");
            }
            missao.associarTripulacao(novaTripulacao);
        }

        if (request.getEspaconaveId() != null) {
            Espaconave novaEspaconave = espaconaveRepository.findById(request.getEspaconaveId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Espaconave nao encontrada com ID: " + request.getEspaconaveId()));

            if ("DESATIVADA".equalsIgnoreCase(novaEspaconave.getStatusOperacional())) {
                throw new IllegalStateException("A nova espaconave selecionada nao esta operacional.");
            }
            missao.setEspaconave(novaEspaconave);
        }

        missao.setOperadorResponsavel(resolveOperadorResponsavel(request, missao));

        Missao missaoSalva = missaoRepository.save(missao);
        return missaoMapper.toDTO(missaoSalva);
    }

    @Override
    @Transactional(readOnly = true)
    public MissaoDTO buscarPorId(Long id) {
        return missaoMapper.toDTO(getAccessibleMissaoById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MissaoDTO> listarTodas() {
        if (isOperator()) {
            Long operadorId = getCurrentOperadorMissaoId();
            if (operadorId == null) {
                return List.of();
            }
            return missaoRepository.findByOperadorResponsavelIdOrderByDataInicioDesc(operadorId).stream()
                    .map(missaoMapper::toDTO)
                    .collect(Collectors.toList());
        }

        return missaoRepository.findAll().stream()
                .map(missaoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deletarMissao(Long id) {
        if (!isAdmin()) {
            throw new IllegalStateException("Apenas administradores podem excluir missoes.");
        }
        if (!missaoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Missao nao encontrada para exclusao (ID: " + id + ")");
        }
        missaoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public MissaoDTO iniciarSimulacao(Long id) {
        Missao missao = getAccessibleMissaoById(id);
        missao.iniciarSimulacao();
        return missaoMapper.toDTO(missaoRepository.save(missao));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventoDTO> listarEventosPorMissao(Long missaoId) {
        Missao missao = getAccessibleMissaoById(missaoId);
        return eventoRepository.findTop100ByMissaoIdOrderByTimestampDesc(missao.getId()).stream()
                .map(eventoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProtocoloEmergencialDTO> listarProtocolosPorMissao(Long missaoId) {
        Missao missao = getAccessibleMissaoById(missaoId);
        return protocoloRepository.findByMissaoIdOrderByAcionadoEmDesc(missao.getId()).stream()
                .map(protocoloMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProtocoloEmergencialDTO acionarProtocolo(Long missaoId, AcionarProtocoloRequest request) {
        Missao missao = getAccessibleMissaoById(missaoId);
        ProtocoloEmergencial protocolo = missao.acionarProtocolo(request.tipo(), request.descricao());
        missaoRepository.save(missao);
        return protocoloMapper.toDTO(protocolo);
    }

    @Override
    @Transactional
    public MissaoDTO concluirMissao(Long missaoId) {
        Missao missao = getAccessibleMissaoById(missaoId);
        missao.concluirMissao();
        return missaoMapper.toDTO(missaoRepository.save(missao));
    }
}
