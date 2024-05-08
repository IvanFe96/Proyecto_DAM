package com.example.cmct.modelo.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;

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

}