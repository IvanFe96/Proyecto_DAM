package com.example.cmct.clases;

import java.io.Serializable;
import java.util.Date;

public class Incidencia implements Serializable {
    String idUsuario;
    String tipo;
    String descripcion;
    Date fechaIncidencia;

    public Incidencia() {
    }

    public Incidencia(String idUsuario, String tipo, String descripcion, Date fechaIncidencia) {
        this.idUsuario = idUsuario;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fechaIncidencia = fechaIncidencia;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaIncidencia() {
        return fechaIncidencia;
    }

    public void setFechaIncidencia(Date fechaIncidencia) {
        this.fechaIncidencia = fechaIncidencia;
    }
}
