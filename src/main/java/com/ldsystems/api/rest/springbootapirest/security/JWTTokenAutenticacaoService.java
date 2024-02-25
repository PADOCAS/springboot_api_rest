package com.ldsystems.api.rest.springbootapirest.security;

import com.ldsystems.api.rest.springbootapirest.ApplicationContextLoad;
import com.ldsystems.api.rest.springbootapirest.model.Usuario;
import com.ldsystems.api.rest.springbootapirest.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

/**
 * Service de Autenticação (Aula 44)
 */
@Service
@Component
public class JWTTokenAutenticacaoService {

    //Tempo de expiração do nosso ‘Token’ (Milisegundos 172800000 - 2 dias):
    //A cada 2 dias ele vai fazer uma nova autenticação
    private static final long EXPIRATION_TIME = 172800000;

    //Uma senha única para compor a autenticação:
    private static final String SECRET = "dashdSLSDS22332sdd2SSDAAa3457974hgbEERFf8432836dPs2SSenhaExtremamenteSecretaLdSystemsRESTAPI1890udndfh3SDDF2";

    //Key gerada com a chave secreta em algoritmo de assinatura HS512:
    private static final Key SIGNING_KEY = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());

    //Prefixo do Token -> Fixo Bearer
    private static final String TOKEN_PREFIX = "Bearer";

    //Identificação do cabeçalho da resposta do ‘Token’ -> Fixo Authorization
    private static final String HEADER_STRING = "Authorization";

    /**
     * Gerando ‘token’ de autenticação e adicionando o cabeçalho e a resposta HTTP
     */
    public void addAuthentication(HttpServletResponse response, String username) throws IOException {
        //Montagem do ‘Token’:
        //Muita coisa depreciada, vamos alinhar isso quando for rodar!
        String jwt = Jwts.builder()
                .subject(username) //Adiciona o Usuário
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) //Expiration -> Data atual + 2 dias
                .signWith(SIGNING_KEY, SignatureAlgorithm.HS512).compact(); //Compactação e algoritmos de geração de senha
        //   Keys.hmacShaKeyFor(SECRET.getBytes()), SignatureAlgorithm.HS512)

        //Junta o token com o prefixo Bearer:
        String token = TOKEN_PREFIX + " " + jwt; //Exemplo: Bearer 943574395794357493574398543958

        //Adiciona no cabeçalho de resposta:
        response.addHeader(HEADER_STRING, token); //Authorization: Bearer 943574395794357493574398543958

        //Atualiza Token do Usuário:
        ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class).atualizaTokenUsuario(username, token.replace(TOKEN_PREFIX, "").replaceAll("\\s", ""));

        //Liberando respostas -> Permite que seu servidor seja acessível por qualquer aplicativo, independentemente de onde ele esteja hospedado:
        liberacaoCors(response);
        //Adiciona no corpo da resposta em formato JSON:
        response.getWriter().write("{\"Authorization\": \"" + token + "\"}");
    }

    /**
     * Retorna o usuário validado com ‘token’ ou caso não seja válido, retorna null
     */
    public Authentication getAuthentication(HttpServletRequest request, HttpServletResponse response) {
        //Pega o ‘token’ enviado no cabaçalho HTTP:
        String token = request.getHeader(HEADER_STRING);

        if (token != null) {
            //Faz a validação do ‘Token’ do usuário na requisição:
            // Use a chave secreta para verificar o token
            //Muita coisa depreciada, vamos alinhar isso quando for rodar!
            Claims claims = Jwts.parser()
                    .setSigningKey(SIGNING_KEY) //Bearer 943574395794357493574398543958
                    .build()
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, "").replaceAll(" ", "").replaceAll("\\s", ""))//943574395794357493574398543958
                    .getBody();

            String user = claims.getSubject(); //andre

            if (user != null) {
                Usuario usuario = ApplicationContextLoad.getApplicationContext().getBean(UsuarioRepository.class).findUsuarioByLogin(user);

                if (usuario != null) {
                    //Caso quiser fazer uma validação do token enviado com o token gravado no banco de dados para o usuário, colocar a validação aqui!
                    //Caso for diferente não deixa autenticar!
                    //Não achei legal colocar isso ainda, pois o usuário pode criar vários ‘tokens’ para ele sendo válidos (mais de 1), basta entrar /login e vai gerar token
                    //O jeito que foi feita a aula ele fica apenas um token gravado para o usuário o que não faria sentido!! não vamos colocar isso agora:
//                    if(usuario.getToken().equals(token.replace(TOKEN_PREFIX, "").replaceAll("\\s", ""))) {
                    //Liberando respostas -> Permite que seu servidor seja acessível por qualquer aplicativo, independentemente de onde ele esteja hospedado:
                    liberacaoCors(response);
                    return new UsernamePasswordAuthenticationToken(usuario.getLogin(), usuario.getSenha(), usuario.getAuthorities());
//                    }
                }
            }
        }

        //Liberando respostas -> Permite que seu servidor seja acessível por qualquer aplicativo, independentemente de onde ele esteja hospedado:
        liberacaoCors(response);
        //Não autorizado:
        return null;
    }

    /**
     * Permite que seu servidor seja acessível por qualquer aplicativo, independentemente de onde ele esteja hospedado. Isso é útil se você estiver desenvolvendo uma API que será usada por aplicativos de terceiros.
     *
     * @param response HttpServletResponse
     */
    private void liberacaoCors(HttpServletResponse response) {
        //Liberando Resposta para porta diferente:
        if (response.getHeader("Access-Control-Allow-Origin") == null) {
            response.addHeader("Access-Control-Allow-Origin", "*");
        }

        if (response.getHeader("Access-Control-Allow-Headers") == null) {
            response.addHeader("Access-Control-Allow-Headers", "*");
        }

        if (response.getHeader("Access-Control-Request-Headers") == null) {
            response.addHeader("Access-Control-Request-Headers", "*");
        }

        if (response.getHeader("Access-Control-Allow-Methods") == null) {
            response.addHeader("Access-Control-Allow-Methods", "*");
        }
    }
}
