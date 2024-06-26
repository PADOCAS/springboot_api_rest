package com.ldsystems.api.rest.springbootapirest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * Mapeia URL's, endereços, autoriza ou bloqueia acessos a URL
 */
@SuppressWarnings("ALL")
@Configuration
@EnableWebSecurity
public class WebConfigSecurity {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver exceptionResolver;

    @Bean
    public JWTAPIAutenticacaoFilter jwtApiAutenticacaoFilter() {
        return new JWTAPIAutenticacaoFilter(exceptionResolver);
    }

    @Bean
    public JWTLoginFilter jwtLoginFilter(AuthenticationConfiguration configuration) throws Exception {
        return new JWTLoginFilter(authenticationManager(configuration), exceptionResolver);
    }

    /**
     * Para funcionamento no payara deve ser deixado dessa forma!! tomcat e payara testados!
     *
     * @param http          {@link HttpSecurity}
     * @param introspector  {@link HandlerMappingIntrospector}
     * @param configuration {@link AuthenticationConfiguration}
     * @return SecurityFilterChain
     * @throws Exception Caso ocorrer algum problema
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector, AuthenticationConfiguration configuration) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector).servletPath("/");

        http.csrf(csrf -> csrf.disable()) //Desativa as configurações padrão de memória
//        http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())) //Causando erro dessa forma para desativar configurações padrão de memória!!! usar da forma acima
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(mvcMatcherBuilder.pattern("/springbootapirest/**")).permitAll() //Definir o caminho do contexto para aplicação permite ALL
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/index")).permitAll() // Qualquer usuário acessa a página inicial
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/recuperarsenhauser/**")).permitAll() // Qualquer usuário pode recuperar a senha por e-mail
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/validlogin/**")).permitAll() // Qualquer usuário pode recuperar a senha por e-mail
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/login")).permitAll() // Qualquer usuário acessa o login
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/")).permitAll() // Qualquer usuário acessa o login
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/v3/api-docs/**")).permitAll() // Qualquer usuário acessa o Swagger
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui/**")).permitAll() // Qualquer usuário acessa o Swagger
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui.html")).permitAll() // Qualquer usuário acessa o Swagger
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/webjars/**")).permitAll() // Qualquer usuário acessa o Swagger
                                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.OPTIONS, "/**")).permitAll() //HTTP do tipo OPTIONS para qualquer caminho são permitidas para todos os usuários, independentemente de estarem autenticados ou não. Essas solicitações são frequentemente usadas por navegadores e outros clientes para testar a disponibilidade de um serviço
                                .anyRequest().authenticated()
                )
                //Não será formulário de Login e sim TOKEN para validação:
                //Filtros, devem ser na ordem considerando before e after e a ordem de passagem deles aqui:
                //Filtra demais requisições para verificar a presença de token JWT no header HTTP:
                .addFilterBefore(jwtApiAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class) //Ordem 1 dos filtros -> Autenticação do Token
                //Filtra requisições de Login para autenticação: -> Quando executar contexto/login ele vai tentar tentar autenticar o usuário com as informações de usuario e senha enviadas por JSON!
                //Se tiver tudo OK, autenticado, ele vai gerar um TOKEN de resposta para o usuário utilizar nas requisições!
                .addFilterBefore(jwtLoginFilter(configuration), UsernamePasswordAuthenticationFilter.class) //Ordem 2 dos filtros -> Login Usuário e senha
                .logout(logout -> {
                    logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout")); // Mapeia URL de logout e invalida usuário autenticado
                    logout.logoutSuccessUrl("/index").permitAll(); // Ao fazer logout volta para página de login! testar direto na url colocando /logout que vai fazer
                });

        return http.build();
    }

    /**
     * Configuração sem usar dados em memória de login, vai checar direto no banco de dados pelo UserDetailsService do Spring (Classe que implementamos UserDetailImplService)
     *
     * @param configuration AuthenticationConfiguration
     * @return AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
