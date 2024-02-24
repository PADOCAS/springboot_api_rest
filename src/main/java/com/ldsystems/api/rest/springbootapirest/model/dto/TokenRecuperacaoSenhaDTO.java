package com.ldsystems.api.rest.springbootapirest.model.dto;

import java.io.Serializable;
import java.util.Objects;

public class TokenRecuperacaoSenhaDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String token;

    private String email;

    private String senha;

    public TokenRecuperacaoSenhaDTO() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenRecuperacaoSenhaDTO that = (TokenRecuperacaoSenhaDTO) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

    @Override
    public String toString() {
        return "TokenRecuperacaoSenhaDTO{" +
                "token='" + token + '\'' +
                ", email='" + email + '\'' +
                ", senha='" + senha + '\'' +
                '}';
    }
}
