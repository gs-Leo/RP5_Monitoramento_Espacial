package com.MonitoramentoEspacial.config;

import com.MonitoramentoEspacial.simulador.SimuladorProperties;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

class EnvironmentConfigurationFactoryTest {

    @Test
    void createsCurrentConfigurationFromActiveProfileProviderAndSimulator() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("staging");

        DataSourceProviderProperties dataSourceProperties = new DataSourceProviderProperties();
        dataSourceProperties.setProvider("self-hosted-postgres");

        SimuladorProperties simuladorProperties = new SimuladorProperties();
        simuladorProperties.setEnabled(true);
        simuladorProperties.setUrl("http://simulator.internal:8000");

        EnvironmentConfiguration configuration = new EnvironmentConfigurationFactory(
                environment,
                dataSourceProperties,
                simuladorProperties
        ).current();

        assertThat(configuration.activeProfile()).isEqualTo("staging");
        assertThat(configuration.dataSourceProvider()).isEqualTo(DataSourceProvider.SELF_HOSTED_POSTGRES);
        assertThat(configuration.simulatorEnabled()).isTrue();
        assertThat(configuration.simulatorUrl()).isEqualTo("http://simulator.internal:8000");
    }

    @Test
    void defaultsProfileToLocalWhenNoneIsActive() {
        EnvironmentConfiguration configuration = new EnvironmentConfigurationFactory(
                new MockEnvironment(),
                new DataSourceProviderProperties(),
                new SimuladorProperties()
        ).current();

        assertThat(configuration.activeProfile()).isEqualTo("local");
        assertThat(configuration.dataSourceProvider()).isEqualTo(DataSourceProvider.LOCAL_POSTGRES);
    }
}
