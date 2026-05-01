package com.MonitoramentoEspacial.security;

import com.MonitoramentoEspacial.aplicacao.dominio.RoleSistema;
import com.MonitoramentoEspacial.aplicacao.dominio.UsuarioAcesso;
import com.MonitoramentoEspacial.middleware.UsuarioAcessoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecuritySeedConfig {

    private static final Logger log = LoggerFactory.getLogger(SecuritySeedConfig.class);

    @Bean
    public CommandLineRunner seedUsuarios(
            UsuarioAcessoRepository usuarioAcessoRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.security.seed.admin.username}") String adminUsername,
            @Value("${app.security.seed.admin.password}") String adminPassword,
            @Value("${app.security.seed.operador.username}") String operadorUsername,
            @Value("${app.security.seed.operador.password}") String operadorPassword,
            @Value("${app.security.seed.analista.username}") String analistaUsername,
            @Value("${app.security.seed.analista.password}") String analistaPassword
    ) {
        return args -> {
            criarUsuarioSeNaoExistir(usuarioAcessoRepository, passwordEncoder, adminUsername, adminPassword, RoleSistema.ADMIN);
            criarUsuarioSeNaoExistir(usuarioAcessoRepository, passwordEncoder, operadorUsername, operadorPassword, RoleSistema.OPERADOR);
            criarUsuarioSeNaoExistir(usuarioAcessoRepository, passwordEncoder, analistaUsername, analistaPassword, RoleSistema.ANALISTA);
        };
    }

    private void criarUsuarioSeNaoExistir(
            UsuarioAcessoRepository usuarioAcessoRepository,
            PasswordEncoder passwordEncoder,
            String username,
            String password,
            RoleSistema role
    ) {
        if (usuarioAcessoRepository.existsByUsername(username)) {
            return;
        }

        UsuarioAcesso usuario = new UsuarioAcesso();
        usuario.setUsername(username);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setRole(role);
        usuario.setAtivo(true);
        usuarioAcessoRepository.save(usuario);

        log.warn("Usuario inicial criado: {} com perfil {}", username, role);
    }
}
