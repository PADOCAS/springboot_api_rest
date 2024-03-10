package com.ldsystems.api.rest.springbootapirest.repository.specification;

import com.ldsystems.api.rest.springbootapirest.model.Profissao;
import com.ldsystems.api.rest.springbootapirest.model.Usuario;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UsuarioSpecification {

    public static Specification<Usuario> dataNascimentoEntre(java.util.Date inicio, java.util.Date fim) {
        return (Root<Usuario> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (inicio != null
                    && fim != null) {
                //Para não ficar horário 00:00:00 e filtrar errado!
                java.sql.Date dataInicio = new java.sql.Date(inicio.getTime() + (12 * 60 * 60 * 1000) + (12 * 60 * 1000) + 2 * 1000);
                java.sql.Date dataFim = new java.sql.Date(fim.getTime() + (12 * 60 * 60 * 1000) + (12 * 60 * 1000) + 2 * 1000);

                //Caminho do VO: dataNascimento:
                predicates.add(criteriaBuilder.between(root.get("dataNascimento"), dataInicio, dataFim));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Usuario> profissaoIdIgual(Long profissaoId) {
        return (Root<Usuario> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (profissaoId != null) {
                //Caminho do VO -> profissao.id:
                // Cria uma junção explícita com a entidade Profissao
                Join<Usuario, Profissao> profissaoJoin = root.join("profissao", JoinType.INNER);
                // Acessa o campo id do objeto Profissao
                predicates.add(criteriaBuilder.equal(profissaoJoin.get("id"), profissaoId));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
