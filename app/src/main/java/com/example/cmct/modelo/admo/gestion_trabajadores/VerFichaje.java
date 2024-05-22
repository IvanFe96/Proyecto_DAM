package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;
import com.example.cmct.clases.Fichaje;
import com.example.cmct.clases.Trabajador;
import com.example.cmct.clases.Utilidades;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class VerFichaje extends AppCompatActivity {

    TextView nombreTrabajador;
    TextView[] horasFichajes, mapas;
    Trabajador trabajador;
    ArrayList<Fichaje> fichajes = new ArrayList<Fichaje>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_ver_fichaje);

        nombreTrabajador = findViewById(R.id.tvNombreApellidosFichaje);

        Intent intent = getIntent();

        // OBTENER TRABAJADOR AUTENTICADO
        obtenerTrabajador(intent.getStringExtra("dni"));

        horasFichajes = new TextView[6];
        horasFichajes[0] = findViewById(R.id.textClock1Fichaje);
        horasFichajes[1] = findViewById(R.id.textClock2Fichaje);
        horasFichajes[2] = findViewById(R.id.textClock3Fichaje);
        horasFichajes[3] = findViewById(R.id.textClock4Fichaje);
        horasFichajes[4] = findViewById(R.id.textClock5Fichaje);
        horasFichajes[5] = findViewById(R.id.textClock6Fichaje);

        mapas = new TextView[6];
        mapas[0] = findViewById(R.id.tvMapa1Fichaje);
        mapas[1] = findViewById(R.id.tvMapa2Fichaje);
        mapas[2] = findViewById(R.id.tvMapa3Fichaje);
        mapas[3] = findViewById(R.id.tvMapa4Fichaje);
        mapas[4] = findViewById(R.id.tvMapa5Fichaje);
        mapas[5] = findViewById(R.id.tvMapa6Fichaje);
    }

    // ENCONTRAR LOS FICHAJES DEL TRABAJADOR
    private void obtenerFichajes() {
        // OBTENER LA FECHA DE HOY
        Date today = new Date();
        // OBTENER LA FECHA DE INICIO Y DE FIN DE HOY
        Date fechaInicio = obtenerFechaInicio(today);
        Date fechaFin = obtenerFechaFin(today);

        // VER QUE FICHAJES HA HECHO EL TRABAJADOR
        FirebaseFirestore.getInstance().collection("fichajes")
                .whereEqualTo("dni",trabajador.getDni())
                .whereGreaterThanOrEqualTo("fechaFichaje", new Timestamp(fechaInicio))
                .whereLessThanOrEqualTo("fechaFichaje", new Timestamp(fechaFin))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                        // RECORRER TODOS LOS REGISTROS ENCONTRADOS
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            // OBTENER LOS DATOS DEL FICHAJE
                            fichajes.add(snapshot.toObject(Fichaje.class));
                        }

                        // COMPROBAR SI LA LISTA ESTA VACIA PARA COMUNICARLO AL ADMINISTRADOR
                        if(fichajes.isEmpty()) {
                            // LA LISTA ESTA VACIA Y SE MUESTRA UN MENSAJE INDICANDOLO
                            Utilidades.mostrarMensajes(this,2,trabajador.getNombre() + " todavía no ha realizado ningún fichaje");
                            finish();
                        } else {
                            // LA LISTA NO ESTA VACIA Y SE PROCEDE A RELLENAR CON LOS DATOS OBTENIDOS
                            rellenarHoras();
                        }
                })
                .addOnFailureListener(e -> {

                });

    }

    // RELLENAR LOS CAMPOS DE TEXTO CON LA HORA A LA QUE HA FICHADO EL TRABAJADOR
    private void rellenarHoras() {
        for (int i = 0; i < fichajes.size(); i++) {
            Date fecha = fichajes.get(i).getFechaFichaje().toDate(); // CONVERTIR A FECHA EL OBJETO
            SimpleDateFormat formatoFecha = new SimpleDateFormat("HH:mm"); // FORMATO PARA QUE LA FECHA SEA SOLO HORAS Y MINUTOS
            horasFichajes[i].setText(formatoFecha.format(fecha)); // ESTABLECER LA FECHA EN EL CAMPO CORRESPONDIENTE
        }

        // LOS CAMPOS DE TEXTO QUE NO HAYAN SIDO RELLENADOS SIGNIFICA QUE NO HA FICHADO TODAVIA EL TRABAJADOR
        for (int i = fichajes.size(); i < 6; i++) {
            horasFichajes[i].setText("Sin fichar");
            mapas[i].setEnabled(false);
        }
    }

    // METODO PARA MOSTRAR EL DIALOGO CON EL MAPA Y EL MARCADOR
    private void mostrarDialogoMapa(int numMapa) {
        // CREAR EL DIALOGO
        Dialog dialogo = new Dialog(this);
        dialogo.setContentView(R.layout.dialogo_mapa_fichaje);

        // OBTENER LA REFERENCIA AL MapView DESDE EL DISEÑO DEL DIALOGO
        MapView mapView = dialogo.findViewById(R.id.mapViewFichaje);
        mapView.onCreate(dialogo.onSaveInstanceState());
        mapView.onResume();

        // CONFIGURAR EL MapView
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // UBICACION MEDIANTE LA LONGITUD Y LA LATITUD
                LatLng ubicacion = new LatLng(fichajes.get(numMapa).getLocalizacion().getLatitude(),fichajes.get(numMapa).getLocalizacion().getLongitude());

                // MARCADOR EN ESA UBICACION
                googleMap.addMarker(new MarkerOptions().position(ubicacion).title(""));

                // MOVER LA CAMARA A LA UBICACION
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15f));
            }
        });

        // MOSTRAR EL DIALOGO
        dialogo.show();
    }

    // CLICK DEL MAPA 1 PARA VER LA UBICACION DONDE A FICHADO
    public void clickVerMapa1(View view) {
        manejarClicMapa(0);
    }

    // CLICK DEL MAPA 2 PARA VER LA UBICACION DONDE A FICHADO
    public void clickVerMapa2(View view) {
        manejarClicMapa(1);
    }

    // CLICK DEL MAPA 3 PARA VER LA UBICACION DONDE A FICHADO
    public void clickVerMapa3(View view) {
        manejarClicMapa(2);
    }

    // CLICK DEL MAPA 4 PARA VER LA UBICACION DONDE A FICHADO
    public void clickVerMapa4(View view) {
        manejarClicMapa(3);
    }

    // CLICK DEL MAPA 5 PARA VER LA UBICACION DONDE A FICHADO
    public void clickVerMapa5(View view) {
        manejarClicMapa(4);
    }

    // CLICK DEL MAPA 6 PARA VER LA UBICACION DONDE A FICHADO
    public void clickVerMapa6(View view) {
        manejarClicMapa(5);
    }

    // METODO PARA MANEJAR LOS MAPAS
    private void manejarClicMapa(int numMapa) {
        // COMPROBAR SI HAY MAPA, LO QUE SIGNIFICA QUE HAY FICHAJE REGISTRADO
        if (mapas[numMapa].isEnabled()) {
            // SE MUESTRA UN DIALOGO CON EL MAPA Y UN MARCADOR CON LA UBICACION
            mostrarDialogoMapa(numMapa);
        } else {
            // MOSTRAR MENSAJE INDICANDO QUE NO HAY FICHAJE REGISTRADO
            Utilidades.mostrarMensajes(this,1, "No hay fichaje registrado");
        }
    }

    // OBTENER EL INICIO DEL DIA
    private Date obtenerFechaInicio(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    // OBTENER EL FIN DEL DIA
    private Date obtenerFechaFin(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    // OBTENER AL TRABAJADOR AUTENTICADO
    private void obtenerTrabajador(String dniTrabajador) {
        // OBTENER LOS DATOS DEL TRABAJADOR
        FirebaseFirestore.getInstance().collection("usuarios")
                .whereEqualTo("dni",dniTrabajador)
                .whereEqualTo("rol","trabajador")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots ->  {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // OBTENER EL OBJETO TRABAJADOR
                        trabajador = queryDocumentSnapshots.getDocuments().get(0).toObject(Trabajador.class);
                        
                        // ESTABLECER EL NOMBRE PARA SABER EL TRABAJADOR QUE ES
                        nombreTrabajador.setText(trabajador.getNombre()+" "+trabajador.getApellido1()+" "+trabajador.getApellido2());

                        // MOSTRAR LA HORA A LA QUE HAN FICHADO LOS TRABAJADORES
                        obtenerFichajes();
                    } else {
                        Utilidades.mostrarMensajes(this, 1, "No se ha encontrado al trabajador");
                    }
                })
                .addOnFailureListener(e -> {
                    Utilidades.mostrarMensajes(this, 1, "Error al buscar datos del trabajador");
                });
    }
}
