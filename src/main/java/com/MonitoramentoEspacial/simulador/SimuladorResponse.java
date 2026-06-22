package com.MonitoramentoEspacial.simulador;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SimuladorResponse(
        String id,
        String descricao,
        String tipo,
        String resultado,
        String data_execucao,
        Map<String, Object> detalhes
) {
}
