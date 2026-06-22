package com.MonitoramentoEspacial.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    public static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI monitoramentoEspacialOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Monitoramento Espacial API")
                        .version("v1")
                        .description("Contratos REST do backend de monitoramento espacial."))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addTagsItem(new Tag().name("Autenticacao").description("Login e emissao de token JWT."))
                .addTagsItem(new Tag().name("Missoes").description("Cadastro, consulta e operacoes de ciclo de vida de missoes."))
                .addTagsItem(new Tag().name("Astronautas").description("Cadastro e consulta de astronautas."))
                .addTagsItem(new Tag().name("Espaconaves").description("Cadastro e consulta de espaconaves."))
                .addTagsItem(new Tag().name("Operadores").description("Cadastro e consulta de operadores de missao."))
                .addTagsItem(new Tag().name("Ameacas").description("Registro e consulta de ameacas associadas a missoes."))
                .addTagsItem(new Tag().name("Operacoes de Simulacao").description("Operacoes relacionadas a simulacao, eventos e protocolos de missoes."));
    }

    @Bean
    public GroupedOpenApi criticalBackendApi() {
        return GroupedOpenApi.builder()
                .group("critical-backend-api")
                .pathsToMatch(
                        "/auth/**",
                        "/missoes/**",
                        "/astronautas/**",
                        "/espaconaves/**",
                        "/operadores/**",
                        "/ameacas/**")
                .build();
    }
}
