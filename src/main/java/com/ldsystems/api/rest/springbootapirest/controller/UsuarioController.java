package com.ldsystems.api.rest.springbootapirest.controller;

import com.google.gson.Gson;
import com.ldsystems.api.rest.springbootapirest.model.Usuario;
import com.ldsystems.api.rest.springbootapirest.model.dto.UsuarioDTO;
import com.ldsystems.api.rest.springbootapirest.model.dto.UsuarioGraficoDTO;
import com.ldsystems.api.rest.springbootapirest.model.dto.UsuarioReportDTO;
import com.ldsystems.api.rest.springbootapirest.model.dto.UsuarioSalarioDTO;
import com.ldsystems.api.rest.springbootapirest.repository.UsuarioRepository;
import com.ldsystems.api.rest.springbootapirest.repository.specification.UsuarioSpecification;
import com.ldsystems.api.rest.springbootapirest.service.ReportService;
import com.ldsystems.api.rest.springbootapirest.service.UserDetailImplService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

//@CrossOrigin dessa forma qualquer sistema poderá acessar esse RestController
@SuppressWarnings("ALL")
@CrossOrigin(origins = "*")  // Forma default, qualquer sistema poderá acessar esse RestController
//@CrossOrigin(origins = {"https://www.ldsystems.com.br", "https://www.google.com.br"}) // Só serão aceitas requisições vindas dessa URL
@RestController //Arquitetura REST
@RequestMapping(value = "/usuario")
@Tag(name = "Controle de Usuários", description = "Controle de Usuários")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UserDetailImplService userDetailImplService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

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
    @Hidden //Oculta método no swagger
    @Operation(summary = "Teste boas vindas usuário", description = "Teste para receber parâmetros e retornar mensagem com eles para o usuário")
    @SecurityRequirement(name = "bearerAuth") //Exige Token para executar!
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem sucedida!"),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente"),
            @ApiResponse(responseCode = "403", description = "Acesso Negado -> Token expirado, faça Login novamente ou informe um Token válido para autenticação!"),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
    public ResponseEntity<?> init(@RequestParam(value = "nome", defaultValue = "estudante", required = false) String nome, @RequestParam(value = "salario", required = false) BigDecimal salario) {
        System.out.println("Nome: " + nome);
        System.out.println("Salário: " + salario);

        StringBuilder str = new StringBuilder();
        str.append("Olá, seja bem vindo ").append(nome);
        str.append(" Bora Brasil. Seu salário: ").append(salario == null ? BigDecimal.valueOf(0).toString() : salario.toString());
        return new ResponseEntity<>(str.toString(), HttpStatus.OK);
    }

    @Hidden //Oculta método no swagger
    @Operation(summary = "Teste instância de Usuário", description = "Teste para instânciar um usuário e retornar")
    @SecurityRequirement(name = "bearerAuth") //Exige Token para executar!
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem sucedida!"),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente"),
            @ApiResponse(responseCode = "403", description = "Acesso Negado -> Token expirado, faça Login novamente ou informe um Token válido para autenticação!"),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
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
    @Hidden //Oculta método no swagger
    @Operation(summary = "Teste instância lista de Usuário", description = "Teste para instânciar lista de usuário e retornar")
    @SecurityRequirement(name = "bearerAuth") //Exige Token para executar!
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem sucedida!"),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente"),
            @ApiResponse(responseCode = "403", description = "Acesso Negado -> Token expirado, faça Login novamente ou informe um Token válido para autenticação!"),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
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
    @Operation(summary = "Retornar Usuário por ID - controle versão (v1)", description = "Retornar Usuário por ID - controle versão (v1)")
    @SecurityRequirement(name = "bearerAuth") //Exige Token para executar!
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem sucedida!"),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente"),
            @ApiResponse(responseCode = "403", description = "Acesso Negado -> Token expirado, faça Login novamente ou informe um Token válido para autenticação!"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado."),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
    public ResponseEntity<?> getUsuarioPorIDV1(@PathVariable(value = "id") Long id) {
        System.out.println("Executando versão 1!");
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        //Retornando um DTO -> UsuarioDTO:
        return optionalUsuario.isPresent() ? ResponseEntity.ok(new UsuarioDTO(optionalUsuario.get())) : new ResponseEntity<>("Nenhum usuário encontrado!", HttpStatus.NOT_FOUND);
    }

    //Simulando controle de versão direto na URI (v2
    @GetMapping(value = "v2/{id}", produces = "application/json")
    @Operation(summary = "Retornar Usuário por ID - controle versão (v2)", description = "Retornar Usuário por ID - controle versão (v2)")
    @SecurityRequirement(name = "bearerAuth") //Exige Token para executar!
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem sucedida!"),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente"),
            @ApiResponse(responseCode = "403", description = "Acesso Negado -> Token expirado, faça Login novamente ou informe um Token válido para autenticação!"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado."),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
    public ResponseEntity<?> getUsuarioPorIDV2(@PathVariable(value = "id") Long id) {
        System.out.println("Executando versão 2!");
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        //Retornando um DTO -> UsuarioDTO:
        return optionalUsuario.isPresent() ? ResponseEntity.ok(new UsuarioDTO(optionalUsuario.get())) : new ResponseEntity<>("Nenhum usuário encontrado!", HttpStatus.NOT_FOUND);
    }

    //Simulando controle de versão por header (v1), deve ser passado como parâmetro no header da requisição!
    @GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-Version=v1")
    @Operation(summary = "Retornar Usuário por ID - controle versão (v1)", description = "Retornar Usuário por ID - controle versão (v1)")
    @SecurityRequirement(name = "bearerAuth") //Exige Token para executar!
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem sucedida!"),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente"),
            @ApiResponse(responseCode = "403", description = "Acesso Negado -> Token expirado, faça Login novamente ou informe um Token válido para autenticação!"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado."),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
    public ResponseEntity<?> getUsuarioPorIDHeaderV1(@PathVariable(value = "id") Long id) {
        System.out.println("Executando versão Header 1 (DIRETO VO)!");
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        //Retornando direto o VO, com todos os campos disponíveis:
        return optionalUsuario.isPresent() ? ResponseEntity.ok(optionalUsuario.get()) : new ResponseEntity<>("Nenhum usuário encontrado!", HttpStatus.NOT_FOUND);
    }

    //Simulando controle de versão por header (v2), deve ser passado como parâmetro no header da requisição!
    @GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-Version=v2")
    @Operation(summary = "Retornar Usuário por ID - controle versão (v2)", description = "Retornar Usuário por ID - controle versão (v2)")
    @SecurityRequirement(name = "bearerAuth") //Exige Token para executar!
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem sucedida!"),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente"),
            @ApiResponse(responseCode = "403", description = "Acesso Negado -> Token expirado, faça Login novamente ou informe um Token válido para autenticação!"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado."),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
    public ResponseEntity<?> getUsuarioPorIDHeaderV2(@PathVariable(value = "id") Long id) {
        System.out.println("Executando versão Header 2!");
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        //Retornando um DTO -> UsuarioDTO:
        return optionalUsuario.isPresent() ? ResponseEntity.ok(new UsuarioDTO(optionalUsuario.get())) : new ResponseEntity<>("Nenhum usuário encontrado!", HttpStatus.NOT_FOUND);
    }

    /**
     * Consulta de Usuários por nome!
     *
     * @param nome (Nome do Usuário -> String)
     * @return List - UsuarioDTO em JSON
     */
    @CacheEvict(value = "cacheUsuariosPorNome", allEntries = true)
    @CachePut(value = "cacheUsuariosPorNome")
    @GetMapping(value = "/usuariopornome/{nome}", produces = "application/json")
    @Operation(summary = "Retornar Usuário por nome", description = "Retornar Usuário por nome")
    @SecurityRequirement(name = "bearerAuth") //Exige Token para executar!
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem sucedida!"),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente"),
            @ApiResponse(responseCode = "403", description = "Acesso Negado -> Token expirado, faça Login novamente ou informe um Token válido para autenticação!"),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
    public ResponseEntity<?> getListUsuarioPorNome(@PathVariable(value = "nome") String nome) {
        //Com Paginação:
        PageRequest page = PageRequest.of(0, 5, Sort.by("id"));
        Page<Usuario> pageUsuarios = usuarioRepository.findUsuarioByNome(nome, page);

        Page<UsuarioDTO> pageUsuariosDto = pageUsuarios
                .map(usuario -> new UsuarioDTO(usuario));

        return ResponseEntity.ok(pageUsuariosDto);

        //Sem paginação:
//        List<Usuario> listUsuario = usuarioRepository.findUsuarioByNome(nome);
//        listUsuario.sort(Comparator.comparing(Usuario::getId));
//
//        return ResponseEntity.ok(listUsuario.stream()
//                .map(usuario -> new UsuarioDTO(usuario))
//                .collect(Collectors.toList()));
    }

    //Fazer o processo em cache, simulando um processo lento que demore 6 segundos essa consulta (Thread)
    //Se tiver em cache, a mesma consulta depois vai trazer rapidão pois já vai estar em cache!
