package com.example.cmct;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.modelo.admo.EleccionGestion;

public class MainActivity extends AppCompatActivity {

    Button btnIniciarSesion;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
    }

    // CLICK DEL BOTON ENTRAR
    public void clickBotonIniciarSesion(View view) {
        //Iniciar la nueva pantalla para elegir entre gestionar trabajadores o clientes
        Intent intent = new Intent(this, EleccionGestion.class);
        startActivity(intent);
    }
}