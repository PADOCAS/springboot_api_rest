package com.ldsystems.api.rest.springbootapirest.controller;

import com.ldsystems.api.rest.springbootapirest.model.Usuario;
import com.ldsystems.api.rest.springbootapirest.model.dto.UsuarioDTO;
import com.ldsystems.api.rest.springbootapirest.repository.UsuarioRepository;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")  // Forma default, qualquer sistema poderá acessar esse RestController
@Tag(name = "Controle de Autenticação de Usuários", description = "Controle de autenticação de Usuários. Usuário informa sua credenciais (login, senha) e se tudo ok, retornará um Token JWT válido.")
// TAG para swagger
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Operation(summary = "Autentica um usuário", description = "Autentica um usuário e retorna um token JWT válido para utilizar nas demais requisições do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token JWT retornado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    @PostMapping(value = "/login", produces = "application/json")
    public ResponseEntity<?> login(@RequestBody Usuario usuario) {
        //Este método serve apenas para documentação no swagger, como utliza o '/login' que já existe e é no filter,
        //essa chamada vai passar pelo filter antes e executar toda lógica de lá para autenticar e retornar token.
        //Portanto, não precisamos implementar nada aqui, apenas retornar um 200 como tudo ok!
        return ResponseEntity.ok().build();
    }

    @Hidden //Oculta método no swagger
    @Operation(summary = "Valida Login Usuário", description = "Teste para credenciais do usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem sucedida!"),
            @ApiResponse(responseCode = "500", description = "Credenciais inválida"),
    })
    @PostMapping(value = "/validlogin", produces = "application/json")
    public ResponseEntity<?> getUsuario(@RequestBody Usuario usuario) throws Exception {
        if (usuario != null
                && usuario.getLogin() != null
                && !usuario.getLogin().isEmpty()
                && usuario.getSenha() != null
                && !usuario.getSenha().isEmpty()) {
            Usuario chargedUsuario = usuarioRepository.findUsuarioByLogin(usuario.getLogin());

            if(chargedUsuario != null
                && chargedUsuario.getSenha() != null) {
                // Compara a senha fornecida com a senha criptografada armazenada no banco de dados:
                if(new BCryptPasswordEncoder().matches(usuario.getSenha(), chargedUsuario.getSenha())) {
                    return ResponseEntity.ok().build();
                } else {
                    throw new Exception("Senha inválida! Tente novamente");
                }
            } else {
                throw new Exception("Usuário não existente! Tente novamente");
            }
        } else {
            throw new Exception("Deve ser informado Login e Senha!");
        }
    }
}
