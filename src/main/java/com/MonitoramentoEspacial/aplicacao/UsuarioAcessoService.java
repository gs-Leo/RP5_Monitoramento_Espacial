package com.MonitoramentoEspacial.aplicacao;

import com.MonitoramentoEspacial.aplicacao.dominio.OperadorDeMissao;
import com.MonitoramentoEspacial.aplicacao.dominio.RoleSistema;
import com.MonitoramentoEspacial.aplicacao.dominio.UsuarioAcesso;
import com.MonitoramentoEspacial.interfaceExterna.AtualizarUsuarioAcessoRequest;
import com.MonitoramentoEspacial.interfaceExterna.CriarUsuarioAcessoRequest;
import com.MonitoramentoEspacial.interfaceExterna.UsuarioAcessoDTO;
import com.MonitoramentoEspacial.middleware.OperadorDeMissaoRepository;
import com.MonitoramentoEspacial.middleware.UsuarioAcessoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioAcessoService implements UsuarioAcessoServiceInterface {

    private final UsuarioAcessoRepository usuarioAcessoRepository;
    private final OperadorDeMissaoRepository operadorDeMissaoRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioAcessoService(
            UsuarioAcessoRepository usuarioAcessoRepository,
            OperadorDeMissaoRepository operadorDeMissaoRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioAcessoRepository = usuarioAcessoRepository;
        this.operadorDeMissaoRepository = operadorDeMissaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UsuarioAcessoDTO criarUsuario(CriarUsuarioAcessoRequest request) {
        String username = request.getUsername().trim();
        if (usuarioAcessoRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Ja existe um usuario com esse username.");
        }

        RoleSistema role = parseRole(request.getRole());

        UsuarioAcesso usuario = new UsuarioAcesso();
        usuario.setUsername(username);
        usuario.setPasswordHash(passwordEncoder.encode(request.getSenha()));
        usuario.setRole(role);
        usuario.setAtivo(Boolean.TRUE.equals(request.getAtivo()));
        usuario.setOperadorMissao(resolveOperador(role, request.getOperadorMissaoId()));

        return toDTO(usuarioAcessoRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioAcessoDTO atualizarUsuario(Long id, AtualizarUsuarioAcessoRequest request) {
        UsuarioAcesso usuario = getUsuario(id);
        RoleSistema role = parseRole(request.getRole());
        usuario.setRole(role);
        usuario.setAtivo(Boolean.TRUE.equals(request.getAtivo()));
        usuario.setOperadorMissao(resolveOperador(role, request.getOperadorMissaoId()));

        if (request.getNovaSenha() != null && !request.getNovaSenha().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(request.getNovaSenha()));
        }

        return toDTO(usuarioAcessoRepository.save(usuario));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioAcessoDTO> listarUsuarios() {
        return usuarioAcessoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioAcessoDTO buscarPorId(Long id) {
        return toDTO(getUsuario(id));
    }

    @Override
    @Transactional
    public void deletarUsuario(Long id) {
        UsuarioAcesso usuario = getUsuario(id);
        usuarioAcessoRepository.delete(usuario);
    }

    private UsuarioAcesso getUsuario(Long id) {
        return usuarioAcessoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuario de acesso nao encontrado com ID: " + id));
    }

    private RoleSistema parseRole(String role) {
        try {
            return RoleSistema.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Perfil invalido. Use ADMIN, OPERADOR ou ANALISTA.");
        }
    }

    private OperadorDeMissao resolveOperador(RoleSistema role, Long operadorMissaoId) {
        if (role == RoleSistema.OPERADOR) {
            if (operadorMissaoId == null) {
                throw new IllegalArgumentException("Usuarios com perfil OPERADOR precisam estar vinculados a um operador de missao.");
            }
            return operadorDeMissaoRepository.findById(operadorMissaoId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Operador de missao nao encontrado com ID: " + operadorMissaoId));
        }

        if (operadorMissaoId == null) {
            return null;
        }

        return operadorDeMissaoRepository.findById(operadorMissaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Operador de missao nao encontrado com ID: " + operadorMissaoId));
    }

    private UsuarioAcessoDTO toDTO(UsuarioAcesso usuario) {
        return new UsuarioAcessoDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getRole().name(),
                usuario.isAtivo(),
                usuario.getOperadorMissao() != null ? usuario.getOperadorMissao().getId() : null,
                usuario.getOperadorMissao() != null ? usuario.getOperadorMissao().getNome() : null
        );
    }
}
