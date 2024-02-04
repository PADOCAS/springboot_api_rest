package com.ldsystems.api.rest.springbootapirest.model.dto;

import com.ldsystems.api.rest.springbootapirest.model.Profissao;

import java.io.Serializable;
import java.util.Objects;

public class ProfissaoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String descricao;

    //Descricao para trabalhar no componente Angular já pronta
    private String idDescricaoFormatted;

    public ProfissaoDTO() {
    }

    //Construtor recebe um VO Profissao e alimenta o DTO
    public ProfissaoDTO(Profissao profissao) {
        if (profissao != null) {
            this.id = profissao.getId();
            this.descricao = profissao.getDescricao();
            if (getId() != null
                    && getDescricao() != null) {
                this.idDescricaoFormatted = new StringBuilder().append("(").append(getId()).append(") ").append(getDescricao()).toString();
            } else {
                this.idDescricaoFormatted = null;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getIdDescricaoFormatted() {
        if (getId() != null
                && getDescricao() != null) {
            return new StringBuilder().append("(").append(getId()).append(") ").append(getDescricao()).toString();
        } else {
            return null;
        }
    }

    public void setIdDescricaoFormatted(String idDescricaoFormatted) {
        this.idDescricaoFormatted = idDescricaoFormatted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfissaoDTO that = (ProfissaoDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ProfissaoDTO{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
