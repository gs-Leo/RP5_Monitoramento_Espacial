package com.MonitoramentoEspacial.middleware;

import com.MonitoramentoEspacial.aplicacao.dominio.RegistroAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Long> {
    List<RegistroAuditoria> findTop200ByOrderByCriadoEmDesc();
}
