package com.ldsystems.api.rest.springbootapirest.controller;

import com.ldsystems.api.rest.springbootapirest.model.Usuario;
import com.ldsystems.api.rest.springbootapirest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@RestController //Arquitetura REST
@RequestMapping(value = "/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

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
    public ResponseEntity<?> init(@RequestParam(value = "nome", defaultValue = "estudante", required = false) String nome, @RequestParam(value = "salario", required = false) BigDecimal salario) {
        System.out.println("Nome: " + nome);
        System.out.println("Salário: " + salario);

        StringBuilder str = new StringBuilder();
        str.append("Olá, seja bem vindo ").append(nome);
        str.append(" Bora Brasil. Seu salário: ").append(salario == null ? BigDecimal.valueOf(0).toString() : salario.toString());
        return new ResponseEntity<>(str.toString(), HttpStatus.OK);
    }

    @GetMapping(value = "/teste", produces = "application/json")
    public ResponseEntity<Usuario> getUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setLogin("admin");
        usuario.setSenha("123");
        usuario.setNome("Administrador Teste");

        return ResponseEntity.ok(usuario);
    }

    @GetMapping(value = "/listteste", produces = "application/json")
    public ResponseEntity<List<Usuario>> getUsuariosTesteManual() {
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

        List<Usuario> listUsuarios = new ArrayList<>();
        listUsuarios.add(usuario);
        listUsuarios.add(usuario2);

        return ResponseEntity.ok(listUsuarios);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<?> getUsuarioPorID(@PathVariable(value = "id") Long id) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        return optionalUsuario.isPresent() ? ResponseEntity.ok(optionalUsuario.get()) : new ResponseEntity<>("Nenhum usuário encontrado!", HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/listall", produces = "application/json")
    public ResponseEntity<?> getListUsuario() {
        List<Usuario> listUsuario = usuarioRepository.findAll();
        listUsuario.sort(Comparator.comparing(Usuario::getId));

        return ResponseEntity.ok(listUsuario);
    }

    //Simulando um retorno de um relatório:
    //Exemplo URL -> http://localhost:8080/springbootapirest/usuario/10/venda/299
    @GetMapping(value = "/{id}/venda/{codigovenda}", produces = "application/pdf")
    public ResponseEntity<?> getRelatorioPdfApenasTeste(@PathVariable(value = "id") Long id, @PathVariable(value = "codigovenda") Long codigovenda) {
        System.out.println("Id: " + id + ", Venda: " + codigovenda);

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        //O retorno seria um Relatório
        //Teria que criar a rotina de gerar o PDF e retornar ele!
        return optionalUsuario.isPresent() ? ResponseEntity.ok(optionalUsuario.get()) : new ResponseEntity<>("Nenhum usuário encontrado!", HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/", produces = "application/json")
    public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody Usuario usuario) {
        Usuario usuarioSave = usuarioRepository.save(usuario);
        return ResponseEntity.ok(usuarioSave);
    }

    @PutMapping(value = "/", produces = "application/json")
    public ResponseEntity<Usuario> atualizarUsuario(@RequestBody Usuario usuario) {
        Usuario usuarioSave = usuarioRepository.save(usuario);
        return ResponseEntity.ok(usuarioSave);
    }
}
