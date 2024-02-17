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
        //Tudo será declarado aqui, isso fará com que a resposta seja liberada para qualquer navegador:
        registry.addMapping("/usuario/**")
                .allowedOrigins("*")
                .allowedMethods("*");

        registry.addMapping("/recuperarsenhauser/**")
                .allowedOrigins("*")
                .allowedMethods("*");
    }
}
