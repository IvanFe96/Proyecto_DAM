package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

    TextClock[] horarios;

    Button botonAceptar;

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

        horarios = new TextClock[12];
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

            nombreTrabajador.setText(intent.getStringExtra("nombre"));

        } else if (intent.getAction().equals("EDITAR")) {
            // LA ACTIVIDAD ES INICIADA DESDE EL BOTON EDITAR Y SE PROCEDE A RELLENAR
            // LOS DESPLEGABLES Y LOS HORARIOS CON LOS DATOS QUE YA TIENE EL TRABAJADOR

        }
    }

    // CLICK EN BOTON ACEPTAR PARA QUE REGISTRE EN LA BASE DE DATOS LOS DATOS RECOGIDOS
    // EN LOS SPINNERS Y EN LOS TEXTCLOCK
    public void clickBotonAceptarAsignarTrabajo(View v) {
        finish();
    }

    public void mostrarTimePickerDialog(View view) {

        final TextClock textClock = (TextClock) view; // ALMACENAR UNA REFERENCIA AL TEXTCLOCK AL QUE HAGO CLICK
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // DETERMINAR SI ES AM O PM
                String amOPm = (hourOfDay < 12) ? "AM" : "PM";

                // Aquí puedes hacer lo que quieras con la hora seleccionada
                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d ", hourOfDay, minute, amOPm);
                textClock.setText(selectedTime);
            }
        }, 12, 0, false);
        timePickerDialog.show();
    }


}
