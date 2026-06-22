package com.MonitoramentoEspacial.simulador;

import com.MonitoramentoEspacial.aplicacao.dominio.Missao;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class SimuladorClientTest {

    @Test
    void iniciarSimulacaoUsesConfiguredBaseUrlPathAndPayload() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        SimuladorClient client = new SimuladorClient(restTemplate, properties("http://simulator.example"));

        server.expect(once(), requestTo("http://simulator.example/simulacoes/orbita"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("""
                        {
                          "descricao": "Missao Orbital",
                          "tempo_maximo": 600,
                          "altitude_inicial": 400000.0
                        }
                        """))
                .andRespond(withSuccess("""
                        {
                          "id": "orbita_20260615_120000",
                          "descricao": "Missao Orbital",
                          "tipo": "orbita",
                          "resultado": "SUCESSO",
                          "data_execucao": "2026-06-15T12:00:00",
                          "detalhes": {"tempo_maximo": 600}
                        }
                        """, MediaType.APPLICATION_JSON));

        Missao missao = new Missao();
        missao.setNome("Missao Orbital");
        missao.setTipoSimulacao("orbita");

        SimuladorResponse response = client.iniciarSimulacao(missao);

        assertThat(response.id()).isEqualTo("orbita_20260615_120000");
        server.verify();
    }

    @Test
    void iniciarSimulacaoMapsSimulatorHttpErrorToClientException() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        SimuladorClient client = new SimuladorClient(restTemplate, properties("http://simulator.example"));

        server.expect(once(), requestTo("http://simulator.example/simulacoes/foguete"))
                .andRespond(withBadRequest().body("{\"detail\":\"parametros invalidos\"}"));

        Missao missao = new Missao();
        missao.setNome("Missao Foguete");
        missao.setTipoSimulacao("foguete");

        assertThatThrownBy(() -> client.iniciarSimulacao(missao))
                .isInstanceOf(SimuladorClientException.class)
                .hasMessageContaining("Simulador retornou erro HTTP 400");

        server.verify();
    }

    @Test
    void iniciarSimulacaoSwitchesBaseUrlFromConfiguration() {
        RestTemplate firstRestTemplate = new RestTemplate();
        MockRestServiceServer firstServer = MockRestServiceServer.bindTo(firstRestTemplate).build();
        SimuladorClient firstClient = new SimuladorClient(firstRestTemplate, properties("http://first-simulator.local"));

        RestTemplate secondRestTemplate = new RestTemplate();
        MockRestServiceServer secondServer = MockRestServiceServer.bindTo(secondRestTemplate).build();
        SimuladorClient secondClient = new SimuladorClient(secondRestTemplate, properties("http://second-simulator.local"));

        firstServer.expect(once(), requestTo("http://first-simulator.local/simulacoes/orbita"))
                .andRespond(withSuccess(validResponse("first"), MediaType.APPLICATION_JSON));
        secondServer.expect(once(), requestTo("http://second-simulator.local/simulacoes/orbita"))
                .andRespond(withSuccess(validResponse("second"), MediaType.APPLICATION_JSON));

        Missao missao = new Missao();
        missao.setNome("Missao URL Switch");
        missao.setTipoSimulacao("orbita");

        assertThat(firstClient.iniciarSimulacao(missao).id()).isEqualTo("first");
        assertThat(secondClient.iniciarSimulacao(missao).id()).isEqualTo("second");

        firstServer.verify();
        secondServer.verify();
    }

    private SimuladorProperties properties(String url) {
        SimuladorProperties properties = new SimuladorProperties();
        properties.setEnabled(true);
        properties.setUrl(url);
        return properties;
    }

    private String validResponse(String id) {
        return """
                {
                  "id": "%s",
                  "descricao": "Missao URL Switch",
                  "tipo": "orbita",
                  "resultado": "SUCESSO",
                  "data_execucao": "2026-06-15T12:00:00",
                  "detalhes": {}
                }
                """.formatted(id);
    }
}
