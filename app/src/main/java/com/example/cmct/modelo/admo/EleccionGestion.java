package com.example.cmct.modelo.admo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;
import com.example.cmct.modelo.admo.gestion_clientes.GestionClientes;
import com.example.cmct.modelo.admo.gestion_trabajadores.GestionTrabajadores;

public class EleccionGestion extends AppCompatActivity {
    Button btnTrabajadores, btnClientes;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_gestores);

        btnTrabajadores = findViewById(R.id.btnTrabajadores);
        btnClientes = findViewById(R.id.btnClientes);
    }

    // CLICK DEL BOTON "Gestion Trabajadores"
    public void clickBotonTrabajadores(View view) {
        //Iniciar la nueva pantalla para elegir las opciones de trabajadores
        intent = new Intent(this, GestionTrabajadores.class);
        startActivity(intent);
    }

    // CLICK DEL BOTON "Gestion Clientes"
    public void clickBotonClientes(View view) {
        //Iniciar la nueva pantalla para elegir las opciones de clientes
        intent = new Intent(this, GestionClientes.class);
        startActivity(intent);
    }
}
