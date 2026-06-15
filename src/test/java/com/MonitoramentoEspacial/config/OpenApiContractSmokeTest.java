package com.MonitoramentoEspacial.config;

import com.MonitoramentoEspacial.aplicacao.AmeacaServiceInterface;
import com.MonitoramentoEspacial.aplicacao.AstronautaServiceInterface;
import com.MonitoramentoEspacial.aplicacao.EspaconaveServiceInterface;
import com.MonitoramentoEspacial.aplicacao.MissaoServiceInterface;
import com.MonitoramentoEspacial.aplicacao.OperadorDeMissaoServiceInterface;
import com.MonitoramentoEspacial.interfaceExterna.AmeacaController;
import com.MonitoramentoEspacial.interfaceExterna.AstronautaController;
import com.MonitoramentoEspacial.interfaceExterna.EspaconaveController;
import com.MonitoramentoEspacial.interfaceExterna.MissaoController;
import com.MonitoramentoEspacial.interfaceExterna.OperadorDeMissaoController;
import com.MonitoramentoEspacial.security.AuthController;
import com.MonitoramentoEspacial.security.AuthService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = OpenApiContractSmokeTest.TestApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class OpenApiContractSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void openApiDocumentIncludesCriticalEndpointGroupsAndSecurityScheme() throws Exception {
        String content = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode document = objectMapper.readTree(content);

        assertThat(document.at("/components/securitySchemes/bearerAuth/type").asText()).isEqualTo("http");
        assertThat(document.at("/paths").fieldNames())
                .toIterable()
                .contains(
                        "/auth/login",
                        "/missoes",
                        "/missoes/{id}/iniciar-simulacao",
                        "/astronautas",
                        "/espaconaves",
                        "/operadores",
                        "/ameacas");

        List<String> tagNames = StreamSupport.stream(document.path("tags").spliterator(), false)
                .map(tag -> tag.path("name").asText())
                .toList();

        assertThat(tagNames).contains(
                "Autenticacao",
                "Missoes",
                "Astronautas",
                "Espaconaves",
                "Operadores",
                "Ameacas",
                "Operacoes de Simulacao");
    }

    @Test
    void criticalBackendApiGroupIsAvailable() throws Exception {
        String content = mockMvc.perform(get("/v3/api-docs/critical-backend-api"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode document = objectMapper.readTree(content);

        assertThat(document.at("/paths").fieldNames())
                .toIterable()
                .contains("/auth/login", "/missoes", "/astronautas", "/espaconaves", "/operadores", "/ameacas");
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class,
            SecurityAutoConfiguration.class,
            SecurityFilterAutoConfiguration.class,
            UserDetailsServiceAutoConfiguration.class
    })
    @Import({
            OpenApiConfig.class,
            AuthController.class,
            MissaoController.class,
            AstronautaController.class,
            EspaconaveController.class,
            OperadorDeMissaoController.class,
            AmeacaController.class,
            MockedServices.class
    })
    static class TestApplication {
    }

    @TestConfiguration
    static class MockedServices {

        @Bean
        AuthService authService() {
            return Mockito.mock(AuthService.class);
        }

        @Bean
        MissaoServiceInterface missaoServiceInterface() {
            return Mockito.mock(MissaoServiceInterface.class);
        }

        @Bean
        AstronautaServiceInterface astronautaServiceInterface() {
            return Mockito.mock(AstronautaServiceInterface.class);
        }

        @Bean
        EspaconaveServiceInterface espaconaveServiceInterface() {
            return Mockito.mock(EspaconaveServiceInterface.class);
        }

        @Bean
        OperadorDeMissaoServiceInterface operadorDeMissaoServiceInterface() {
            return Mockito.mock(OperadorDeMissaoServiceInterface.class);
        }

        @Bean
        AmeacaServiceInterface ameacaServiceInterface() {
            return Mockito.mock(AmeacaServiceInterface.class);
        }
    }
}
