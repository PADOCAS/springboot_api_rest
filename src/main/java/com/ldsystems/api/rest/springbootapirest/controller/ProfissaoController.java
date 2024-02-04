package com.ldsystems.api.rest.springbootapirest.controller;

import com.ldsystems.api.rest.springbootapirest.model.Profissao;
import com.ldsystems.api.rest.springbootapirest.model.dto.ProfissaoDTO;
import com.ldsystems.api.rest.springbootapirest.repository.ProfissaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController //Arquitetura REST
@CrossOrigin(origins = "*")  // Forma default, qualquer sistema poderá acessar esse RestController
@RequestMapping(value = "/profissao")
public class ProfissaoController {

    @Autowired
    private ProfissaoRepository profissaoRepository;

    @GetMapping(value = "/", produces = "application/json")
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
