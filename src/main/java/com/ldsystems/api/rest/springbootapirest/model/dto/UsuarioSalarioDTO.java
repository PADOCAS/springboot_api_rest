package com.ldsystems.api.rest.springbootapirest.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class UsuarioSalarioDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;

    private BigDecimal salario;

    public UsuarioSalarioDTO() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getSalario() {
        return salario;
    }

    public void setSalario(BigDecimal salario) {
        this.salario = salario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioSalarioDTO that = (UsuarioSalarioDTO) o;
        return Objects.equals(nome, that.nome) && Objects.equals(salario, that.salario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, salario);
    }

    @Override
    public String toString() {
        return "UsuarioSalarioDTO{" +
                "nome='" + nome + '\'' +
                ", salario=" + salario +
                '}';
    }
}
