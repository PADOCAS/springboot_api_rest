package com.ldsystems.api.rest.springbootapirest.repository;

import com.ldsystems.api.rest.springbootapirest.model.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query(value = "select u from Usuario u where u.login = :login")
    public Usuario findUsuarioByLogin(@Param("login") String login);

    @Transactional
    @Modifying //Alteração banco de dados
    @Query(value = "update Usuario u set u.token = :token where u.login = :login")
    public void atualizaTokenUsuario(@Param("login") String login, @Param("token") String token);
}
