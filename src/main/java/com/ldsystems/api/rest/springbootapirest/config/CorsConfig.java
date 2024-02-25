package com.ldsystems.api.rest.springbootapirest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração Global de CrossOrigin
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //Fará com que a resposta seja liberada para qualquer navegador, liberação CORS:
        //Do Usuário não precisamos fazer liberação específica de CORS
//        registry.addMapping("/usuario")
//                .allowedOrigins("https://www.ldsystems.com.br", "https://www.google.com.br")
//                .allowedMethods("GET", "POST", "PUT", "DELETE");

        registry.addMapping("/recuperarsenhauser/**")
                .allowedOrigins("*")
                .allowedMethods("*");
    }
}
