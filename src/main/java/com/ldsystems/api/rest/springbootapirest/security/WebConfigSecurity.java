package com.ldsystems.api.rest.springbootapirest.security;

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
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * Mapeia URL's, endereços, autoriza ou bloqueia acessos a URL
 */
@Configuration
@EnableWebSecurity
public class WebConfigSecurity {

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
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/login")).permitAll() // Qualquer usuário acessa o login
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/")).permitAll() // Qualquer usuário acessa o login
                                .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.OPTIONS, "/**")).permitAll() //HTTP do tipo OPTIONS para qualquer caminho são permitidas para todos os usuários, independentemente de estarem autenticados ou não. Essas solicitações são frequentemente usadas por navegadores e outros clientes para testar a disponibilidade de um serviço
                                .anyRequest().authenticated()
                )
                //Não será formulário de Login e sim TOKEN para validação:
                //Filtra requisições de Login para autenticação: -> Quando executar contexto/login ele vai tentar tentar autenticar o usuário com as informações de usuario e senha enviadas por JSON!
                //Se tiver tudo OK, autenticado, ele vai gerar um TOKEN de resposta para o usuário utilizar nas requisições!
                .addFilterBefore(new JWTLoginFilter("/login", authenticationManager(configuration)), UsernamePasswordAuthenticationFilter.class)
                //Filtra demais requisições para verificar a presença de token JWT no header HTTP:
                .addFilterBefore(new JWTAPIAutenticacaoFilter(), UsernamePasswordAuthenticationFilter.class)
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
