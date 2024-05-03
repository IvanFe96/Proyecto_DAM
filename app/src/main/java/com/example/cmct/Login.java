package com.example.cmct;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.modelo.admo.EleccionGestion;
import com.example.cmct.modelo.trabajador.EleccionTrabajador;

public class Login extends AppCompatActivity {

    Button btnIniciarSesion;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
    }

    // CLICK DEL BOTON INICIAR SESION
    public void clickBotonIniciarSesion(View view) {
        // Iniciar la nueva pantalla para elegir entre gestionar trabajadores o clientes
        Intent intent = new Intent(this, EleccionGestion.class);
        startActivity(intent);

        /*Intent intent = new Intent(this, EleccionCliente.class);
        startActivity(intent);*/

        /*Intent intent = new Intent(this, EleccionTrabajador.class);
        startActivity(intent);*/
    }
}