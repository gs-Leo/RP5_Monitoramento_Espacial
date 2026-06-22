package com.MonitoramentoEspacial.middleware;

import com.MonitoramentoEspacial.aplicacao.dominio.Missao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MissaoRepository extends JpaRepository<Missao, Long> {
    List<Missao> findByOperadorResponsavelIdOrderByDataInicioDesc(Long operadorId);
    Optional<Missao> findByIdAndOperadorResponsavelId(Long id, Long operadorId);
}
