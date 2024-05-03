package com.example.cmct.modelo.admo.gestion_clientes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;

public class GestionClientes extends AppCompatActivity {
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_gestor_clientes);
    }

    // CLICK DEL BOTON "Ver Trabajadores"
    public void clickBotonAltaCliente(View view) {
        //Iniciar la nueva pantalla para ver los trabajadores de la empresa
        intent = new Intent(this, AltaModificacionCliente.class);
        startActivity(intent);
    }

    // CLICK DEL BOTON "Ver Clientes"
    public void clickBotonVerClientes(View view) {
        //Iniciar la nueva pantalla para ver todos los clientes que tengo
        intent = new Intent(this, VerClientes.class);
        startActivity(intent);
    }

}
