package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;

public class BuscarTrabajador extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_buscar_trabajador);
    }

    public void clickBotonBuscarTrabajador(View v) {

        // OBTENER EL INTENTO DEL QUE PROCEDE
        Intent intentPadre = getIntent();

        // COMPROBAR LA PROCEDENCIA DEL INTENT PARA SABER SI HAY QUE
        // EDITAR EL TRABAJO ASIGNADO O VER LAS HORAS DE FICHAJE DEL TRABAJADOR
        if(intentPadre.getAction().equals("EDITARTRABAJO")) {

            Intent intent = new Intent(v.getContext(), AsignarTrabajo.class);
            intent.putExtra("nombre", "Trabajador2");
            intent.setAction("EDITAR");
            v.getContext().startActivity(intent);

        } else if (intentPadre.getAction().equals("FICHAJE")) {

            Intent intent = new Intent(v.getContext(), Fichaje.class);
            v.getContext().startActivity(intent);

        }
    }
}
