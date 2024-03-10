package com.ldsystems.api.rest.springbootapirest.repository;

import com.ldsystems.api.rest.springbootapirest.model.Usuario;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

    @Query(value = "select u from Usuario u where u.login = :login")
    Usuario findUsuarioByLogin(@Param("login") String login);

    @Query(value = "select u from Usuario u where u.nome ilike %:nome%")
    Page<Usuario> findUsuarioByNome(@Param("nome") String nome, PageRequest pageRequest);

    @Transactional
    @Modifying //Alteração banco de dados
    @Query(value = "update Usuario u set u.token = :token where u.login = :login")
    void atualizaTokenUsuario(@Param("login") String login, @Param("token") String token);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO public.usuario_role (usuario_id, role_id) VALUES (:id, (select rol.id from public.role rol where rol.nome_role = 'ROLE_USER' limit 1))")
    void insertUsuarioRolePadrao(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM public.usuario_role usuRol WHERE usuRol.usuario_id = :id")
    void deleteUsuarioRoleById(@Param("id") Long id);
}
