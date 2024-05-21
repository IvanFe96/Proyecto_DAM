package com.example.cmct.modelo.trabajador;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cmct.R;
import com.example.cmct.clases.Trabajador;
import com.example.cmct.clases.Utilidades;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class EleccionTrabajador extends AppCompatActivity {

    Button botonFichar, botonIncidencia, botonHorario;

    FusedLocationProviderClient localizacion;

    // OBTENER LA INSTANCIA DE LA BASE DE DATOS DE FIREBASE
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Trabajador trabajador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trabajador_eleccion);

        botonFichar = findViewById(R.id.btnFichar);
        botonIncidencia = findViewById(R.id.btnIncidencia);
        botonHorario = findViewById(R.id.btnHorario);

        // OBTENER EL TRABAJADOR AUTENTICADO
        obtenerTrabajador();

        // INICIALIZAR LA LOCALIZACION PARA MAS TARDE OBTENERLA EN CASO DE QUERER FICHAR
        localizacion = LocationServices.getFusedLocationProviderClient(this);

    }

    // CLICK BOTON PARA GUARDAR EN LA BASE DE DATOS LA FECHA Y UBICACION DEL TRABAJADOR
    public void clickBotonFichar(View view) {
        obtenerUbicacion();
    }

    // OBTENER LA LANGITUD Y LATITUD DEL TRABAJADOR PARA PODER FICHAR
    private void obtenerUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            localizacion.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    trabajador.fichar(this, latitude, longitude);
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    // CLICK BOTON PARA INICIAR PANTALLA DE CREAR UNA INCIDENCIA
    public void clickBotonIncidencia(View view) {
        Intent intento = new Intent(this, CrearIncidencias.class);
        startActivity(intento);
    }

    // CLICK BOTON PARA INICIAR PANTALLA DE HORARIO
    public void clickBotonHorario(View view) {
        Intent intento = new Intent(this, ListaClientesHorario.class);
        intento.putExtra("dniTrabajador", trabajador.getDni());
        startActivity(intento);
    }

    // OBTENER AL TRABAJADOR AUTENTICADO
    private void obtenerTrabajador() {
        // OBTENER LA ID DEL USUARIO TRABAJADOR QUE ESTA AUTENTICADO
        String idUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // OBTENER LOS DATOS DEL TRABAJADOR
        db.collection("usuarios")
                .document(idUsuario)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.exists()) {
                        // OBTENER EL OBJETO TRABAJADOR
                        trabajador = queryDocumentSnapshots.toObject(Trabajador.class);
                    } else {
                        Utilidades.mostrarMensajes(this, 1, "No se ha encontrado al trabajador");
                    }
                })
                .addOnFailureListener(e -> {
                    Utilidades.mostrarMensajes(this, 1, "Error al buscar datos del trabajador");
                });
    }
}