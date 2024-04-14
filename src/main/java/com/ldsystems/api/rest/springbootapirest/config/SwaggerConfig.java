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
                        .description(getDescription())
                        .version("v0.0.1"));
    }

    private String getDescription() {
        StringBuilder str = new StringBuilder();
        str.append("Aplicação desenvolvida para projeto - Spring REST API x Angular.");
        str.append("<br/>");
        str.append("- Utilize o 'Controle de Autenticação de Usuários' -> /login - passando login e senha, para validar suas credenciais e pegar um Token JWT.");
        str.append("<br/>");
        str.append("- Adicione o Token no botão 'Authorize', onde será enviado em todas as requisições que exijam Token.");

        return str.toString();
    }
}
