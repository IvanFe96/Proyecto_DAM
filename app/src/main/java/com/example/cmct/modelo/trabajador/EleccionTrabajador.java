package com.example.cmct.modelo.trabajador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;

public class EleccionTrabajador extends AppCompatActivity {

    Button botonFichar, botonIncidencia, botonHorario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trabajador_eleccion);

        botonFichar = findViewById(R.id.btnFichar);
        botonIncidencia = findViewById(R.id.btnIncidencia);
        botonHorario = findViewById(R.id.btnHorario);

    }

    // CLICK BOTON PARA GUARDAR EN LA BASE DE DATOS LA FECHA Y UBICACION DEL TRABAJADOR
    public void clickBotonFichar(View view) {

    }

    // CLICK BOTON PARA INICIAR PANTALLA DE CREAR UNA INCIDENCIA
    public void clickBotonIncidencia(View view) {
        Intent intent = new Intent(this, CrearIncidencias.class);
        startActivity(intent);
    }

    // CLICK BOTON PARA INICIAR PANTALLA DE HORARIO
    public void clickBotonHorario(View view) {
        Intent intent = new Intent(this, ListaClientesHorario.class);
        startActivity(intent);
    }

}