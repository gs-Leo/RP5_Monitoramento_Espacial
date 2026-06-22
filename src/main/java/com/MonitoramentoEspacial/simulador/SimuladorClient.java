package com.MonitoramentoEspacial.simulador;

import com.MonitoramentoEspacial.aplicacao.dominio.Missao;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class SimuladorClient {

    private static final String SIMULACOES_PATH = "/simulacoes/{tipo}";

    private final RestTemplate restTemplate;
    private final SimuladorProperties properties;

    public SimuladorClient(
            @Qualifier("simuladorRestTemplate") RestTemplate restTemplate,
            SimuladorProperties properties
    ) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    public boolean isEnabled() {
        return properties.isEnabled();
    }

    public SimuladorResponse iniciarSimulacao(Missao missao) {
        SimuladorTipo tipo = SimuladorTipo.normalizar(missao.getTipoSimulacao());
        SimuladorRequest request = SimuladorRequest.padrao(tipo.valorApi(), missao.getNome());
        URI uri = UriComponentsBuilder.fromHttpUrl(properties.getUrl())
                .path(SIMULACOES_PATH)
                .buildAndExpand(tipo.valorApi())
                .toUri();

        try {
            SimuladorResponse response = restTemplate.postForObject(uri, request, SimuladorResponse.class);
            if (response == null || response.id() == null || response.id().isBlank()) {
                throw new SimuladorClientException("Resposta invalida do simulador: campo id ausente.");
            }
            return response;
        } catch (RestClientResponseException ex) {
            throw new SimuladorClientException(
                    "Simulador retornou erro HTTP " + ex.getStatusCode().value() + " ao iniciar " + tipo.valorApi() + ".",
                    ex
            );
        } catch (ResourceAccessException ex) {
            throw new SimuladorClientException("Falha de conexao ou timeout ao chamar o simulador.", ex);
        }
    }

    public HttpStatusCode healthStatus() {
        URI uri = UriComponentsBuilder.fromHttpUrl(properties.getUrl())
                .path("/health")
                .build()
                .toUri();
        return restTemplate.getForEntity(uri, String.class).getStatusCode();
    }
}
