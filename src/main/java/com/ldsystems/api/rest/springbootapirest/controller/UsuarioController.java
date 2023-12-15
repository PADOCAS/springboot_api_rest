package com.ldsystems.api.rest.springbootapirest.controller;

import com.google.gson.Gson;
import com.ldsystems.api.rest.springbootapirest.model.Usuario;
import com.ldsystems.api.rest.springbootapirest.model.dto.UsuarioDTO;
import com.ldsystems.api.rest.springbootapirest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public ResponseEntity<UsuarioDTO> getUsuario() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setLogin("admin");
        usuario.setSenha("123");
        usuario.setNome("Administrador Teste");

        //Retornando um DTO -> UsuarioDTO:
        return ResponseEntity.ok(new UsuarioDTO(usuario));
    }

    //@CrossOrigin dessa forma qualquer sistema poderá acessar esse RestController
    @CrossOrigin(origins = "*")  //Forma default, qualquer sistema poderá acessar esse RestController
//    @CrossOrigin(origins = "https://www.ldsystems.com.br", methods = {RequestMethod.GET})
    @GetMapping(value = "/listteste", produces = "application/json")
    public ResponseEntity<List<UsuarioDTO>> getUsuariosTesteManual() {
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

        //Retornando uma lista de DTO -> UsuarioDTO:
        List<UsuarioDTO> listUsuarios = new ArrayList<>();
        listUsuarios.add(new UsuarioDTO(usuario));
        listUsuarios.add(new UsuarioDTO(usuario2));

        return ResponseEntity.ok(listUsuarios);
    }

    //Simulando controle de versão direto na URI (v1)
    @GetMapping(value = "v1/{id}", produces = "application/json")
    public ResponseEntity<?> getUsuarioPorIDV1(@PathVariable(value = "id") Long id) {
        System.out.println("Executando versão 1!");
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        //Retornando um DTO -> UsuarioDTO:
        return optionalUsuario.isPresent() ? ResponseEntity.ok(new UsuarioDTO(optionalUsuario.get())) : new ResponseEntity<>("Nenhum usuário encontrado!", HttpStatus.NOT_FOUND);
    }

    //Simulando controle de versão direto na URI (v2
    @GetMapping(value = "v2/{id}", produces = "application/json")
    public ResponseEntity<?> getUsuarioPorIDV2(@PathVariable(value = "id") Long id) {
        System.out.println("Executando versão 2!");
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        //Retornando um DTO -> UsuarioDTO:
        return optionalUsuario.isPresent() ? ResponseEntity.ok(new UsuarioDTO(optionalUsuario.get())) : new ResponseEntity<>("Nenhum usuário encontrado!", HttpStatus.NOT_FOUND);
    }

    //Simulando controle de versão por header (v1), deve ser passado como parâmetro no header da requisição!
    @GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-Version=v1")
    public ResponseEntity<?> getUsuarioPorIDHeaderV1(@PathVariable(value = "id") Long id) {
        System.out.println("Executando versão Header 1!");
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        //Retornando um DTO -> UsuarioDTO:
        return optionalUsuario.isPresent() ? ResponseEntity.ok(new UsuarioDTO(optionalUsuario.get())) : new ResponseEntity<>("Nenhum usuário encontrado!", HttpStatus.NOT_FOUND);
    }

    //Simulando controle de versão por header (v2), deve ser passado como parâmetro no header da requisição!
    @GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-Version=v2")
    public ResponseEntity<?> getUsuarioPorIDHeaderV2(@PathVariable(value = "id") Long id) {
        System.out.println("Executando versão Header 2!");
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        //Retornando um DTO -> UsuarioDTO:
        return optionalUsuario.isPresent() ? ResponseEntity.ok(new UsuarioDTO(optionalUsuario.get())) : new ResponseEntity<>("Nenhum usuário encontrado!", HttpStatus.NOT_FOUND);
    }

    //Fazer o processo em cache, simulando um processo lento que demore 6 segundos essa consulta (Thread)
    //Se tiver em cache, a mesma consulta depois vai trazer rapidão pois já vai estar em cache!
//    @Cacheable("cache_usuario_listall") //Essa opção por default só refaz a consulta se tiver alteração nos parâmetros da entrada, caso contrário fica sempre em cache
    @CacheEvict(value = "cacheUsuarios", allEntries = true)
//Vai deixar o cache sempre limpo, mesmo que não haja iterações na tabela
    @CachePut(value = "cacheUsuarios")
