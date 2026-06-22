package com.MonitoramentoEspacial.config;

import java.util.Arrays;

public enum DataSourceProvider {
    LOCAL_POSTGRES("local-postgres"),
    NEON("neon"),
    SELF_HOSTED_POSTGRES("self-hosted-postgres");

    private final String propertyValue;

    DataSourceProvider(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String propertyValue() {
        return propertyValue;
    }

    public static DataSourceProvider fromPropertyValue(String value) {
        if (value == null || value.isBlank()) {
            return LOCAL_POSTGRES;
        }

        return Arrays.stream(values())
                .filter(provider -> provider.propertyValue.equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Provedor de datasource nao suportado: " + value));
    }
}
