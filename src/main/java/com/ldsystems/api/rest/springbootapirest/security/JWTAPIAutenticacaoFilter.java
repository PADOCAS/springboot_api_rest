package com.ldsystems.api.rest.springbootapirest.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

//Filtro onde todas as requisições serão capturadas para autenticação: (Aula 46)
public class JWTAPIAutenticacaoFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //Busca a a autenticação para requisição (caso retornar NULL não estará autenticado):
        Authentication authentication = new JWTTokenAutenticacaoService().getAuthentication((HttpServletRequest) request, (HttpServletResponse) response);
        //Coloca o processo de autenticação no Spring Security:
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
