package com.ldsystems.api.rest.springbootapirest.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ldsystems.api.rest.springbootapirest.model.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * Estabelece nosso gerenciador de Token (Aula 45)
 */
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

    private HandlerExceptionResolver exceptionResolver;

    //Obrigamos a autenticar a URL
    @Autowired
    public JWTLoginFilter(AuthenticationManager authenticationManager, @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver) {
        super(new AntPathRequestMatcher("/login"), authenticationManager);
        this.exceptionResolver = exceptionResolver;
    }

    //Retorna o usuário ao processar a autenticação
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        try {
            System.out.println("Validando as credenciais de Login!");
            //Pegando o Token para validar e retornar o usuario:
            Usuario usuario = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);
            return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities()));
        } catch (Exception ex) {
            exceptionResolver.resolveException((HttpServletRequest) request, (HttpServletResponse) response, null, ex);
        }

        return null;
    }

    //Depois que autenticou com sucesso, pega o token na resposta
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        try {
            System.out.println("Usuário autenticado com sucesso!");
            new JWTTokenAutenticacaoService().addAuthentication(response, authResult.getName());
        } catch (Exception ex) {
            exceptionResolver.resolveException((HttpServletRequest) request, (HttpServletResponse) response, null, ex);
        }
    }
}
