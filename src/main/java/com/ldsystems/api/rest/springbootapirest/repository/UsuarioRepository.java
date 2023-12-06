package com.ldsystems.api.rest.springbootapirest.repository;

import com.ldsystems.api.rest.springbootapirest.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query(value = "select u from Usuario u where u.login = :login")
    public Usuario findUsuarioByLogin(@Param("login") String login);
}