//Qualquer mudança nas tabelas envolvidas vai atualizar o cache com os dados atualizados!
    @GetMapping(value = "/listall", produces = "application/json")
    public ResponseEntity<?> getListUsuario() {
        List<Usuario> listUsuario = usuarioRepository.findAll();
        listUsuario.sort(Comparator.comparing(Usuario::getId));

//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        //Retornando uma list DTO -> UsuarioDTO:
        return ResponseEntity.ok(listUsuario.stream()
                .map(usuario -> new UsuarioDTO(usuario))
                .collect(Collectors.toList()));
    }

    //Simulando um retorno de um relatório:
    //Exemplo URL -> http://localhost:8080/springbootapirest/usuario/10/venda/299
    @GetMapping(value = "/{id}/venda/{codigovenda}", produces = "application/pdf")
    public ResponseEntity<?> getRelatorioPdfApenasTeste(@PathVariable(value = "id") Long id, @PathVariable(value = "codigovenda") Long codigovenda) {
        System.out.println("Id: " + id + ", Venda: " + codigovenda);

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        //O retorno seria um Relatório
        //Teria que criar a rotina de gerar o PDF e retornar ele!
        //Retornando um DTO -> UsuarioDTO:
        return optionalUsuario.isPresent() ? ResponseEntity.ok(new UsuarioDTO(optionalUsuario.get())) : new ResponseEntity<>("Nenhum usuário encontrado!", HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/", produces = "application/json")
    public ResponseEntity<Usuario> cadastrarUsuario(@RequestBody Usuario usuario) throws Exception {
        Usuario usuarioSave = null;

        if (usuario != null) {
            if (usuario.getListTelefone() != null) {
                usuario.getListTelefone().forEach(telefone -> {
                    telefone.setUsuario(usuario);
                });
            }

            //Consumindo API externa VIACEP:
            chargedCep(usuario);

            //Criptografando Senha:
            usuario.setSenha(new BCryptPasswordEncoder().encode(usuario.getSenha()));

            usuarioSave = usuarioRepository.save(usuario);
        }

        return ResponseEntity.ok(usuarioSave);
    }

    @PutMapping(value = "/", produces = "application/json")
    public ResponseEntity<?> atualizarUsuario(@RequestBody Usuario usuario) throws Exception {
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

                //Consumindo API externa VIACEP:
                chargedCep(usuario);

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

    private void chargedCep(Usuario usuario) throws Exception {
        if (usuario != null
                && usuario.getCep() != null) {
            //Valida CEP:
            validaCepInformado(usuario.getCep());

            try {
                URL url = new URL("https://viacep.com.br/ws/" + usuario.getCep() + "/json/");
                URLConnection urlConnection = url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String cep = "";
                StringBuilder strJsonCep = new StringBuilder();

                //Varre as linhas:
                while ((cep = bufferedReader.readLine()) != null) {
                    strJsonCep.append(cep);
                }

                Usuario usuarioPreenchimentoCepAuxiliar = new Gson().fromJson(strJsonCep.toString(), Usuario.class);
                if (usuarioPreenchimentoCepAuxiliar != null) {
                    usuario.setLogradouro(usuarioPreenchimentoCepAuxiliar.getLogradouro() == null || usuarioPreenchimentoCepAuxiliar.getLogradouro().trim().isEmpty() ? null : usuarioPreenchimentoCepAuxiliar.getLogradouro().toUpperCase());
                    usuario.setBairro(usuarioPreenchimentoCepAuxiliar.getBairro() == null || usuarioPreenchimentoCepAuxiliar.getBairro().trim().isEmpty() ? null : usuarioPreenchimentoCepAuxiliar.getBairro().toUpperCase());
                    usuario.setComplemento(usuarioPreenchimentoCepAuxiliar.getComplemento() == null || usuarioPreenchimentoCepAuxiliar.getComplemento().trim().isEmpty() ? null : usuarioPreenchimentoCepAuxiliar.getComplemento());
                    usuario.setUf(usuarioPreenchimentoCepAuxiliar.getUf() == null || usuarioPreenchimentoCepAuxiliar.getUf().trim().isEmpty() ? null : usuarioPreenchimentoCepAuxiliar.getUf().toUpperCase());
                    usuario.setLocalidade(usuarioPreenchimentoCepAuxiliar.getLocalidade() == null || usuarioPreenchimentoCepAuxiliar.getLocalidade().trim().isEmpty() ? null : usuarioPreenchimentoCepAuxiliar.getLocalidade().toUpperCase());
                }
            } catch (Exception ex) {
                throw new Exception("CEP inválido. Informe um CEP Válido para prosseguir.");
            }
        }
    }

    private void validaCepInformado(String cep) throws Exception {
        if (cep != null) {
            if (cep.length() > 8) {
                throw new Exception("Cep inválido! Deve ter no máximo 8 digitos.");
            }

            if (temCaracteresEspeciais(cep)) {
                throw new Exception("Cep inválido! Apenas números devem ser informados!");
            }
        }
    }

    public static boolean temCaracteresEspeciais(String cep) {
        if (cep != null) {
            for (int i = 0; i < cep.length(); i++) {
                if (!Character.isDigit(cep.charAt(i))) {
                    return true;
                }
            }
        }

        return false;
    }
}
