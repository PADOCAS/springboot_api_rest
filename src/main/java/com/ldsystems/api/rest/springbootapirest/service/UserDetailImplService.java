package com.ldsystems.api.rest.springbootapirest.service;

import com.ldsystems.api.rest.springbootapirest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserDetailImplService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails usuario = usuarioRepository.findUsuarioByLogin(username);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado.");
        }

        return new User(usuario.getUsername(), usuario.getPassword(), usuario.isEnabled(), usuario.isAccountNonExpired(), usuario.isCredentialsNonExpired(), usuario.isAccountNonLocked(), usuario.getAuthorities());
    }

    /**
     * Salva o usuario_role padrão ROLE_USER para um novo usuário cadastrado!
     * @param id
     */
    public void insereRolesPadrao(Long id) {
        if (id != null) {
            usuarioRepository.insertUsuarioRolePadrao(id);
        }
    }
}
