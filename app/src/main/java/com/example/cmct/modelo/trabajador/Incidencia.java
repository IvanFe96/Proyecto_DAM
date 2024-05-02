package com.example.cmct.modelo.trabajador;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;

public class Incidencia extends AppCompatActivity {

    Spinner tipoIncidencia;
    EditText descripcion;
    Button botonGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trabajador_incidencia);

        tipoIncidencia = findViewById(R.id.spinnerIncidencia);
        descripcion = findViewById(R.id.editTextMultilineDescripcionIncidencia);
        botonGuardar = findViewById(R.id.btnGuardarIncidencia);

        // LISTA DE INCIDENCIAS PARA PONER EN EL DESPLEGABLE
        String[] incidencias = {"Médico", "Retraso por tráfico", "Necesidad de cliente", "Apoyo a trabajador"};

        // CREAR ADAPTADOR PARA EL DESPLEGABLE
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, incidencias);

        // ESPECIFICAR EL LAYOUT DEL DESPLEGABLE
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // ASIGNAR EL ADAPTADOR CREADO EN EL DESPLEGABLE
        tipoIncidencia.setAdapter(adaptador);

    }

    // CLICK BOTON PARA GUARDAR LA INCIDENCIA DEL TRABAJADOR EN LA BASE DE DATOS
    public void clickBotonGuardarIncidencia(View view) {
        finish();
    }

}
