package com.ldsystems.api.rest.springbootapirest.controller;

import com.ldsystems.api.rest.springbootapirest.model.Usuario;
import com.ldsystems.api.rest.springbootapirest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

//@CrossOrigin dessa forma qualquer sistema poderá acessar esse RestController
@CrossOrigin(origins = "*")  // Forma default, qualquer sistema poderá acessar esse RestController
//@CrossOrigin(origins = {"https://www.ldsystems.com.br", "https://www.google.com.br"}) // Só serão aceitas requisições vindas dessa URL
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

    //@CrossOrigin dessa forma qualquer sistema poderá acessar esse RestController
    @CrossOrigin(origins = "*")  //Forma default, qualquer sistema poderá acessar esse RestController
//    @CrossOrigin(origins = "https://www.ldsystems.com.br", methods = {RequestMethod.GET})
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

    //Simulando controle de versão direto na URI (v1)
    @GetMapping(value = "v1/{id}", produces = "application/json")
    public ResponseEntity<?> getUsuarioPorIDV1(@PathVariable(value = "id") Long id) {
        System.out.println("Executando versão 1!");
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        return optionalUsuario.isPresent() ? ResponseEntity.ok(optionalUsuario.get()) : new ResponseEntity<>("Nenhum usuário encontrado!", HttpStatus.NOT_FOUND);
    }

    //Simulando controle de versão direto na URI (v2
    @GetMapping(value = "v2/{id}", produces = "application/json")
    public ResponseEntity<?> getUsuarioPorIDV2(@PathVariable(value = "id") Long id) {
        System.out.println("Executando versão 2!");
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        return optionalUsuario.isPresent() ? ResponseEntity.ok(optionalUsuario.get()) : new ResponseEntity<>("Nenhum usuário encontrado!", HttpStatus.NOT_FOUND);
    }

    //Simulando controle de versão por header (v1), deve ser passado como parâmetro no header da requisição!
    @GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-Version=v1")
    public ResponseEntity<?> getUsuarioPorIDHeaderV1(@PathVariable(value = "id") Long id) {
        System.out.println("Executando versão Header 1!");
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        return optionalUsuario.isPresent() ? ResponseEntity.ok(optionalUsuario.get()) : new ResponseEntity<>("Nenhum usuário encontrado!", HttpStatus.NOT_FOUND);
    }

    //Simulando controle de versão por header (v2), deve ser passado como parâmetro no header da requisição!
    @GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-Version=v2")
    public ResponseEntity<?> getUsuarioPorIDHeaderV2(@PathVariable(value = "id") Long id) {
        System.out.println("Executando versão Header 2!");
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        return optionalUsuario.isPresent() ? ResponseEntity.ok(optionalUsuario.get()) : new ResponseEntity<>("Nenhum usuário encontrado!", HttpStatus.NOT_FOUND);
    }

    //Fazer o processo em cache, simulando um processo lento que demore 6 segundos essa consulta (Thread)
    //Se tiver em cache, a mesma consulta depois vai trazer rapidão pois já vai estar em cache!
    @Cacheable("cache_usuario_listall") //Essa opção por default só refaz a consulta se tiver alteração nos parâmetros da entrada, caso contrário fica sempre em cache
    @GetMapping(value = "/listall", produces = "application/json")
    public ResponseEntity<?> getListUsuario() {
        List<Usuario> listUsuario = usuarioRepository.findAll();
        listUsuario.sort(Comparator.comparing(Usuario::getId));

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

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
        Usuario usuarioSave = null;

        if (usuario != null) {
            if (usuario.getListTelefone() != null) {
                usuario.getListTelefone().forEach(telefone -> {
                    telefone.setUsuario(usuario);
                });
            }

            //Criptografando Senha:
            usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));

            usuarioSave = usuarioRepository.save(usuario);
        }

        return ResponseEntity.ok(usuarioSave);
    }

    @PutMapping(value = "/", produces = "application/json")
    public ResponseEntity<?> atualizarUsuario(@RequestBody Usuario usuario) {
        Usuario usuarioSave;

        if (usuario != null
                && usuario.getId() != null) {
            Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuario.getId());
            if (optionalUsuario.isPresent()) {
                if (usuario.getListTelefone() != null) {
                    usuario.getListTelefone().forEach(telefone -> {
                        telefone.setUsuario(usuario);
                    });
                }

                //Verifica se a senha for enviada uma atualização:
                if (optionalUsuario.get().getSenha() != null
                        && usuario.getSenha() != null
                        && !optionalUsuario.get().getSenha().equals(usuario.getSenha())) {
                    //Criptografando Senha:
                    usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));
                }

                usuarioSave = usuarioRepository.save(usuario);
            } else {
                return ResponseEntity.ok("Usuário com ID(" + usuario.getId().toString() + ") " + "não encontrado!");
            }
        } else {
            return ResponseEntity.ok("Deve ser informado um ID para o usuário que deseja atualizar!");
        }

        return ResponseEntity.ok(usuarioSave);
    }

    @DeleteMapping(value = "/{id}", produces = "application/text")
    public ResponseEntity<?> deletaUsuario(@PathVariable(value = "id") Long id) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            usuarioRepository.deleteById(id);
            return ResponseEntity.ok("Usuário deletado com sucesso!");
        } else {
            return ResponseEntity.ok("Usuário não encontrado!");
        }
    }
}
