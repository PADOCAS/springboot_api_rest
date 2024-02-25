package com.ldsystems.api.rest.springbootapirest.service;

import com.ldsystems.api.rest.springbootapirest.model.ConfigGeral;
import com.ldsystems.api.rest.springbootapirest.model.TokenRecuperacaoSenha;
import com.ldsystems.api.rest.springbootapirest.model.Usuario;
import com.ldsystems.api.rest.springbootapirest.model.dto.TokenRecuperacaoSenhaDTO;
import com.ldsystems.api.rest.springbootapirest.repository.ConfigGeralRepository;
import com.ldsystems.api.rest.springbootapirest.repository.TokenRecuperacaoSenhaRepository;
import com.ldsystems.api.rest.springbootapirest.repository.UsuarioRepository;
import com.ldsystems.api.rest.springbootapirest.util.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;

@Service
public class EnviarEmailService {

    @Autowired
    private ConfigGeralRepository configGeralRepository;

    @Autowired
    private TokenRecuperacaoSenhaRepository tokenRecuperacaoSenhaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    //Uma senha única para compor a autenticação:
    private static final String SECRET = "zou298LSS22332sYSUZzoi1292s9DAAa3457974hgbu2EERFf84dPs2SSenhaExtremamenteSecretaLdSystemsRESTAPI180udndSDDF2";

    //Tempo de expiração do nosso Token (Milisegundos 7200000 -> 2 HORAS):
    private static final long EXPIRATION_TIME = 7200000;

    //Key gerada com a chave secreta em algoritmo de assinatura HS512:
    private static final Key SIGNING_KEY = new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());

    public Date getDataExpiracaoNovoToken() {
        return new Date(System.currentTimeMillis() + EXPIRATION_TIME);
    }

    public String getLinkUserRecuperacaoSenha(String token, String email) {
        StringBuilder str = new StringBuilder();

        if (token != null
                && email != null) {
            str.append(Constants.getUriRecuperacaoSenhaLinkForEmail()).append("?").append("email=").append(email).append("&token=").append(token);
        }

        return str.toString();
    }

    public void saveUsuarioToken(Long usuarioId, Date dataExpiracao, String token) {
        if (usuarioId != null
                && token != null
                && dataExpiracao != null) {
            Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuarioId);
            if (optionalUsuario.isPresent()) {
                TokenRecuperacaoSenha tokenRecuperacaoSenha = new TokenRecuperacaoSenha();
                tokenRecuperacaoSenha.setToken(token);
                tokenRecuperacaoSenha.setUsuario(optionalUsuario.get());
                tokenRecuperacaoSenha.setDataExpiracao(dataExpiracao);
                tokenRecuperacaoSenha.setUtilizado(false);

                tokenRecuperacaoSenhaRepository.save(tokenRecuperacaoSenha);
            }
        }
    }

    public String getTokenEnvioRecuperacaoSenha(Long usuarioId, Date dataExpiracao) {
        StringBuilder str = new StringBuilder();

        if (usuarioId != null
                && dataExpiracao != null) {
            str.append(Jwts.builder()
                    .subject(usuarioId.toString()) //Adiciona o ID do Usuário
                    .expiration(dataExpiracao)
                    .signWith(SIGNING_KEY, SignatureAlgorithm.HS512).compact()); //Compactação e algoritmos de geração de senha
        }

        return str.toString();
    }

    /**
     * Retorna o usuário validado com token ou caso não seja válido, retorna null
     */
    public Boolean getValidTokenRecuperacaoSenha(TokenRecuperacaoSenhaDTO tokenRecuperacaoSenhaDTO) throws Exception {
        try {
            if (tokenRecuperacaoSenhaDTO != null
                    && tokenRecuperacaoSenhaDTO.getToken() != null) {
                //Faz a validação do Token de alteração de senha:
                // Use a chave secreta para verificar o token
                Claims claims = Jwts.parser()
                        .setSigningKey(SIGNING_KEY) //943574395794357493574398543958
                        .build()
                        .parseClaimsJws(tokenRecuperacaoSenhaDTO.getToken().replaceAll("\\s", ""))//943574395794357493574398543958
                        .getBody();

                String userIdStr = claims.getSubject(); //ID do Usuário

                if (userIdStr != null) {
                    Optional<Usuario> usuarioOptional = usuarioRepository.findById(Long.parseLong(userIdStr));

                    if (usuarioOptional.isPresent()) {
                        return true;
                    } else {
                        throw new Exception("Token inválido.\nUsuário não existe mais cadastrado no sistema.");
                    }
                }
            }
        } catch (ExpiredJwtException ex) {
            throw new Exception("Token inválido.\nEsse token já está expirado. Faça a requisição de recuperação de senha novamente no sistema.");
        }

        //Não autorizado:
        return false;
    }

    public void enviarEmail(String assuntoEmail, String emailDestino, String mensagemEmail) throws MessagingException {
        if (emailDestino != null
                && !emailDestino.trim().isEmpty()) {
            ConfigGeral configGeral = configGeralRepository.findUniqueConfigGeral();

            if (configGeral != null
                    && configGeral.getEmail() != null
                    && configGeral.getSenha() != null
                    && configGeral.getSmtpHost() != null
                    && configGeral.getSmtpPort() != null
                    && configGeral.getSocketPort() != null) {
                Properties properties = new Properties();
                properties.put("mail.smtp.ssl.trust", "*"); //Permissão SSL
                properties.put("mail.smtp.auth", true); //Autenticação
                properties.put("mail.smtp.starttls", true); //Autorização
                properties.put("mail.smtp.host", configGeral.getSmtpHost()); //Servidor do Google
                properties.put("mail.smtp.port", configGeral.getSmtpPort()); //Porta Servidor
                properties.put("mail.smtp.socketFactory.port", configGeral.getSocketPort()); //Porta Socket
                properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); //Classe de conexão Socket

                Session session = Session.getInstance(properties, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(configGeral.getEmail(), configGeral.getSenha());
                    }
                });

                Address[] toUser = InternetAddress.parse(emailDestino);
                Message message = new MimeMessage(session);
                // Definir o tipo de conteúdo como HTML
                message.setContent(mensagemEmail, "text/html");
                message.setFrom(new InternetAddress(configGeral.getEmail())); //E-mail Remetente
                message.setRecipients(Message.RecipientType.TO, toUser); //E-mail Destinatário
                message.setSubject(assuntoEmail);
//            message.setText(mensagemEmail);  //Só assim não fica HTML o corpo do email!

                Transport.send(message);
            } else {
                throw new MessagingException("Configuração do Servidor de E-mail não encontrada!\nVerifique com o administrador do sistema para alimentar as informações do servidor de E-mail para o sistema (public.config_geral).\n\nApós feita as configurações necessárias, tente executar a recuperação de acesso novamente.");
            }
        } else {
            throw new MessagingException("E-mail de destino inválido!\nNão foi possível enviar o E-mail de recupação.");
        }
    }
}
