package com.example.cmct.clases;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Date;

public class Incidencia implements Serializable {
    String dni;
    String tipo;
    String descripcion;
    Timestamp fechaIncidencia;

    public Incidencia() {
    }

    public Incidencia(String dni, String tipo, String descripcion, Timestamp fechaIncidencia) {
        this.dni = dni;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fechaIncidencia = fechaIncidencia;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
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

    public Timestamp getFechaIncidencia() {
        return fechaIncidencia;
    }

    public void setFechaIncidencia(Timestamp fechaIncidencia) {
        this.fechaIncidencia = fechaIncidencia;
    }
}
