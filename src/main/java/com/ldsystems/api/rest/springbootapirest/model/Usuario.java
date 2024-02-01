package com.ldsystems.api.rest.springbootapirest.model;

import br.com.caelum.stella.bean.validation.CPF;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
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

    @Column(name = "login", nullable = false, length = 30, unique = true)
    private String login;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "senha", nullable = false, length = 100)
    private String senha;

    @Column(name = "token")
    private String token;

    @Column(name = "cep", length = 15)
    private String cep;
    @Column(name = "logradouro", length = 100)
    private String logradouro;

    @Column(name = "complemento", length = 100)
    private String complemento;

    @Column(name = "bairro", length = 60)
    private String bairro;

    @Column(name = "localidade", length = 50)
    private String localidade;

    @Column(name = "uf", length = 2)
    private String uf;

    @JsonFormat(pattern = "dd/MM/yyyy") //Em JSON vai receber nesse formato
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "dd/MM/yyyy") //Formato de Data que vai trabalhar
    @Temporal(TemporalType.DATE) //Grava no formato data no banco de dados
    @Column(name = "data_nascimento")
    private Date dataNascimento;

    @CPF
    @Column(name = "cpf", length = 11)
    private String cpf;

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

    @JsonIgnore //Não trazer coluna no JSON! Não queremos!
    @Override
    public String getPassword() {
        return getSenha();
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return getLogin();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getLocalidade() {
        return localidade;
    }

    public void setLocalidade(String localidade) {
        this.localidade = localidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
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
