package com.example.cmct.modelo.admo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.Login;
import com.example.cmct.R;
import com.example.cmct.clases.Utilidades;
import com.example.cmct.modelo.admo.gestion_clientes.VerClientes;
import com.example.cmct.modelo.admo.gestion_trabajadores.GestionTrabajadores;
import com.google.firebase.auth.FirebaseAuth;

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
        intent = new Intent(this, VerClientes.class);
        startActivity(intent);
    }

    // METODO PARA CUANDO EL USUARIO VAYA HACIA ATRAS SE CIERRE LA SESION DEL MISMO Y VAYA A LA PANTALLA DEL LOGIN

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Utilidades.mostrarMensajes(EleccionGestion.this,2,"Cerrando sesión...");

        // RETRASAR LA EJECUCION PARA CERRAR DE MANERA CORRECTA LA SESION DEL USUARIO
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // CERRAR SESION CON EL USUARIO REGISTRADO
                FirebaseAuth.getInstance().signOut();
                // REDIRIGIR AL USUARIO A LA PANTALLA DE LOGIN
                finish(); // CERRAR LA PANTALLA ACTUAL PARA EVITAR QUE EL USUARIO REGRESE
            }
        }, 2000); // RETRASO
    }
}
