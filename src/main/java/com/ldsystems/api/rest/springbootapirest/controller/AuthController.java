package com.ldsystems.api.rest.springbootapirest.controller;

import com.ldsystems.api.rest.springbootapirest.model.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Controle de Autenticação de Usuários", description = "Controle de autenticação de Usuários. Usuário informa sua credenciais (login, senha) e se tudo ok, retornará um Token JWT válido.") // TAG para swagger
public class AuthController {

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
}
