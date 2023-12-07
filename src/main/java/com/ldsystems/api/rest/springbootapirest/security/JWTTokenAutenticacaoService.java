package com.ldsystems.api.rest.springbootapirest.security;

import com.ldsystems.api.rest.springbootapirest.ApplicationContextLoad;
import com.ldsystems.api.rest.springbootapirest.model.Usuario;
import com.ldsystems.api.rest.springbootapirest.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;

/**
 * Service de Autenticação (Aula 44)
 */
@Service
@Component
public class JWTTokenAutenticacaoService {

    //Tempo de expiração do nosso Token (Milisegundos 172800000 - 2 dias):
    //A cada 2 dias ele vai fazer uma nova autenticação
    private static final long EXPIRATION_TIME = 172800000;

    //Uma senha única para compor a autenticação:
    private static final String SECRET = "*36dP_)(!s2S@)_SenhaExtremamenteSecreta|LdSystems|@*#__@RESTAPI*_)@!1";

    //Prefixo do Token -> Fixo Bearer
    private static final String TOKEN_PREFIX = "Bearer";

    //Identificação do cabeçalho da resposta do Token -> Fixo Authorization
    private static final String HEADER_STRING = "Authorization";

    /**
     * Gerando token de autenticação e adicionando o cabeçalho e a resposta HTTP
     */
    public void addAuthentication(HttpServletResponse response, String username) throws IOException {
        //Montagem do Token:
        //Referente a aula 44 -> Muita coisa depreciada, vamos alinhar isso quando for rodar!
        String jwt = Jwts.builder()
                .subject(username) //Adiciona o Usuário
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) //Expiration -> Data atual + 2 dias
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS512).compact(); //Compactação e algoritmos de geração de senha

        //Junta o token com o prefixo Bearer:
        String token = TOKEN_PREFIX + " " + jwt; //Exemplo: Bearer 943574395794357493574398543958

        //Adiciona no cabeçalho de resposta:
        response.addHeader(HEADER_STRING, token); //Authorization: Bearer 943574395794357493574398543958
        //Adiciona no corpo da resposta em formato JSON:
        response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
    }

    /**
     * Retorna o usuário validado com token ou caso não seja válido, retorna null
     */
    public Authentication getAuthentication(HttpServletRequest request) {
        //Pega o token enviado no cabaçalho HTTP:
        String token = request.getHeader(HEADER_STRING);

        if (token != null) {
            //Faz a validação do Token do usuário na requisição:
            // Use a chave secreta para verificar o token
            //Referente a aula 44 -> Muita coisa depreciada, vamos alinhar isso quando for rodar!
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET) //Bearer 943574395794357493574398543958
                    .build()
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))//943574395794357493574398543958
                    .getBody();

            String user = claims.getSubject(); //andre

            if (user != null) {
                Usuario usuario = ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class).findUsuarioByLogin(user);

                if (usuario != null) {
                    return new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities());
                }
            }
        }

        //Não autorizado:
        return null;
    }
}
