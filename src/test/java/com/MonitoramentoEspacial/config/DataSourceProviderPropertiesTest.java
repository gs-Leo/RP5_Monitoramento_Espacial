package com.MonitoramentoEspacial.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DataSourceProviderPropertiesTest {

    @Test
    void resolvesSupportedProvidersFromPropertyValues() {
        assertThat(DataSourceProvider.fromPropertyValue("local-postgres")).isEqualTo(DataSourceProvider.LOCAL_POSTGRES);
        assertThat(DataSourceProvider.fromPropertyValue("neon")).isEqualTo(DataSourceProvider.NEON);
        assertThat(DataSourceProvider.fromPropertyValue("self-hosted-postgres")).isEqualTo(DataSourceProvider.SELF_HOSTED_POSTGRES);
    }

    @Test
    void defaultsBlankProviderToLocalPostgres() {
        assertThat(DataSourceProvider.fromPropertyValue(null)).isEqualTo(DataSourceProvider.LOCAL_POSTGRES);
        assertThat(DataSourceProvider.fromPropertyValue(" ")).isEqualTo(DataSourceProvider.LOCAL_POSTGRES);
    }

    @Test
    void rejectsUnsupportedProviderValues() {
        assertThatThrownBy(() -> DataSourceProvider.fromPropertyValue("mysql"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Provedor de datasource nao suportado");
    }

    @Test
    void resolvesConfiguredProperty() {
        DataSourceProviderProperties properties = new DataSourceProviderProperties();
        properties.setProvider("neon");

        assertThat(properties.resolvedProvider()).isEqualTo(DataSourceProvider.NEON);
    }
}
