package com.example.cmct.clases;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cmct.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class Trabajador extends Usuario implements Serializable {

    // OBTENER LAS INSTANCIAS DE AUTENTICACION Y LA BASE DE DATOS DE FIREBASE
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Trabajador() {
        super();
    }

    public Trabajador(String imagen, String nombre, String apellido1, String apellido2, String telefono, String dni, String correo, String contrasenia, String rol) {
        super(imagen, nombre, apellido1, apellido2, telefono, dni, correo, contrasenia, rol);
    }

    // METODO PARA REGISTRAR INCIDENCIAS DEL TRABAJADOR
    public void crearIncidencia(Activity actividad, String tipoIncidencia, String descripcion, Timestamp fechaIncidencia) {
        db.collection("incidencias").add(new Incidencia(this.getDni(),tipoIncidencia,descripcion, fechaIncidencia));
        Utilidades.mostrarMensajes(actividad,0,"Incidencia registrada con éxito");
    }

    // METODO PARA FICHAR
    public void fichar(Activity actividad, double latitud, double longitud) {
        // CREAR UN OBJETO FICHAJE PARA GUARDARLO
        Fichaje fichaje = new Fichaje(this.getDni(),Timestamp.now(),new GeoPoint(latitud,longitud));

        // GUARDAR LOS DATOS EN LA COLECCION FICHAJES
        db.collection("fichajes").add(fichaje)
                .addOnSuccessListener(documentReference -> {
                    Utilidades.mostrarMensajes(actividad,0,"Has fichado con éxito");
                })
                .addOnFailureListener(e -> {
                    Utilidades.mostrarMensajes(actividad,1,"Error al fichar");
                });

    }

}
