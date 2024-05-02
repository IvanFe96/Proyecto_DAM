package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;
import com.example.cmct.modelo.admo.gestion_clientes.AltaCliente;

public class GestionTrabajadores extends AppCompatActivity {

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_gestor_trabajadores);
    }

    // CLICK DEL BOTON "Ver Trabajadores"
    public void clickBotonVerTrabajadores(View view) {
        //Iniciar la nueva pantalla para ver los trabajadores de la empresa
        intent = new Intent(this, VerTrabajadores.class);
        startActivity(intent);
    }

    // CLICK DEL BOTON "Asignar Trabajo"
    public void clickBotonAsignarTrabajo(View view) {
        //Iniciar la nueva pantalla para asignar clientes a los trabajadores
        intent = new Intent(this, ListaTrabajadoresAsignarTrabajo.class);
        intent.setAction("ASIGNARTRABAJO");
        startActivity(intent);
    }

    // CLICK DEL BOTON "Editar Trabajo Asignado"
    public void clickBotonEditarTrabajoAsignado(View view) {
        //Iniciar la nueva pantalla para editar el horario del trabajador
        intent = new Intent(this, BuscarTrabajador.class);
        intent.setAction("EDITARTRABAJO");
        startActivity(intent);
    }

    // CLICK DEL BOTON "Valoraciones"
    public void clickBotonValoraciones(View view) {
        //Iniciar la nueva pantalla para ver las valoraciones de los clientes
        intent = new Intent(this, Valoraciones.class);
        startActivity(intent);
    }

    // CLICK DEL BOTON "Incidencias"
    public void clickBotonIncidencias(View view) {
        //Iniciar la nueva pantalla para ver las incidencias de los trabajadores
        intent = new Intent(this, ListaTrabajadoresIncidencias.class);
        intent.setAction("INCIDENCIAS");
        startActivity(intent);
    }

    // CLICK DEL BOTON "Ver fichaje"
    public void clickBotonVerFichaje(View view) {
        //Iniciar la nueva pantalla para ver la hora y lugar donde han fichado los trabajadores
        intent = new Intent(this, BuscarTrabajador.class);
        intent.setAction("FICHAJE");
        startActivity(intent);
    }
}
