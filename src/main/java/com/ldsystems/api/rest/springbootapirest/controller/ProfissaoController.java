package com.ldsystems.api.rest.springbootapirest.controller;

import com.ldsystems.api.rest.springbootapirest.model.Profissao;
import com.ldsystems.api.rest.springbootapirest.repository.ProfissaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController //Arquitetura REST
@CrossOrigin(origins = "*")  // Forma default, qualquer sistema poderá acessar esse RestController
@RequestMapping(value = "/profissao")
public class ProfissaoController {

    @Autowired
    private ProfissaoRepository profissaoRepository;

    @GetMapping(value = "/", produces = "application/json")
    private ResponseEntity<List<Profissao>> getListProfissoes() {
        //Sem paginação, apenas retornar a lista de todas as profissões disponíveis ordenadas
        List<Profissao> listProfissao = profissaoRepository.findAll();

        //Ordena por Descrição:
        listProfissao.sort(Comparator.comparing(Profissao::getDescricao));

        return ResponseEntity.ok(listProfissao);
    }
}
