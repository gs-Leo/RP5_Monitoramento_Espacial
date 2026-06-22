package com.MonitoramentoEspacial.config;

import com.MonitoramentoEspacial.simulador.SimuladorProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class EnvironmentConfigurationFactory {

    private final Environment environment;
    private final DataSourceProviderProperties dataSourceProviderProperties;
    private final SimuladorProperties simuladorProperties;

    public EnvironmentConfigurationFactory(
            Environment environment,
            DataSourceProviderProperties dataSourceProviderProperties,
            SimuladorProperties simuladorProperties
    ) {
        this.environment = environment;
        this.dataSourceProviderProperties = dataSourceProviderProperties;
        this.simuladorProperties = simuladorProperties;
    }

    public EnvironmentConfiguration current() {
        String activeProfile = Arrays.stream(environment.getActiveProfiles())
                .findFirst()
                .orElse("local");

        return new EnvironmentConfiguration(
                activeProfile,
                dataSourceProviderProperties.resolvedProvider(),
                simuladorProperties.isEnabled(),
                simuladorProperties.getUrl()
        );
    }
}
