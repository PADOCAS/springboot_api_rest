package com.ldsystems.api.rest.springbootapirest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
public class TokenRecuperacaoSenha implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonIgnore //Não deixar recursivo, carregando usuário novamente no JSON e ficar em loop infinito!
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false, referencedColumnName = "id", foreignKey = @ForeignKey(name = "token_recuperacao_senha_fk1"))
    private Usuario usuario;

    @Column(name = "token", nullable = false)
    private String token;

    @Temporal(TemporalType.TIMESTAMP) //Grava no formato data no banco de dados
    @Column(name = "data_expiracao", nullable = false)
    private Date dataExpiracao;

    @Column(name = "utilizado")
    private Boolean utilizado;

    public TokenRecuperacaoSenha() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(Date dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    public Boolean getUtilizado() {
        return utilizado;
    }

    public void setUtilizado(Boolean utilizado) {
        this.utilizado = utilizado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenRecuperacaoSenha that = (TokenRecuperacaoSenha) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TokenRecuperacaoSenha{" +
                "id=" + id +
                ", usuario=" + usuario +
                ", token='" + token + '\'' +
                ", dataExpiracao=" + dataExpiracao +
                ", utilizado=" + utilizado +
                '}';
    }
}
