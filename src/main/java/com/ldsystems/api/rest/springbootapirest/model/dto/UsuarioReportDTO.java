package com.ldsystems.api.rest.springbootapirest.model.dto;

import com.ldsystems.api.rest.springbootapirest.model.Profissao;

import java.io.Serializable;
import java.util.Objects;

public class UsuarioReportDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dataNascimentoInicio;
    private String dataNascimentoFim;
    private Profissao profissao;

    public UsuarioReportDTO() {
    }

    public String getDataNascimentoInicio() {
        return dataNascimentoInicio;
    }

    public void setDataNascimentoInicio(String dataNascimentoInicio) {
        this.dataNascimentoInicio = dataNascimentoInicio;
    }

    public String getDataNascimentoFim() {
        return dataNascimentoFim;
    }

    public void setDataNascimentoFim(String dataNascimentoFim) {
        this.dataNascimentoFim = dataNascimentoFim;
    }

    public Profissao getProfissao() {
        return profissao;
    }

    public void setProfissao(Profissao profissao) {
        this.profissao = profissao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UsuarioReportDTO that = (UsuarioReportDTO) o;
        return Objects.equals(dataNascimentoInicio, that.dataNascimentoInicio) && Objects.equals(dataNascimentoFim, that.dataNascimentoFim) && Objects.equals(profissao, that.profissao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataNascimentoInicio, dataNascimentoFim, profissao);
    }

    @Override
    public String toString() {
        return "UsuarioReportDTO{" +
                "dataNascimentoInicio='" + dataNascimentoInicio + '\'' +
                ", dataNascimentoFim='" + dataNascimentoFim + '\'' +
                ", profissao=" + profissao +
                '}';
    }
}
