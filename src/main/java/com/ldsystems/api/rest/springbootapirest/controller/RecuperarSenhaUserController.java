package com.ldsystems.api.rest.springbootapirest.controller;

import com.ldsystems.api.rest.springbootapirest.exception.ObjetoErro;
import com.ldsystems.api.rest.springbootapirest.model.Usuario;
import com.ldsystems.api.rest.springbootapirest.repository.UsuarioRepository;
import com.ldsystems.api.rest.springbootapirest.service.EnviarEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/recuperarsenhauser")
public class RecuperarSenhaUserController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EnviarEmailService enviarEmailService;

    @PostMapping(value = "/", produces = "application/json")
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
            StringBuilder str = new StringBuilder();
            str.append("<div class=\"container\">");
            str.append("<h1>Recuperação de Acesso</h1>");
            str.append("<p>Olá ").append(userCharged.getNome()).append("</p>");
            str.append("<p>Você solicitou a recuperação de acesso para sua conta no sistema.</p>");
            str.append("<p>Para redefinir sua senha, clique no botão abaixo e siga as instruções.</p>");
            str.append("<a href=\"http://www.uol.com.br\" class=\"btn btn -primary\">Redefinir Senha</a>");
            str.append("<p>Atenciosamente,</p>");
            str.append("<p>LDSystems</p>");
            str.append("</div>");

            enviarEmailService.enviarEmail("Recuperação de Acesso", userCharged.getEmail(), str.toString());

            //Objeto erro setado como 200 (Sucesso)
            objetoErro.setErro("Acesso enviado para o seu e-mail");
            objetoErro.setCodigo(HttpStatus.OK.value() + " ==> " + HttpStatus.OK.getReasonPhrase());
            objetoErro.setTimestamp(LocalDateTime.now());
            objetoErro.setException("java.lang.Exception");
            objetoErro.setPath("../recuperarsenhauser/");
        }

        return ResponseEntity.ok(objetoErro);
    }
}
