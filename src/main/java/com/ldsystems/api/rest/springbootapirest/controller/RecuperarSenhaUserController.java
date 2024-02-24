package com.ldsystems.api.rest.springbootapirest.controller;

import com.ldsystems.api.rest.springbootapirest.exception.ObjetoErro;
import com.ldsystems.api.rest.springbootapirest.model.TokenRecuperacaoSenha;
import com.ldsystems.api.rest.springbootapirest.model.Usuario;
import com.ldsystems.api.rest.springbootapirest.model.dto.TokenRecuperacaoSenhaDTO;
import com.ldsystems.api.rest.springbootapirest.repository.TokenRecuperacaoSenhaRepository;
import com.ldsystems.api.rest.springbootapirest.repository.UsuarioRepository;
import com.ldsystems.api.rest.springbootapirest.service.EnviarEmailService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Date;

@RestController
@RequestMapping(value = "/recuperarsenhauser")
public class RecuperarSenhaUserController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenRecuperacaoSenhaRepository tokenRecuperacaoSenhaRepository;

    @Autowired
    private EnviarEmailService enviarEmailService;

    @PostMapping(value = "/", produces = "application/json")
    @Transactional
//    @ResponseBody
    public ResponseEntity<ObjetoErro> recuperarSenhaUser(@RequestBody Usuario usuario) throws Exception {
        ObjetoErro objetoErro = new ObjetoErro();
        Usuario userCharged = null;

        if (usuario != null
                && usuario.getLogin() != null
                && !usuario.getLogin().trim().isEmpty()) {
            userCharged = usuarioRepository.findUsuarioByLogin(usuario.getLogin());
        }

        if (userCharged == null) {
            objetoErro.setErro("Usuário não encontrado!");
            objetoErro.setCodigo(HttpStatus.NOT_FOUND.value() + " ==> " + HttpStatus.NOT_FOUND.getReasonPhrase());
            objetoErro.setTimestamp(LocalDateTime.now());
            objetoErro.setException("java.lang.Exception");
            objetoErro.setPath("../recuperarsenhauser/");
        } else {
            //Enviar E-mail:
            Date dataExpiracao = enviarEmailService.getDataExpiracaoNovoToken();
            String token = enviarEmailService.getTokenEnvioRecuperacaoSenha(userCharged.getId(), dataExpiracao);

            if (dataExpiracao != null
                    && token != null) {
                StringBuilder str = new StringBuilder();
                str.append("<div class=\"container\">");
                str.append("<h1>Recuperação de Acesso</h1>");
                str.append("<p>Olá ").append(userCharged.getNome()).append("</p>");
                str.append("<p>Você solicitou a recuperação de acesso para sua conta no sistema.</p>");
                str.append("<p>Para redefinir sua senha, clique no botão abaixo e siga as instruções.</p>");
                str.append("<a href=\"").append(enviarEmailService.getLinkUserRecuperacaoSenha(token, userCharged.getEmail())).append("\" target=\"_blank\" class=\"btn btn -primary\">Redefinir Senha</a>");
                str.append("<br/>");
                str.append("<br/>");
                str.append("<p>Esse link tem expiração de 2 horas.</p>");
                str.append("<p>Atenciosamente,</p>");
                str.append("<p>LDSystems</p>");
                str.append("</div>");

                enviarEmailService.enviarEmail("Recuperação de Acesso", userCharged.getEmail(), str.toString());
                //Grava Token para o Usuário:
                enviarEmailService.saveUsuarioToken(userCharged.getId(), dataExpiracao, token);

                //Objeto erro setado como 200 (Sucesso)
                objetoErro.setErro("Acesso enviado para o seu e-mail");
                objetoErro.setCodigo(HttpStatus.OK.value() + " ==> " + HttpStatus.OK.getReasonPhrase());
                objetoErro.setTimestamp(LocalDateTime.now());
                objetoErro.setException("java.lang.Exception");
                objetoErro.setPath("../recuperarsenhauser/");
            }
        }

        return ResponseEntity.ok(objetoErro);
    }

    @PostMapping(value = "/resetpassword", produces = "application/json")
    @Transactional
    public ResponseEntity<?> recuperarSenhaUser(@RequestBody TokenRecuperacaoSenhaDTO tokenRecuperacaoSenhaDto) throws Exception {
        if (tokenRecuperacaoSenhaDto != null
                && tokenRecuperacaoSenhaDto.getToken() != null
                && tokenRecuperacaoSenhaDto.getSenha() != null) {
            TokenRecuperacaoSenha tokenChargedSystem = tokenRecuperacaoSenhaRepository.findTokenRecuperacaoSenhaByToken(tokenRecuperacaoSenhaDto.getToken());
            if (tokenChargedSystem != null
                    && tokenChargedSystem.getId() != null
                    && tokenChargedSystem.getToken() != null
                    && tokenChargedSystem.getDataExpiracao() != null
                    && tokenChargedSystem.getUsuario() != null
                    && tokenChargedSystem.getUsuario().getId() != null
                    && tokenChargedSystem.getUtilizado() != null) {
                if (tokenChargedSystem.getUtilizado()) {
                    throw new Exception("Token de redefinição de senha inválido.\nJá foi utilizado anteriormente, gere a redefinição de senha novamente no sistema e tente novamente por um novo link enviado ao seu E-mail.");
                }

                if (this.enviarEmailService.getValidTokenRecuperacaoSenha(tokenRecuperacaoSenhaDto)) {
                    //1) Altera Senha (com criptografia):
                    tokenRecuperacaoSenhaRepository.atualizaSenhaUsuario(tokenChargedSystem.getUsuario().getId(), new BCryptPasswordEncoder().encode(tokenRecuperacaoSenhaDto.getSenha()));

                    //2) Desativa o Token para não ser utilizado novamente:
                    tokenRecuperacaoSenhaRepository.desativaTokenUsuarioSenhaAlterada(tokenChargedSystem.getId());
                }

                return ResponseEntity.ok(new TokenRecuperacaoSenhaDTO());
            } else {
                throw new Exception("Token de redefinição de senha inválido.\nCertifique-se de que está usando o link do último e-mail enviado pelo Sistema.");
            }
        } else {
            throw new Exception("Deve ser enviado por parâmetro a página que quer visualizar.");
        }
    }
}
