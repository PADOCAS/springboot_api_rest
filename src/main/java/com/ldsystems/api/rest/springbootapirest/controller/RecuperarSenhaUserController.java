package com.ldsystems.api.rest.springbootapirest.controller;

import com.ldsystems.api.rest.springbootapirest.exception.ObjetoErro;
import com.ldsystems.api.rest.springbootapirest.model.Usuario;
import com.ldsystems.api.rest.springbootapirest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/recuperarsenhauser")
public class RecuperarSenhaUserController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping(value = "/", produces = "application/json")
//    @ResponseBody
    public ResponseEntity<ObjetoErro> recuperarSenhaUser(@RequestBody Usuario usuario) {
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
            //Todo: Envio do E-mail:

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
