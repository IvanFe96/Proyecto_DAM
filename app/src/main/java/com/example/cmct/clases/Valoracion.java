package com.example.cmct.clases;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Valoracion implements Serializable {
    private String dni;
    String nombreTrabajador;
    private float calificacion;
    private Timestamp fechaValoracion;

    public Valoracion() {
    }

    public Valoracion(String dni, String nombreTrabajador, float calificacion, Timestamp fechaValoracion) {
        this.dni = dni;
        this.nombreTrabajador = nombreTrabajador;
        this.calificacion = calificacion;
        this.fechaValoracion = fechaValoracion;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombreTrabajador() {
        return nombreTrabajador;
    }

    public void setNombreTrabajador(String nombreTrabajador) {
        this.nombreTrabajador = nombreTrabajador;
    }

    public float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(float calificacion) {
        this.calificacion = calificacion;
    }

    public Timestamp getFechaValoracion() {
        return fechaValoracion;
    }

    public void setFechaValoracion(Timestamp fechaValoracion) {
        this.fechaValoracion = fechaValoracion;
    }
}
