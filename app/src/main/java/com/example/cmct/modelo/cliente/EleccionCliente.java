package com.example.cmct.modelo.cliente;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;
import com.example.cmct.clases.Utilidades;
import com.example.cmct.modelo.admo.EleccionGestion;
import com.google.firebase.auth.FirebaseAuth;

public class EleccionCliente extends AppCompatActivity {

    Button botonRegistrarNecesidades, botonValorar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_eleccion);

        botonRegistrarNecesidades = findViewById(R.id.btnRegistrarNecesidadesEleccion);
        botonValorar = findViewById(R.id.btnValorarEleccion);

    }

    // CLICK BOTON PARA INICIAR PANTALLA DE REGISTRAR NECESIDADES DEL CLIENTE
    public void clickBotonRegistrarNecesidades(View view) {
        Intent intent = new Intent(this, RegistrarNecesidades.class);
        startActivity(intent);
    }

    // CLICK BOTON PARA INICIAR PANTALLA DE VALORAR AL TRABAJADOR DEL CLIENTE
    public void clickBotonValorar(View view) {
        Intent intent = new Intent(this, Valorar.class);
        startActivity(intent);
    }

    // METODO PARA CUANDO EL USUARIO VAYA HACIA ATRAS SE CIERRE LA SESION DEL MISMO Y VAYA A LA PANTALLA DEL LOGIN
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Utilidades.mostrarMensajes(EleccionCliente.this,2,"Cerrando sesión...");

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