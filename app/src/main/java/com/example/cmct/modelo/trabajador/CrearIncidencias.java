package com.example.cmct.modelo.trabajador;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;
import com.example.cmct.clases.Trabajador;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class CrearIncidencias extends AppCompatActivity {

    Spinner tipoIncidencia;
    EditText descripcion;
    Button botonGuardar;

    // OBTENER LA INSTANCIA DE LA BASE DE DATOS DE FIREBASE
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Trabajador trabajador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trabajador_incidencia);

        tipoIncidencia = findViewById(R.id.spinnerIncidencia);
        descripcion = findViewById(R.id.editTextMultilineDescripcionIncidencia);
        botonGuardar = findViewById(R.id.btnGuardarIncidencia);

        // OBTENER EL TRABAJADOR AUTENTICADO
        obtenerTrabajador();

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
        // COMPROBAR QUE HAYAN ESCRITO UNA DESCRIPCION
        if(descripcion.getText().toString().isEmpty()) {
            mostrarMensajes(getApplicationContext(),1,"Debes dar una descripción");
        } else {
            // OBTENER LA FECHA EN LA QUE SE VA A CREAR LA INCIDENCIA
            Timestamp fechaIncidencia = Timestamp.now();

            // CREAR LA INCIDENCIA
            trabajador.crearIncidencia(this, (String) tipoIncidencia.getSelectedItem(),descripcion.getText().toString(), fechaIncidencia);
            finish();
        }
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

    private void obtenerTrabajador() {
        // OBTENER LA ID DEL USUARIO TRABAJADOR QUE ESTA AUTENTICADO
        String idUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // OBTENER LOS DATOS DEL TRABAJADOR
        db.collection("usuarios")
                .document(idUsuario)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.exists()) {
                        // OBTENER EL OBJETO TRABAJADOR
                        trabajador = queryDocumentSnapshots.toObject(Trabajador.class);
                    } else {
                        mostrarMensajes(getApplicationContext(), 1, "No se ha encontrado al trabajador");
                    }
                })
                .addOnFailureListener(e -> {
                    mostrarMensajes(getApplicationContext(), 1, "Error al buscar datos del trabajador");
                });
    }
}
