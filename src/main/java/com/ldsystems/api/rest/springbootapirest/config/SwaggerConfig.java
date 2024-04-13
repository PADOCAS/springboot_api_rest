package com.ldsystems.api.rest.springbootapirest.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//Informa o Token JWT para autenticação das requisições no Swagger
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {

    @Bean
    public OpenAPI swaggerSpringRestApiDefinition() {
        License licenca = new License();
        licenca.setName("Licença - LD Systems");
        licenca.setUrl("http://www.ldsystems.com.br/portfolio");

        return new OpenAPI()
                .info(new Info().title("Aplicação Spring REST API")
                        .contact(new Contact().name("Equipe LD Systems").email("ldsystems.atendimento@gmail.com"))
                        .termsOfService("Termo de uso: Open Source")
                        .license(licenca)
                        .description("Aplicação desenvolvida para testes - Spring REST API x Angular.")
                        .version("v0.0.1"));
    }
}
