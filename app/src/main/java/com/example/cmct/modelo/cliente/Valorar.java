package com.example.cmct.modelo.cliente;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;

public class Valorar extends AppCompatActivity {

    Button botonAceptar;
    RatingBar valoracion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_valorar);

        botonAceptar = findViewById(R.id.btnAceptarValorar);
        valoracion = findViewById(R.id.ratingBarValorar);
    }

    // CLICK DEL BOTON ACEPTAR PARA GUARDAR LA VALORACION DEL CLIENTE EN LA BASE DE DATOS
    public void clickBotonAceptar(View view) {

    }
}