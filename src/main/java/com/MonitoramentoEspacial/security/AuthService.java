package com.MonitoramentoEspacial.security;

import com.MonitoramentoEspacial.aplicacao.dominio.TipoAuditoria;
import com.MonitoramentoEspacial.aplicacao.dominio.UsuarioAcesso;
import com.MonitoramentoEspacial.middleware.UsuarioAcessoRepository;
import com.MonitoramentoEspacial.security.audit.AuditService;
import com.MonitoramentoEspacial.security.dto.AuthResponse;
import com.MonitoramentoEspacial.security.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final UsuarioAcessoRepository usuarioAcessoRepository;
    private final JwtService jwtService;
    private final AuditService auditService;

    public AuthService(
            AuthenticationManager authenticationManager,
            CustomUserDetailsService userDetailsService,
            UsuarioAcessoRepository usuarioAcessoRepository,
            JwtService jwtService,
            AuditService auditService
    ) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.usuarioAcessoRepository = usuarioAcessoRepository;
        this.jwtService = jwtService;
        this.auditService = auditService;
    }

    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.senha())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
            UsuarioAcesso usuario = usuarioAcessoRepository.findByUsername(request.username())
                    .orElseThrow(() -> new BadCredentialsException("Usuario nao encontrado"));

            String token = jwtService.generateToken(userDetails, usuario.getRole().name());
            long expiresAt = jwtService.getExpirationTimestamp();

            auditService.registrar(
                    TipoAuditoria.LOGIN_SUCESSO,
                    "Login realizado",
                    "Autenticacao concluida com sucesso",
                    authentication,
                    httpRequest,
                    true
            );

            return new AuthResponse(token, usuario.getUsername(), usuario.getRole().name(), expiresAt);
        } catch (BadCredentialsException | DisabledException ex) {
            auditService.registrar(
                    TipoAuditoria.LOGIN_FALHA,
                    "Falha de login",
                    ex.getMessage(),
                    request.username(),
                    null,
                    httpRequest,
                    false
            );
            throw ex;
        }
    }
}
