package com.MonitoramentoEspacial.config;

public record EnvironmentConfiguration(
        String activeProfile,
        DataSourceProvider dataSourceProvider,
        boolean simulatorEnabled,
        String simulatorUrl
) {
}
