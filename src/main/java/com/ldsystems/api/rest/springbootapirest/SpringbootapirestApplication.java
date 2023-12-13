package com.ldsystems.api.rest.springbootapirest;

import com.ldsystems.api.rest.springbootapirest.exception.ControleExcecoes;
import com.ldsystems.api.rest.springbootapirest.exception.ObjetoErro;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EntityScan(basePackages = "com.ldsystems.api.rest.springbootapirest.model")
@EnableTransactionManagement  //Gerenciar transações tudo automaticamente
@EnableWebMvc // Permitindo trabalhar com WebMVC
@RestController //Projeto que vai rodar REST
@EnableCaching //Habilitar o cache
public class SpringbootapirestApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SpringbootapirestApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringbootapirestApplication.class, args);
        //Teste para verificar se alguma classe está no contexto do spring, caso não estiver o spring não está enxergando ela:
//        ApplicationContext context = SpringApplication.run(SpringbootapirestApplication.class);
//        boolean isRegistered = context.getBeansOfType(ObjetoErro.class).size() > 0;
//        System.out.println("A classe ControleExcecoes está registrada no contexto do Spring: " + isRegistered);
    }

}
