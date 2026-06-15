package com.MonitoramentoEspacial.simulador;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SimuladorRequest(
        String descricao,
        Integer tempo_maximo,
        Double massa_inicial,
        Double massa_combustivel,
        Double empuxo,
        Double altitude_inicial,
        Double velocidade_inicial
) {

    public static SimuladorRequest padrao(String tipoSimulacao, String descricaoMissao) {
        String tipoNormalizado = SimuladorTipo.normalizar(tipoSimulacao).valorApi();
        Map<String, Object> parametros = SimuladorTipo.parametrosPadrao(tipoNormalizado);

        return new SimuladorRequest(
                descricaoMissao,
                (Integer) parametros.get("tempo_maximo"),
                (Double) parametros.get("massa_inicial"),
                (Double) parametros.get("massa_combustivel"),
                (Double) parametros.get("empuxo"),
                (Double) parametros.get("altitude_inicial"),
                (Double) parametros.get("velocidade_inicial")
        );
    }
}
