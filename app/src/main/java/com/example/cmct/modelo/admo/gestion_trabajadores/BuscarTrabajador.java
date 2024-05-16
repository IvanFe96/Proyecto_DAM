package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;

public class BuscarTrabajador extends AppCompatActivity {

    EditText buscadorTrabajador;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_buscar_trabajador);

        buscadorTrabajador = findViewById(R.id.etBuscadorTrabajadores);
    }

    public void clickBotonBuscarTrabajador(View v) {

        // COMPROBAR QUE EL CAMPO DE BUSCADOR NO ESTE VACIO
        if(buscadorTrabajador.getText().toString().isEmpty()) {
            mostrarMensajes(1,"Introduce el DNI del trabajador");
        } else if (!validarDni(buscadorTrabajador.getText().toString())) {
            // EL DNI INTRODUCIDO NO ES VALIDO
            mostrarMensajes(1,"El DNI no es válido");
        } else {
            // TODO ESTA CORRECTO PARA BUSCAR AL TRABAJADOR
            // OBTENER EL INTENTO DEL QUE PROCEDE
            Intent intentPadre = getIntent();

            // COMPROBAR LA PROCEDENCIA DEL INTENT PARA SABER SI HAY QUE
            // EDITAR EL TRABAJO ASIGNADO O VER LAS HORAS DE FICHAJE DEL TRABAJADOR
            if(intentPadre.getAction().equals("EDITARTRABAJO")) {

                Intent intent = new Intent(v.getContext(), AsignarTrabajo.class);
                intent.putExtra("nombre", "Trabajador2");
                intent.setAction("EDITAR");
                v.getContext().startActivity(intent);

            } else if (intentPadre.getAction().equals("FICHAJE")) {

                Intent intent = new Intent(v.getContext(), VerFichaje.class);
                intent.putExtra("dni", buscadorTrabajador.getText().toString().toUpperCase());
                v.getContext().startActivity(intent);

            }
        }
    }

    // COMPROBAR QUE EL DNI ES CORRECTO
    private boolean validarDni(String dni) {
        // VERIFICAR QUE EL DNI TIENE LONGITUD 9
        if (dni == null || dni.length() != 9) {
            return false;
        }

        // EXTRAER EL NUMERO Y LA LETRA DEL DNI
        String numero = dni.substring(0, 8);
        String letra = dni.substring(8).toUpperCase();

        // VERIFICAR QUE EL NUMERO DEL DNI ES UN NUMERO
        try {
            Integer.parseInt(numero);
        } catch (NumberFormatException e) {
            return false;
        }

        // CALCULAR LA LETRA CORRESPONDIENTE AL DNI
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        int indice = Integer.parseInt(numero) % 23;
        char letraCalculada = letras.charAt(indice);

        // COMPROBAR SI LA LETRA CALCULADA COINCIDE CON LA LETRA DEL DNI
        return letraCalculada == letra.charAt(0);
    }

    // MOSTRAR TOAST PERSONALIZADOS DE ERRORES Y DE QUE TODO HA IDO CORRECTO
    private void mostrarMensajes(int tipo, String mensaje) {
        // MENSAJE DE QUE ES CORRECTO
        if(tipo == 0) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_personalizado, null);

            TextView text = (TextView) layout.findViewById(R.id.toast_text);
            text.setText(mensaje); // CONFIGURAR EL MENSAJE PERSONALIZADO

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 300);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        } else {
            // MENSAJE DE ERRORES
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_personalizado_error, null);

            TextView text = (TextView) layout.findViewById(R.id.toast_text);
            text.setText(mensaje); // CONFIGURAR EL MENSAJE DE ERROR PERSONALIZADO

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 300);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }
    }
}
