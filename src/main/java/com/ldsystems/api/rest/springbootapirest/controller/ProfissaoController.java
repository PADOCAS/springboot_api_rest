package com.ldsystems.api.rest.springbootapirest.controller;

import com.ldsystems.api.rest.springbootapirest.model.Profissao;
import com.ldsystems.api.rest.springbootapirest.model.dto.ProfissaoDTO;
import com.ldsystems.api.rest.springbootapirest.repository.ProfissaoRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
@RestController //Arquitetura REST
@CrossOrigin(origins = "*")  // Forma default, qualquer sistema poderá acessar esse RestController
@RequestMapping(value = "/profissao")
@Tag(name = "Controle de Profissões", description = "Controle de Profissões") // TAG para swagger
public class ProfissaoController {

    @Autowired
    private ProfissaoRepository profissaoRepository;

    @GetMapping(value = "/", produces = "application/json")
    @Operation(summary = "Lista de Profissões", description = "Retorna lista de profissões cadastradas.")
    @SecurityRequirement(name = "bearerAuth") //Exige Token para executar!
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação bem sucedida!"),
            @ApiResponse(responseCode = "400", description = "Solicitação inválida"),
            @ApiResponse(responseCode = "401", description = "Token JWT inválido ou ausente"),
            @ApiResponse(responseCode = "403", description = "Acesso Negado -> Token expirado, faça Login novamente ou informe um Token válido para autenticação!"),
            @ApiResponse(responseCode = "503", description = "Serviço indisponível")
    })
    private ResponseEntity<List<ProfissaoDTO>> getListProfissoes() {
        //Sem paginação, apenas retornar a lista de todas as profissões disponíveis ordenadas
        List<Profissao> listProfissao = profissaoRepository.findAll();

        //Ordena por Descrição:
        listProfissao.sort(Comparator.comparing(Profissao::getId));

        //Retorna List ProfissaoDTO apenas com dados necessários:
        return ResponseEntity.ok(listProfissao.stream()
                .map(profissao -> new ProfissaoDTO(profissao))
                .collect(Collectors.toList()));
    }
}
