package com.ldsystems.api.rest.springbootapirest.repository;

import com.ldsystems.api.rest.springbootapirest.model.TokenRecuperacaoSenha;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRecuperacaoSenhaRepository extends JpaRepository<TokenRecuperacaoSenha, Long> {

    @Query(value = "SELECT t FROM TokenRecuperacaoSenha t WHERE t.token =:token")
    public TokenRecuperacaoSenha findTokenRecuperacaoSenhaByToken(@Param("token") String token);

    @Transactional
    @Modifying
    @Query(value = "UPDATE Usuario u SET u.senha = :senha WHERE u.id = :id")
    public void atualizaSenhaUsuario(@Param("id") Long id, @Param("senha") String senha);

    @Transactional
    @Modifying
    @Query(value = "UPDATE TokenRecuperacaoSenha t SET t.utilizado = true WHERE t.id = :id")
    public void desativaTokenUsuarioSenhaAlterada(@Param("id") Long id);
}
