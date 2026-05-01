package com.MonitoramentoEspacial.aplicacao;

import com.MonitoramentoEspacial.interfaceExterna.RegistroAuditoriaDTO;
import com.MonitoramentoEspacial.middleware.RegistroAuditoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegistroAuditoriaService {

    private final RegistroAuditoriaRepository registroAuditoriaRepository;

    public RegistroAuditoriaService(RegistroAuditoriaRepository registroAuditoriaRepository) {
        this.registroAuditoriaRepository = registroAuditoriaRepository;
    }

    @Transactional(readOnly = true)
    public List<RegistroAuditoriaDTO> listarRecentes() {
        return registroAuditoriaRepository.findTop200ByOrderByCriadoEmDesc().stream()
                .map(registro -> new RegistroAuditoriaDTO(
                        registro.getId(),
                        registro.getTipo(),
                        registro.getEvento(),
                        registro.getUsername(),
                        registro.getRole(),
                        registro.getMetodoHttp(),
                        registro.getCaminho(),
                        registro.getDetalhe(),
                        registro.isSucesso(),
                        registro.getCriadoEm()
                ))
                .toList();
    }
}
