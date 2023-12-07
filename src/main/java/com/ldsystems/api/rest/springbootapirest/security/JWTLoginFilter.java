package com.ldsystems.api.rest.springbootapirest.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ldsystems.api.rest.springbootapirest.model.Usuario;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

/**
 * Estabelece nosso gerenciador de Token (Aula 45)
 */
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

    //Obrigamos a autenticar a URL
    protected JWTLoginFilter(String url, AuthenticationManager authenticationManager) {
        super(new AntPathRequestMatcher(url), authenticationManager);
    }

    //Retorna o usuário ao processar a autenticação
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        System.out.println("Validando as credenciais de Login!");
        //Pegando o Token para validar e retornar o usuario:
        Usuario usuario = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);
        return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities()));
    }

    //Depois que autenticou com sucesso, pega o token na resposta
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("Usuário autenticado com sucesso!");
        new JWTTokenAutenticacaoService().addAuthentication(response, authResult.getName());
    }
}
