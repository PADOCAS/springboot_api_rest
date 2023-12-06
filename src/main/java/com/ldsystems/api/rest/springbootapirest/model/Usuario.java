package com.ldsystems.api.rest.springbootapirest.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Implementa UserDetails que auxiliar nos tratamentos para usuário no Spring!
 * Vamos se preocupar com o login, senha e acessos. O restante dos métodos da interface retornamos true!
 */
@Entity
public class Usuario implements UserDetails {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "login", nullable = false, length = 30)
    private String login;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "senha", nullable = false, length = 100)
    private String senha;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Telefone> listTelefone;

    //Dessa forma, está criando uma unique isolado para o role_id, vamos tirar ela manualmente para não atrapalhar!
    //Deixar no banco apenas uma unique com os 2 campos (CONSTRAINT usuario_role_uk1 UNIQUE (usuario_id, role_id))
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    //Cria tabela de acessos para o usuário (Vai gerar uma tabela usuario_role unindo (role e usuario))
    @JoinTable(name = "usuario_role",
            joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id", table = "usuario", foreignKey = @ForeignKey(name = "usuario_role_fk1")),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id", table = "role", foreignKey = @ForeignKey(name = "usuario_role_fk2")),
            uniqueConstraints = {@UniqueConstraint(columnNames = {"usuario_id", "role_id"}, name = "usuario_role_uk1")})
    private List<Role> listRole;

    public Usuario() {
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getListRole();
    }

    @Override
    public String getPassword() {
        return getSenha();
    }

    @Override
    public String getUsername() {
        return getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Usuario(Long id) {
        this.id = id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public List<Telefone> getListTelefone() {
        return listTelefone;
    }

    public void setListTelefone(List<Telefone> listTelefone) {
        this.listTelefone = listTelefone;
    }

    public List<Role> getListRole() {
        return listRole;
    }

    public void setListRole(List<Role> listRole) {
        this.listRole = listRole;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Usuario usuario = (Usuario) object;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", nome='" + nome + '\'' +
                ", senha='" + senha + '\'' +
                '}';
    }
}
