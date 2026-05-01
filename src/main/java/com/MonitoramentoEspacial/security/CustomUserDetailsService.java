package com.MonitoramentoEspacial.security;

import com.MonitoramentoEspacial.aplicacao.dominio.UsuarioAcesso;
import com.MonitoramentoEspacial.middleware.UsuarioAcessoRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioAcessoRepository usuarioAcessoRepository;

    public CustomUserDetailsService(UsuarioAcessoRepository usuarioAcessoRepository) {
        this.usuarioAcessoRepository = usuarioAcessoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsuarioAcesso usuario = usuarioAcessoRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));

        return new AppUserPrincipal(
                usuario.getId(),
                usuario.getOperadorMissao() != null ? usuario.getOperadorMissao().getId() : null,
                usuario.getUsername(),
                usuario.getPasswordHash(),
                usuario.isAtivo(),
                List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name()))
        );
    }
}