//    @Cacheable("cache_usuario_listall") //Essa opção por default só refaz a consulta se tiver alteração nos parâmetros da entrada, caso contrário fica sempre em cache
    @CacheEvict(value = "cacheUsuarios", allEntries = true)
//Vai deixar o cache sempre limpo, mesmo que não haja iterações na tabela
    @CachePut(value = "cacheUsuarios")
//Qualquer mudança nas tabelas envolvidas vai atualizar o cache com os dados atualizados!
    @GetMapping(value = "/listall", produces = "application/json")
    @Operation(summary = "Retornar todos os usuários - paginado", description = "Retornar todos os usuários - paginado")
    @SecurityRequirement(name = "bearerAuth") //Exige Token para executar!
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem sucedida!"),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente"),
            @ApiResponse(responseCode = "403", description = "Acesso Negado -> Token expirado, faça Login novamente ou informe um Token válido para autenticação!"),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
    public ResponseEntity<?> getListUsuario() {
        //Paginando informações (Primeira página 5 registros):
        PageRequest page = PageRequest.of(0, 5, Sort.by("id"));
        Page<Usuario> pageUsuarios = usuarioRepository.findAll(page);

        // Mapeamento da lista de usuários para uma lista de UsuarioDTO
        Page<UsuarioDTO> pageUsuarioDTO = pageUsuarios.
                map(usuario -> new UsuarioDTO(usuario));

        return ResponseEntity.ok(pageUsuarioDTO);

        //Sem Paginação:
//        List<Usuario> listUsuario = usuarioRepository.findAll();
//        listUsuario.sort(Comparator.comparing(Usuario::getId));

//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        //Retornando uma list DTO -> UsuarioDTO:
//        return ResponseEntity.ok(listUsuario.stream()
//                .map(usuario -> new UsuarioDTO(usuario))
//                .collect(Collectors.toList()));
    }

    /**
     * Método para trazer os usuários para determinada página que recebe por parâmetro
     *
     * @param pagina (Long)
     * @return ResponseEntity
     * @throws Exception Exceção ao consultar usuários para a página
     */
    @CacheEvict(value = "cachePageUsuariosPorNome", allEntries = true)
    @CachePut(value = "cachePageUsuariosPorNome")
    @GetMapping(value = "/pageusuariopornome/{pagina}/{nome}", produces = "application/json")
    @Operation(summary = "Retornar Usuários por nome com paginação", description = "Retornar Usuários por nome com paginação")
    @SecurityRequirement(name = "bearerAuth") //Exige Token para executar!
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem sucedida!"),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente"),
            @ApiResponse(responseCode = "403", description = "Acesso Negado -> Token expirado, faça Login novamente ou informe um Token válido para autenticação!"),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
    public ResponseEntity<?> getPageUsuarioPorNome(@PathVariable(value = "pagina") Long pagina, @PathVariable(value = "nome") String nome) throws Exception {
        if (pagina != null
                && nome != null
                && !nome.isEmpty()) {
            //Paginando informações (Página recebida parâmetro, trazendo até 5 registros):
            PageRequest page = PageRequest.of(pagina.intValue(), 5, Sort.by("id"));
            Page<Usuario> pageUsuarios = usuarioRepository.findUsuarioByNome(nome, page);

            // Mapeamento da lista de usuários para uma lista de UsuarioDTO
            Page<UsuarioDTO> pageUsuarioDTO = pageUsuarios.
                    map(usuario -> new UsuarioDTO(usuario));

            return ResponseEntity.ok(pageUsuarioDTO);
        } else {
            throw new Exception("Deve ser enviado por parâmetro a página que quer visualizar e o nome filtrado.");
        }
    }

    /**
     * Método para trazer os usuários para determinada página que recebe por parâmetro
     *
     * @param pagina (Long)
     * @return ResponseEntity
     * @throws Exception Exceção ao consultar usuários para a página
     */
    @CacheEvict(value = "cachePageUsuarios", allEntries = true)
    @CachePut(value = "cachePageUsuarios")
    @GetMapping(value = "/page/{pagina}", produces = "application/json")
    @Operation(summary = "Retornar Usuários em uma página", description = "Retornar Usuários em uma página")
    @SecurityRequirement(name = "bearerAuth") //Exige Token para executar!
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem sucedida!"),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente"),
            @ApiResponse(responseCode = "403", description = "Acesso Negado -> Token expirado, faça Login novamente ou informe um Token válido para autenticação!"),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
    public ResponseEntity<?> getPageUsuario(@PathVariable(value = "pagina") Long pagina) throws Exception {
        if (pagina != null) {
            //Paginando informações (Página recebida parâmetro, trazendo até 5 registros):
            PageRequest page = PageRequest.of(pagina.intValue(), 5, Sort.by("id"));
            Page<Usuario> pageUsuarios = usuarioRepository.findAll(page);

            // Mapeamento da lista de usuários para uma lista de UsuarioDTO
            Page<UsuarioDTO> pageUsuarioDTO = pageUsuarios.
                    map(usuario -> new UsuarioDTO(usuario));

            return ResponseEntity.ok(pageUsuarioDTO);
        } else {
            throw new Exception("Deve ser enviado por parâmetro a página que quer visualizar.");
        }
    }

    //Simulando um retorno de um relatório:
    //Exemplo URL -> http://localhost:8080/springbootapirest/usuario/10/venda/299
    @GetMapping(value = "/{id}/venda/{codigovenda}", produces = "application/pdf")
    @Hidden //Não mostrar no swagger
    @Operation(summary = "Retornar Vendas", description = "Teste para retornar vendas (simulação)")
    @SecurityRequirement(name = "bearerAuth") //Exige Token para executar!
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem sucedida!"),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente"),
            @ApiResponse(responseCode = "403", description = "Acesso Negado -> Token expirado, faça Login novamente ou informe um Token válido para autenticação!"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado."),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
    public ResponseEntity<?> getRelatorioPdfApenasTeste(@PathVariable(value = "id") Long id, @PathVariable(value = "codigovenda") Long codigovenda) {
        System.out.println("Id: " + id + ", Venda: " + codigovenda);

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        //O retorno seria um Relatório
        //Teria que criar a rotina de gerar o PDF e retornar ele!
        //Retornando um DTO -> UsuarioDTO:
        return optionalUsuario.isPresent() ? ResponseEntity.ok(new UsuarioDTO(optionalUsuario.get())) : new ResponseEntity<>("Nenhum usuário encontrado!", HttpStatus.NOT_FOUND);
    }

    //@Valid -> Vamos validar o objeto (validações referentes ao VO) antes de salvar!
    @PostMapping(value = "/", produces = "application/json")
    @Transactional
    @Operation(summary = "Salvar Usuário", description = "Salvar Usuário")
    @SecurityRequirement(name = "bearerAuth") //Exige Token para executar!
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem sucedida!"),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente"),
            @ApiResponse(responseCode = "403", description = "Acesso Negado -> Token expirado, faça Login novamente ou informe um Token válido para autenticação!"),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
    //Transactional (caso der erro em alguma etapa (save do usuário ou do usuario_role) fará o rollback!!!)
    public ResponseEntity<Usuario> cadastrarUsuario(@Valid @RequestBody Usuario usuario) throws Exception {
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

            //Verifica hora para data (não deixar gravação com hora 00:00):
            //No angular tratamos apenas data (String), não quisemos tratar hora lá!
            if (usuario.getDataNascimento() != null) {
                //Joga a Data mais o horário para 12:12:02 para gravação não ficar 00:00:00 por mais que não usamos hora nesse campo!
                Date dateHour = new Date((usuario.getDataNascimento().getTime() + (12 * 60 * 60 * 1000) + (12 * 60 * 1000) + 2 * 1000));
                usuario.setDataNascimento(dateHour);
            }

            usuarioSave = usuarioRepository.save(usuario);

            //Insere acessos Padrão (usuario_role):
            userDetailImplService.insereRolesPadrao(usuarioSave.getId());
        }

        return ResponseEntity.ok(usuarioSave);
    }

    //@Valid -> Vamos validar o objeto (validações referentes ao VO) antes de salvar!
    @PutMapping(value = "/", produces = "application/json")
    @Operation(summary = "Atualizar Usuário", description = "Atualizar Usuário")
    @SecurityRequirement(name = "bearerAuth") //Exige Token para executar!
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem sucedida!"),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente"),
            @ApiResponse(responseCode = "403", description = "Acesso Negado -> Token expirado, faça Login novamente ou informe um Token válido para autenticação!"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado."),
            @ApiResponse(responseCode = "405", description = "Você não tem permissão para editar o Usuário 'admin'."),
            @ApiResponse(responseCode = "406", description = "Deve ser informado um ID para o usuário que deseja atualizar."),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
    public ResponseEntity<?> atualizarUsuario(@Valid @RequestBody Usuario usuario) throws Exception {
        Usuario usuarioSave;

        if (usuario != null
                && usuario.getId() != null) {
            Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuario.getId());
            if (optionalUsuario.isPresent()) {
                if (optionalUsuario.get() != null
                        && optionalUsuario.get().getLogin() != null
                        && !optionalUsuario.get().getLogin().equals("admin")) {
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

                    //Verifica hora para data (não deixar gravação com hora 00:00):
                    //No angular tratamos apenas data (String), não quisemos tratar hora lá!
                    if (usuario.getDataNascimento() != null) {
                        //Joga a Data mais o horário para 12:12:02 para gravação não ficar 00:00:00 por mais que não usamos hora nesse campo!
                        Date dateHour = new Date((usuario.getDataNascimento().getTime() + (12 * 60 * 60 * 1000) + (12 * 60 * 1000) + 2 * 1000));
                        usuario.setDataNascimento(dateHour);
                    }

                    //Não vamos alterar a lista de TokenRecuperação, deixa como está atualmente:
                    usuario.setListTokenRecuperacaoSenha(optionalUsuario.get().getListTokenRecuperacaoSenha());
                    if (usuario.getListTokenRecuperacaoSenha() != null
                            && !usuario.getListTokenRecuperacaoSenha().isEmpty()) {
                        usuario.getListTokenRecuperacaoSenha().forEach(tokenRecuperacaoSenha -> {
                            tokenRecuperacaoSenha.setUsuario(usuario);
                        });
                    }

                    usuarioSave = usuarioRepository.save(usuario);
                } else {
                    return new ResponseEntity<>("Você não tem permissão para editar o Usuário 'admin'.", HttpStatus.METHOD_NOT_ALLOWED);
                }
            } else {
                return new ResponseEntity<>("Usuário com ID(" + usuario.getId().toString() + ") " + "não encontrado!", HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("Deve ser informado um ID para o usuário que deseja atualizar!", HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(usuarioSave);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    @Transactional //Deleta usuario e usuario_role
    @Operation(summary = "Apagar Usuário", description = "Apagar Usuário")
    @SecurityRequirement(name = "bearerAuth") //Exige Token para executar!
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem sucedida!"),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente"),
            @ApiResponse(responseCode = "403", description = "Acesso Negado -> Token expirado, faça Login novamente ou informe um Token válido para autenticação!"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado."),
            @ApiResponse(responseCode = "406", description = "Você não tem permissão para excluir o Usuário 'admin'."),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
    public ResponseEntity<?> deletaUsuario(@PathVariable(value = "id") Long id) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            if (optionalUsuario.get() != null
                    && optionalUsuario.get().getLogin() != null
                    && !optionalUsuario.get().getLogin().equals("admin")) {
                usuarioRepository.deleteUsuarioRoleById(id);
                //Limpa lista de Role já deletada manualmente acima para não causar problema no hibernate querer matar usuario_role e role!
                optionalUsuario.get().getListRole().clear();
                usuarioRepository.deleteById(id);
                return ResponseEntity.ok("Usuário deletado com sucesso!");
            } else {
                return new ResponseEntity<>("Você não tem permissão para excluir o Usuário 'admin'.", HttpStatus.NOT_ACCEPTABLE);
            }
        } else {
            return new ResponseEntity<>("Usuário não encontrado!", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Impressão do relatório de Usuário em String base64 - PDF
     *
     * @param request
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/relatorio", produces = "application/text")
    @Operation(summary = "Relatório de todos os Usuários", description = "Relatório de todos os Usuários cadastrados em PDF.")
    @Hidden
    //Não mostrar no Swagger, Swagger não visualiza PDF direto ali, e retornamos em BASE64, não vamos mostrar relatório lá!
    @SecurityRequirement(name = "bearerAuth") //Exige Token para executar!
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem sucedida!"),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente"),
            @ApiResponse(responseCode = "403", description = "Acesso Negado -> Token expirado, faça Login novamente ou informe um Token válido para autenticação!"),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
    public ResponseEntity<String> downloadRelatorioUsuario(HttpServletRequest request) throws Exception {
        List<Usuario> listUsuario = usuarioRepository.findAll();

        if (listUsuario == null
                || listUsuario.isEmpty()) {
            //Retorna Objeto NULO caso não tenha registro!
            return ResponseEntity.ok(null);
        }
        listUsuario.sort(Comparator.comparing(Usuario::getId));

        byte[] pdf = reportService.getReportPdf(listUsuario, "usuario", new HashMap<>(), request.getServletContext());
        String base64Pdf = "data:application/pdf;base64," + Base64.getEncoder().encodeToString(pdf);

        return ResponseEntity.ok(base64Pdf);
    }

    /**
     * Impressão do relatório de Usuário em String base64 - PDF (Recebendo parâmetro para filtrar relatório)
     *
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/relatorio/", produces = "application/text")
    @Operation(summary = "Relatório de Usuários com filtro", description = "Relatório de Usuários cadastrados de acordo com o filtro passado - em PDF.")
    @Hidden
    //Não mostrar no Swagger, Swagger não visualiza PDF direto ali, e retornamos em BASE64, não vamos mostrar relatório lá!
    @SecurityRequirement(name = "bearerAuth") //Exige Token para executar!
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem sucedida!"),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente"),
            @ApiResponse(responseCode = "403", description = "Acesso Negado -> Token expirado, faça Login novamente ou informe um Token válido para autenticação!"),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
    public ResponseEntity<String> downloadRelatorioUsuarioWithParam(HttpServletRequest request, @RequestBody UsuarioReportDTO usuarioReportDto) throws Exception {
        Map<String, Object> param = new HashMap<>();

        if (usuarioReportDto != null) {
            if (usuarioReportDto.getDataNascimentoInicio() != null
                    && usuarioReportDto.getDataNascimentoFim() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat dateFormatParamRel = new SimpleDateFormat("yyyy-MM-dd");
                //String dataInicio = dateFormatParamRel.format(dateFormat.parse(usuarioReportDto.getDataNascimentoInicio()));
                //String dataFim = dateFormatParamRel.format(dateFormat.parse(usuarioReportDto.getDataNascimentoFim()));

                //Data Início:
                java.util.Date dateInicio = dateFormatParamRel.parse(dateFormatParamRel.format(dateFormat.parse(usuarioReportDto.getDataNascimentoInicio())));

                //Data Fim:
                java.util.Date dateFim = dateFormatParamRel.parse(dateFormatParamRel.format(dateFormat.parse(usuarioReportDto.getDataNascimentoFim())));

                param.put("DATA_NASC_INICIO", usuarioReportDto.getDataNascimentoInicio());
                param.put("DATA_NASC_FIM", usuarioReportDto.getDataNascimentoFim());
                param.put("dataNascInicio", dateInicio);
                param.put("dataNascFim", dateFim);
            }

            if (usuarioReportDto.getProfissao() != null
                    && usuarioReportDto.getProfissao().getId() != null
                    && usuarioReportDto.getProfissao().getDescricao() != null) {
                param.put("profissaoId", usuarioReportDto.getProfissao().getId());
                param.put("PROFISSAO", "(" + usuarioReportDto.getProfissao().getId() + ") " + usuarioReportDto.getProfissao().getDescricao().toUpperCase());
            }
        }

        //Select filtrando dados:
        List<Usuario> listUsuario = usuarioRepository.findAll(
                UsuarioSpecification.dataNascimentoEntre(param.get("dataNascInicio") == null ? null : (Date) param.get("dataNascInicio"), param.get("dataNascFim") == null ? null : (Date) param.get("dataNascFim"))
                        .and(UsuarioSpecification.profissaoIdIgual(param.get("profissaoId") == null ? null : (Long) param.get("profissaoId")))
        );

        if (listUsuario == null
                || listUsuario.isEmpty()) {
            //Retorna Objeto NULO caso não tenha registro!
            return ResponseEntity.ok(null);
        }

        listUsuario.sort(Comparator.comparing(Usuario::getId));

        byte[] pdf = reportService.getReportPdf(listUsuario, "usuario", param, request.getServletContext());
        String base64Pdf = "data:application/pdf;base64," + Base64.getEncoder().encodeToString(pdf);

        return ResponseEntity.ok(base64Pdf);
    }

    @GetMapping(value = "/graficosalario", produces = "application/json")
    @Operation(summary = "Gráfico de Salário x Usuários", description = "Dados para gráfico de Salário x Usuários")
    @SecurityRequirement(name = "bearerAuth") //Exige Token para executar!
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem sucedida!"),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente"),
            @ApiResponse(responseCode = "403", description = "Acesso Negado -> Token expirado, faça Login novamente ou informe um Token válido para autenticação!"),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
    public ResponseEntity<UsuarioGraficoDTO> getGraficoSalarioUsuarios() {
        UsuarioGraficoDTO usuarioGraficoDto = new UsuarioGraficoDTO();
        StringBuilder arrayNomes = new StringBuilder();
        StringBuilder arraySalarios = new StringBuilder();

        StringBuilder sql = new StringBuilder();
        sql.append("         select usu.nome,  ")
                .append("       usu.salario  ")
                .append("      from public.usuario usu  ")
                .append("     where usu.salario > 0  ")
                .append("       and usu.nome is not null   ")
                .append("  order by usu.salario;  ");

        //Usando query com BeanPropertyRowMapper, ele usa JDBC template, faz o select manual e converte para as colunas do VO/DTO direto! PERFEITO!
        List<UsuarioSalarioDTO> listUsuarioSalarioDto = jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(UsuarioSalarioDTO.class));
        if (listUsuarioSalarioDto != null
                && !listUsuarioSalarioDto.isEmpty()) {
            listUsuarioSalarioDto.forEach(res -> {
                if (res.getNome() != null
                        && res.getSalario() != null) {
                    //Nomes:
                    if (!arrayNomes.isEmpty()) {
                        arrayNomes.append(",");
                    }
                    //Armazena em formato JSON para string, aspas duplas separando strings, para o relatorio vai em json depois:
                    arrayNomes.append("\"").append(res.getNome()).append("\"");

                    //Salarios:
                    if (!arraySalarios.isEmpty()) {
                        arraySalarios.append(",");
                    }
                    arraySalarios.append(res.getSalario().setScale(2));
                }
            });
        }

        usuarioGraficoDto.setNome(arrayNomes.toString());
        usuarioGraficoDto.setSalario(arraySalarios.toString());

        return ResponseEntity.ok(usuarioGraficoDto);
    }

    private void chargedCep(Usuario usuario) throws Exception {
        if (usuario != null
                && usuario.getCep() != null
                && !usuario.getCep().trim().isEmpty()) {
            //Vamos passar para carregar o CEP apenas se não foi nada informado manualmente ainda,
            //Casos onde o usuário cadastra manualmente as informações (complemento, etc.. o CEP já foi carregado no site, não precisa fazer novamente)
            if (((usuario.getLogradouro() == null) || (usuario.getLogradouro().trim().isEmpty()))
                    && ((usuario.getBairro() == null) || (usuario.getBairro().trim().isEmpty()))
                    && ((usuario.getComplemento() == null) || (usuario.getComplemento().trim().isEmpty()))
                    && ((usuario.getUf() == null) || (usuario.getUf().trim().isEmpty()))
                    && ((usuario.getLocalidade() == null) || (usuario.getLocalidade().trim().isEmpty()))) {

                //Valida CEP:
                validaCepInformado(usuario.getCep());

                try {
                    URL url = new URL("https://viacep.com.br/ws/" + usuario.getCep() + "/json/");
                    URLConnection urlConnection = url.openConnection();
                    InputStream inputStream = urlConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    String cep;
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
                    bufferedReader.close();
                    inputStream.close();
                } catch (RuntimeException ex) {
                    throw new Exception(ex.getMessage());
                } catch (Exception ex) {
                    throw new Exception("CEP inválido. Informe um CEP Válido para prosseguir.");
                }
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
