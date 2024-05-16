package com.example.cmct.clases;

import android.location.Geocoder;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class Fichaje implements Serializable {
    private String dni;
    private Timestamp fechaFichaje;
    private GeoPoint localizacion;

    public Fichaje(){

    }

    public Fichaje(String dni, Timestamp fechaFichaje, GeoPoint localizacion) {
        this.dni = dni;
        this.fechaFichaje = fechaFichaje;
        this.localizacion = localizacion;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Timestamp getFechaFichaje() {
        return fechaFichaje;
    }

    public void setFechaFichaje(Timestamp fechaFichaje) {
        this.fechaFichaje = fechaFichaje;
    }

    public GeoPoint getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(GeoPoint localizacion) {
        this.localizacion = localizacion;
    }
}
