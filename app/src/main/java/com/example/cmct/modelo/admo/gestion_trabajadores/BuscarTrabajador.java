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
import com.example.cmct.clases.Utilidades;

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
            Utilidades.mostrarMensajes(this,1,"Introduce el DNI del trabajador");
        } else if (!validarDni(buscadorTrabajador.getText().toString())) {
            // EL DNI INTRODUCIDO NO ES VALIDO
            Utilidades.mostrarMensajes(this,1,"El DNI no es válido");
        } else {
            // TODO ESTA CORRECTO PARA BUSCAR AL TRABAJADOR
            // OBTENER EL INTENTO DEL QUE PROCEDE
            Intent intentPadre = getIntent();

            // COMPROBAR LA PROCEDENCIA DEL INTENT PARA SABER SI HAY QUE
            // VER LAS HORAS DE FICHAJE DEL TRABAJADOR
             if (intentPadre.getAction().equals("FICHAJE")) {

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

}
