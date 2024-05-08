package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;

import java.util.Locale;

public class AsignarTrabajo extends AppCompatActivity {

    Intent intent;

    TextView nombreTrabajador;

    Spinner[] desplegables;

    TextView[] horarios;

    Button botonAceptar;

    String[] items = new String[]{"Cliente 1", "Cliente 2", "Cliente 3", "Cliente 4", "Cliente 5", "Cliente 6"};

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_asignar_trabajo);

        desplegables = new Spinner[6];
        desplegables[0] = findViewById(R.id.spinner1AsignarTrabajo);
        desplegables[1] = findViewById(R.id.spinner2AsignarTrabajo);
        desplegables[2] = findViewById(R.id.spinner3AsignarTrabajo);
        desplegables[3] = findViewById(R.id.spinner4AsignarTrabajo);
        desplegables[4] = findViewById(R.id.spinner5AsignarTrabajo);
        desplegables[5] = findViewById(R.id.spinner6AsignarTrabajo);

        horarios = new TextView[12];
        horarios[0] = findViewById(R.id.textClock1AsignarTrabajo);
        horarios[1] = findViewById(R.id.textClock2AsignarTrabajo);
        horarios[2] = findViewById(R.id.textClock3AsignarTrabajo);
        horarios[3] = findViewById(R.id.textClock4AsignarTrabajo);
        horarios[4] = findViewById(R.id.textClock5AsignarTrabajo);
        horarios[5] = findViewById(R.id.textClock6AsignarTrabajo);
        horarios[6] = findViewById(R.id.textClock7AsignarTrabajo);
        horarios[7] = findViewById(R.id.textClock8AsignarTrabajo);
        horarios[8] = findViewById(R.id.textClock9AsignarTrabajo);
        horarios[9] = findViewById(R.id.textClock10AsignarTrabajo);
        horarios[10] = findViewById(R.id.textClock11AsignarTrabajo);
        horarios[11] = findViewById(R.id.textClock12AsignarTrabajo);

        nombreTrabajador = findViewById(R.id.tvNombreApellidosAsignarTrabajo);

        intent = getIntent();

        // COMPROBAR SI LA ACTIVIDAD ESTA INICIADA DESDE LA LISTA DE TRABAJADORES QUE NO TIENEN ASIGNADOS CLIENTES
        if(intent.getAction().equals("NUEVO")) {

            // ESTABLECER EL NOMBRE DEL TRABAJADOR DEL CUAL HEMOS SELECCIONADO EN LA PANTALLA ANTERIOR
            nombreTrabajador.setText(intent.getStringExtra("nombre"));

            // CREAR UN ADAPTADOR PARA PONER LOS DATOS DE PRUEBA EN LOS SPINNERS
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // ESTABLECER A LOS DESPLEGABLES LOS DATOS DE PRUEBA DEL ADAPTADOR
            for (Spinner spinner : desplegables) {
                spinner.setAdapter(adapter);
            }

        } else if (intent.getAction().equals("EDITAR")) {
            // LA ACTIVIDAD ES INICIADA DESDE EL BOTON EDITAR Y SE PROCEDE A RELLENAR
            // LOS DESPLEGABLES Y LOS HORARIOS CON LOS DATOS QUE YA TIENE EL TRABAJADOR

            // CREAR UN ADAPTADOR PARA PONER LOS DATOS DE PRUEBA EN LOS SPINNERS
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            // ESTABLECER A LOS DESPLEGABLES LOS DATOS DE PRUEBA DEL ADAPTADOR
            for (Spinner spinner : desplegables) {
                spinner.setAdapter(adapter);
            }
        }
    }

    // CLICK EN BOTON ACEPTAR PARA QUE REGISTRE EN LA BASE DE DATOS LOS DATOS RECOGIDOS
    // EN LOS SPINNERS Y EN LOS TEXTCLOCK
    public void clickBotonAceptarAsignarTrabajo(View v) {
        finish();
    }

    public void mostrarTimePickerDialog(View view) {

        final TextView textView = (TextView) view; // ALMACENAR UNA REFERENCIA AL TEXTVIEW AL QUE HAGO CLICK
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // DETERMINAR SI ES AM O PM
                String amOPm = (hourOfDay < 12) ? "AM" : "PM";

                // Aquí puedes hacer lo que quieras con la hora seleccionada
                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d ", hourOfDay, minute, amOPm);
                textView.setText(selectedTime);
            }
        }, 12, 0, false);
        timePickerDialog.show();
    }


}
