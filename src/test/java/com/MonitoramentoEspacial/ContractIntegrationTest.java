package com.MonitoramentoEspacial;

import com.MonitoramentoEspacial.aplicacao.MonitoramentoEspacialApp;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MonitoramentoEspacialApp.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:contract-tests;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.show-sql=false",
        "app.security.jwt.secret=test-contract-jwt-secret-32-chars-minimum",
        "app.security.seed.admin.username=admin",
        "app.security.seed.admin.password=admin_local_password",
        "app.security.seed.operador.username=operador",
        "app.security.seed.operador.password=operador_local_password",
        "app.security.seed.analista.username=analista",
        "app.security.seed.analista.password=analista_local_password",
        "simulator.api.enabled=false",
        "simulator.api.url=http://mock-simulator.local"
})
class ContractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void authenticationSuccessFailureAndAuthorizationBehavior() throws Exception {
        String token = login("admin", "admin_local_password")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(objectMapper.readTree(token).path("token").asText()).isNotBlank();

        login("admin", "wrong_password")
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/missoes"))
                .andExpect(status().isUnauthorized());

        String analistaToken = tokenFor("analista", "analista_local_password");
        mockMvc.perform(post("/astronautas")
                        .header("Authorization", bearer(analistaToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "nome", "Mae Jemison",
                                "idade", 69,
                                "ativo", true,
                                "nivelAptidaoMedica", "APTO",
                                "missoesRealizadas", 1
                        ))))
                .andExpect(status().isForbidden());
    }

    @Test
    void representativeCrudFlowsPersistForMissionsAstronautsSpacecraftAndOperators() throws Exception {
        String adminToken = tokenFor("admin", "admin_local_password");

        long operadorId = idFrom(createOperador(adminToken, "Operador CRUD")
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/operadores/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Operador CRUD"))
                .andReturn()
                .getResponse()
                .getContentAsString());

        long astronautaId = idFrom(createAstronauta(adminToken, "Astronauta CRUD")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Astronauta CRUD"))
                .andReturn()
                .getResponse()
                .getContentAsString());

        long espaconaveId = idFrom(createEspaconave(adminToken, "Nave CRUD")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nome").value("Nave CRUD"))
                .andReturn()
                .getResponse()
                .getContentAsString());

        mockMvc.perform(put("/astronautas/{id}", astronautaId)
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "nome", "Astronauta CRUD Atualizado",
                                "idade", 36,
                                "ativo", true,
                                "nivelAptidaoMedica", "APTO",
                                "missoesRealizadas", 3
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Astronauta CRUD Atualizado"));

        mockMvc.perform(put("/espaconaves/{id}", espaconaveId)
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "nome", "Nave CRUD Atualizada",
                                "capacidade", 4,
                                "statusOperacional", "OPERACIONAL"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Nave CRUD Atualizada"));

        mockMvc.perform(put("/operadores/{id}", operadorId)
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "nome", "Operador CRUD Atualizado",
                                "idade", 41,
                                "ativo", true,
                                "turno", "Noturno",
                                "areaEspecializacao", "Controle Orbital"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Operador CRUD Atualizado"));

        long missaoId = idFrom(createMissao(adminToken, operadorId, astronautaId, espaconaveId, "Missao CRUD")
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.containsString("/missoes/")))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.status").value("PLANEJADA"))
                .andExpect(jsonPath("$.operadorResponsavel.id").value((int) operadorId))
                .andReturn()
                .getResponse()
                .getContentAsString());

        mockMvc.perform(get("/missoes/{id}", missaoId).header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Missao CRUD"))
                .andExpect(jsonPath("$.tripulacao[0].id").value((int) astronautaId));

        mockMvc.perform(get("/missoes").header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(put("/missoes/{id}", missaoId)
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "nome", "Missao CRUD Atualizada",
                                "objetivo", "Validar contrato atualizado",
                                "dataInicio", "2026-07-02",
                                "tripulacaoIds", new long[]{astronautaId},
                                "espaconaveId", espaconaveId,
                                "operadorId", operadorId
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Missao CRUD Atualizada"));

        mockMvc.perform(delete("/missoes/{id}", missaoId).header("Authorization", bearer(adminToken)))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/astronautas/{id}", astronautaId).header("Authorization", bearer(adminToken)))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/espaconaves/{id}", espaconaveId).header("Authorization", bearer(adminToken)))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete("/operadores/{id}", operadorId).header("Authorization", bearer(adminToken)))
                .andExpect(status().isNoContent());
    }

    @Test
    void threatCreationListingAndMissionSimulationInitiationAreCovered() throws Exception {
        String adminToken = tokenFor("admin", "admin_local_password");

        long operadorId = idFrom(createOperador(adminToken, "Operador Ameaca").andReturn().getResponse().getContentAsString());
        long astronautaId = idFrom(createAstronauta(adminToken, "Astronauta Ameaca").andReturn().getResponse().getContentAsString());
        long espaconaveId = idFrom(createEspaconave(adminToken, "Nave Ameaca").andReturn().getResponse().getContentAsString());
        long missaoId = idFrom(createMissao(adminToken, operadorId, astronautaId, espaconaveId, "Missao Ameaca")
                .andReturn()
                .getResponse()
                .getContentAsString());

        mockMvc.perform(post("/ameacas")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "missaoId", missaoId,
                                "tipo", "METEORO",
                                "descricao", "Fragmento orbital em aproximacao",
                                "nivelPerigo", 8,
                                "distanciaKm", 1200.5
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.missaoId").value((int) missaoId))
                .andExpect(jsonPath("$.tipo").value("METEORO"));

        mockMvc.perform(get("/ameacas/missao/{missaoId}", missaoId).header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].descricao").value("Fragmento orbital em aproximacao"));

        mockMvc.perform(post("/missoes/{id}/iniciar-simulacao", missaoId).header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value((int) missaoId))
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));

        mockMvc.perform(get("/missoes/{id}", missaoId).header("Authorization", bearer(adminToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));
    }

    @Test
    void openApiDocumentIsAvailableWithCriticalGroups() throws Exception {
        String content = mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode paths = objectMapper.readTree(content).path("paths");

        assertThat(paths.fieldNames()).toIterable().contains(
                "/auth/login",
                "/missoes",
                "/missoes/{id}/iniciar-simulacao",
                "/astronautas",
                "/espaconaves",
                "/operadores",
                "/ameacas"
        );
    }

    private org.springframework.test.web.servlet.ResultActions login(String username, String senha) throws Exception {
        return mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("username", username, "senha", senha))));
    }

    private String tokenFor(String username, String senha) throws Exception {
        MvcResult result = login(username, senha)
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).path("token").asText();
    }

    private org.springframework.test.web.servlet.ResultActions createOperador(String token, String nome) throws Exception {
        return mockMvc.perform(post("/operadores")
                .header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "nome", nome,
                        "idade", 40,
                        "ativo", true,
                        "turno", "Diurno",
                        "areaEspecializacao", "Controle de Voo"
                ))));
    }

    private org.springframework.test.web.servlet.ResultActions createAstronauta(String token, String nome) throws Exception {
        return mockMvc.perform(post("/astronautas")
                .header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "nome", nome,
                        "idade", 35,
                        "ativo", true,
                        "nivelAptidaoMedica", "APTO",
                        "missoesRealizadas", 2
                ))));
    }

    private org.springframework.test.web.servlet.ResultActions createEspaconave(String token, String nome) throws Exception {
        return mockMvc.perform(post("/espaconaves")
                .header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "nome", nome,
                        "capacidade", 3,
                        "statusOperacional", "OPERACIONAL"
                ))));
    }

    private org.springframework.test.web.servlet.ResultActions createMissao(
            String token,
            long operadorId,
            long astronautaId,
            long espaconaveId,
            String nome
    ) throws Exception {
        return mockMvc.perform(post("/missoes")
                .header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of(
                        "nome", nome,
                        "objetivo", "Validar contrato HTTP",
                        "dataInicio", "2026-07-01",
                        "tipoSimulacao", "orbita",
                        "tripulacaoIds", new long[]{astronautaId},
                        "espaconaveId", espaconaveId,
                        "operadorId", operadorId
                ))));
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    private long idFrom(String content) {
        try {
            return objectMapper.readTree(content).path("id").asLong();
        } catch (Exception ex) {
            throw new IllegalStateException("Nao foi possivel ler o id do payload: " + content, ex);
        }
    }
}
