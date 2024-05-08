package com.example.cmct.modelo.cliente;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;

public class RegistrarNecesidades extends AppCompatActivity {

    CheckBox[] necesidades;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_registrar_necesidades);

        necesidades = new CheckBox[7];
        necesidades[0] = findViewById(R.id.cbAseoPersonal);
        necesidades[1] = findViewById(R.id.cbCompra);
        necesidades[2] = findViewById(R.id.cbComida);
        necesidades[3] = findViewById(R.id.cbAyudaDomestica);
        necesidades[4] = findViewById(R.id.cbAcompañamiento);
        necesidades[5] = findViewById(R.id.cbAyudaMovilidad);
        necesidades[6] = findViewById(R.id.cbEjerciciosCognitivos);
    }

    // CLICK BOTON ACEPTAR PARA QUE ABRA UN DIALOGO PREGUNTANDO SI
    // SE ESTA SEGURO DE LA ELECCION DE LAS NECESIDADES
    public void clickBotonAceptarRegistrarNecesidades(View view) {

        mostrarDialogo();

    }

    // METODO PARA MOSTRAR EL DIALOGO
    private void mostrarDialogo() {

        // CREAR UN DIALOGO EN CASO DE QUE HAYA ALGO MAL EN LOS CAMPOS RELLENADOS
        final AlertDialog.Builder ventana = new AlertDialog.Builder(this);
        ventana.setTitle("¿Estás seguro de tus necesidades?");
        String marcados = "";

        // VARIABLE PARA COMPROBAR SI HAY ALGUNA NECESIDAD MARCADA
        int numMarcados = 0;

        // RECORRER TODOS LOS CHECKBOX DE NECESIDADES
        for (int i = 0; i < necesidades.length; i++) {

            // COMPROBAR SI LA NECESIDAD ESTA MARCADA Y LA AÑADIMOS AL DIALOGO
            if(necesidades[i].isChecked()) {

                marcados += "- " + necesidades[i].getText().toString()+"\n";
                numMarcados += 1;

            }

        }

        // COMPROBAR SI NINGUNA NECESIDAD ESTA MARCADA PARA CAMBIAR EL DIALOGO Y LO MUESTRE
        if(numMarcados == 0) {

            ventana.setTitle("NO HAS MARCADO NINGUNA NECESIDAD");
            ventana.setMessage("");

        } else {

            ventana.setMessage(marcados);

            // BOTON SI PARA GUARDAR EN LA BASE DE DATOS LAS NECESIDADES SELECCIONADAS
            ventana.setNegativeButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            // BOTON PARA CANCELAR LA OPERACION Y CERRAR EL DIALOGO
            ventana.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

        }

        ventana.show();
    }
}