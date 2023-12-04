package com.ldsystems.api.rest.springbootapirest.controller;

import com.ldsystems.api.rest.springbootapirest.model.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController //Arquitetura REST
@RequestMapping(value = "/index")
public class IndexController {

    /**
     * Método para retornar uma mensagem ao receber uma requisição
     *
     * @param nome    (Nome do Usuário)
     * @param salario (Salário do Usuário)
     * @return ResponseEntity com a mensagem de saudação
     */
    //Serviço RESTful que deve receber um parâmetro nome!
    //defaultValue, caso não passar o parametro nome, vai assumir esse default!
    //required default é SIM, se você colocar false ele não vai exigir para chamada do método!
    @GetMapping(value = "/", produces = "application/json")
    public ResponseEntity init(@RequestParam(value = "nome", defaultValue = "estudante", required = false) String nome, @RequestParam(value = "salario", required = false) BigDecimal salario) {
        System.out.println("Nome: " + nome);
        System.out.println("Salário: " + salario);

        StringBuilder str = new StringBuilder();
        str.append("Olá, seja bem vindo ").append(nome);
        str.append(" Bora Brasil. Seu salário: ").append(salario == null ? BigDecimal.valueOf(0).toString() : salario.toString());
        return new ResponseEntity(str.toString(), HttpStatus.OK);
    }

    @GetMapping(value = "/usuario", produces = "application/json")
    public ResponseEntity<Usuario> getUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setLogin("admin");
        usuario.setSenha("123");
        usuario.setNome("Administrador Teste");

        return ResponseEntity.ok(usuario);
    }

    @GetMapping(value = "/usuarios", produces = "application/json")
    public ResponseEntity<List<Usuario>> getUsuarios() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setLogin("admin");
        usuario.setSenha("123");
        usuario.setNome("Administrador Teste");

        Usuario usuario2 = new Usuario();
        usuario2.setId(1L);
        usuario2.setLogin("padoca");
        usuario2.setSenha("123");
        usuario2.setNome("Padoca");

        List<Usuario> listUsuarios = new ArrayList<Usuario>();
        listUsuarios.add(usuario);
        listUsuarios.add(usuario2);

        return ResponseEntity.ok(listUsuarios);
    }
}
