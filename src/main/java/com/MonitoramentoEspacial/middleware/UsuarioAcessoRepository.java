package com.MonitoramentoEspacial.middleware;

import com.MonitoramentoEspacial.aplicacao.dominio.UsuarioAcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioAcessoRepository extends JpaRepository<UsuarioAcesso, Long> {
    Optional<UsuarioAcesso> findByUsername(String username);
    boolean existsByUsername(String username);
}
