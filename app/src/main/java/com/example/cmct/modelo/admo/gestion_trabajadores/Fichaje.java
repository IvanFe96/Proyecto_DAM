package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Fichaje extends AppCompatActivity {

    Intent intent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_ver_fichaje);

        intent = getIntent();
    }

    // METODO PARA MOSTRAR EL DIALOGO CON EL MAPA Y EL MARCADOR
    private void mostrarDialogoMapa() {
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
                // UBICACION
                LatLng ubicacion = new LatLng(41.8733481, -0.7916915);

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
        // SE MUESTRA UN DIALOGO CON EL MAPA Y UN MARCADOR CON LA UBICACION
        mostrarDialogoMapa();
    }

    // CLICK DEL MAPA 2 PARA VER LA UBICACION DONDE A FICHADO
    public void clickVerMapa2(View view) {
        // SE MUESTRA UN DIALOGO CON EL MAPA Y UN MARCADOR CON LA UBICACION
        mostrarDialogoMapa();
    }

    // CLICK DEL MAPA 3 PARA VER LA UBICACION DONDE A FICHADO
    public void clickVerMapa3(View view) {
        // SE MUESTRA UN DIALOGO CON EL MAPA Y UN MARCADOR CON LA UBICACION
        mostrarDialogoMapa();
    }

    // CLICK DEL MAPA 4 PARA VER LA UBICACION DONDE A FICHADO
    public void clickVerMapa4(View view) {
        // SE MUESTRA UN DIALOGO CON EL MAPA Y UN MARCADOR CON LA UBICACION
        mostrarDialogoMapa();
    }

    // CLICK DEL MAPA 5 PARA VER LA UBICACION DONDE A FICHADO
    public void clickVerMapa5(View view) {
        // SE MUESTRA UN DIALOGO CON EL MAPA Y UN MARCADOR CON LA UBICACION
        mostrarDialogoMapa();
    }

    // CLICK DEL MAPA 6 PARA VER LA UBICACION DONDE A FICHADO
    public void clickVerMapa6(View view) {
        // SE MUESTRA UN DIALOGO CON EL MAPA Y UN MARCADOR CON LA UBICACION
        mostrarDialogoMapa();
    }
}
