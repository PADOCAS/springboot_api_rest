package com.ldsystems.api.rest.springbootapirest.model.dto;

import java.io.Serializable;
import java.util.Objects;

public class UsuarioGraficoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;

    private String salario;

    public UsuarioGraficoDTO() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSalario() {
        return salario;
    }

    public void setSalario(String salario) {
        this.salario = salario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioGraficoDTO that = (UsuarioGraficoDTO) o;
        return Objects.equals(nome, that.nome) && Objects.equals(salario, that.salario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, salario);
    }

    @Override
    public String toString() {
        return "UsuarioGraficoDTO{" +
                "nome='" + nome + '\'' +
                ", salario='" + salario + '\'' +
                '}';
    }
}
