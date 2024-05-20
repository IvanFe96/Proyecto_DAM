package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;

public class GestionTrabajadores extends AppCompatActivity {

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_gestor_trabajadores);
    }

    // CLICK DEL BOTON "Ver Trabajadores"
    public void clickBotonVerTrabajadores(View view) {
        // INICIAR LA NUEVA PANTALLA PARA VER LOS TRABAJADORES DE LA EMPRESA
        intent = new Intent(this, VerTrabajadores.class);
        startActivity(intent);
    }

    // CLICK DEL BOTON "Asignar Trabajo"
    public void clickBotonAsignarTrabajo(View view) {
        // INICIAR LA NUEVA PANTALLA PARA ASIGNAR CLIENTES A LOS TRABAJADORES
        intent = new Intent(this, ListaTrabajadoresAsignarTrabajo.class);
        intent.setAction("ASIGNARTRABAJO");
        startActivity(intent);
    }

    // CLICK DEL BOTON "VerValoraciones"
    public void clickBotonValoraciones(View view) {
        // INICIAR LA NUEVA PANTALLA PARAVER LAS VALORACIONES QUE TIENEN LOS TRABAJADORES
        intent = new Intent(this, VerValoraciones.class);
        startActivity(intent);
    }

    // CLICK DEL BOTON "Incidencias"
    public void clickBotonIncidencias(View view) {
        // INICIAR LA NUEVA PANTALLA PARA VER LAS INCIDENCIAS DE LOS TRABAJADORES
        intent = new Intent(this, ListaTrabajadoresIncidencias.class);
        intent.setAction("INCIDENCIAS");
        startActivity(intent);
    }

    // CLICK DEL BOTON "Ver fichaje"
    public void clickBotonVerFichaje(View view) {
        // INICIAR LA NUEVA PANTALLA PARA VER LA HORA Y LUGAR DONDE HAN FICHADO LOS TRABAJADORES
        intent = new Intent(this, BuscarTrabajador.class);
        intent.setAction("FICHAJE");
        startActivity(intent);
    }
}
