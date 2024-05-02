package com.example.cmct.modelo.trabajador;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;

public class DatosCliente extends AppCompatActivity {
    TextView datosCliente;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trabajador_horario_cliente);

        datosCliente = findViewById(R.id.tvDatosCliente);
    }
}
