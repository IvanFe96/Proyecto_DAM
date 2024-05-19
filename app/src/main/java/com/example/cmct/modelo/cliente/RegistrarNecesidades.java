package com.example.cmct.modelo.cliente;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;
import com.example.cmct.clases.Cliente;
import com.example.cmct.clases.Trabajador;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegistrarNecesidades extends AppCompatActivity {

    CheckBox[] necesidades;
    // OBTENER LA INSTANCIA DE LA BASE DE DATOS DE FIREBASE
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Cliente cliente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_registrar_necesidades);

        // OBTENER LOS DATOS DEL CLIENTE
        obtenerCliente();

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
        HashMap<String, String> necesidadesCliente = new HashMap<>();

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

                necesidadesCliente.put("necesidad"+numMarcados,necesidades[i].getText().toString());
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
                    cliente.setNecesidades(necesidadesCliente);
                    cliente.registrarNecesidades(RegistrarNecesidades.this);
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

    private void obtenerCliente() {
        // OBTENER LA ID DEL USUARIO CLIENTE QUE ESTA AUTENTICADO
        String idUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // OBTENER LOS DATOS DEL CLIENTE
        db.collection("usuarios")
                .document(idUsuario)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.exists()) {
                        // OBTENER EL OBJETO TRABAJADOR
                        cliente = queryDocumentSnapshots.toObject(Cliente.class);
                    } else {
                        mostrarMensajes(getApplicationContext(), 1, "No se ha encontrado al cliente");
                    }
                })
                .addOnFailureListener(e -> {
                    mostrarMensajes(getApplicationContext(), 1, "Error al buscar datos del cliente");
                });
    }

    // MOSTRAR TOAST PERSONALIZADOS DE ERRORES Y DE QUE TODO HA IDO CORRECTO
    private void mostrarMensajes(Context contexto, int tipo, String mensaje) {
        // MENSAJE DE QUE ES CORRECTO
        if(tipo == 0) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_personalizado, null);

            TextView text = (TextView) layout.findViewById(R.id.toast_text);
            text.setText(mensaje); // CONFIGURAR EL MENSAJE PERSONALIZADO

            Toast toast = new Toast(contexto.getApplicationContext());
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

            Toast toast = new Toast(contexto.getApplicationContext());
            toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 300);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }
    }
}