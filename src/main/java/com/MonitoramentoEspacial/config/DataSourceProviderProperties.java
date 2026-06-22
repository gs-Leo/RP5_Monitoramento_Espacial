package com.MonitoramentoEspacial.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.datasource")
public class DataSourceProviderProperties {

    private String provider = DataSourceProvider.LOCAL_POSTGRES.propertyValue();

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public DataSourceProvider resolvedProvider() {
        return DataSourceProvider.fromPropertyValue(provider);
    }
}
