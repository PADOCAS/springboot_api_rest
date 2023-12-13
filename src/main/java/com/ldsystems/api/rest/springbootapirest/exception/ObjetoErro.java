package com.ldsystems.api.rest.springbootapirest.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class ObjetoErro implements Serializable {

    private static final long serialVersionUID = 1L;

    private String erro;

    private String path;

    private String codigo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime timestamp;

    public ObjetoErro() {
    }

    public ObjetoErro(String erro, String path, String codigo, LocalDateTime timestamp) {
        this.erro = erro;
        this.path = path;
        this.codigo = codigo;
        this.timestamp = timestamp;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjetoErro that = (ObjetoErro) o;
        return Objects.equals(erro, that.erro) && Objects.equals(path, that.path) && Objects.equals(codigo, that.codigo) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(erro, path, codigo, timestamp);
    }

    @Override
    public String toString() {
        return "ObjetoErro{" +
                "erro='" + erro + '\'' +
                ", path='" + path + '\'' +
                ", codigo='" + codigo + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
