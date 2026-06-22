package com.MonitoramentoEspacial.simulador;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

enum SimuladorTipo {
    FOGUETE("foguete"),
    ORBITA("orbita"),
    REENTRADA("reentrada");

    private final String valorApi;

    SimuladorTipo(String valorApi) {
        this.valorApi = valorApi;
    }

    String valorApi() {
        return valorApi;
    }

    static SimuladorTipo normalizar(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return FOGUETE;
        }

        String normalizado = Normalizer.normalize(tipo, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim();

        return switch (normalizado) {
            case "orbita", "orbital" -> ORBITA;
            case "reentrada" -> REENTRADA;
            default -> FOGUETE;
        };
    }

    static Map<String, Object> parametrosPadrao(String tipo) {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("tempo_maximo", 600);

        if ("orbita".equals(tipo)) {
            parametros.put("altitude_inicial", 400000.0);
            return parametros;
        }

        if ("reentrada".equals(tipo)) {
            parametros.put("altitude_inicial", 120000.0);
            parametros.put("velocidade_inicial", 7800.0);
            return parametros;
        }

        parametros.put("massa_inicial", 549000.0);
        parametros.put("massa_combustivel", 507000.0);
        parametros.put("empuxo", 7607000.0);
        return parametros;
    }
}
