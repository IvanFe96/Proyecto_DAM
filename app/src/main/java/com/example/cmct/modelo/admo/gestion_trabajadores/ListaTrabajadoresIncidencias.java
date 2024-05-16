package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;
import com.example.cmct.clases.Trabajador;
import com.example.cmct.modelo.admo.adaptadores.AdaptadorTrabajadorSimple;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ListaTrabajadoresIncidencias extends AppCompatActivity {
    AdaptadorTrabajadorSimple adaptadorTrabajadorSimple;
    RecyclerView recyclerTrabajadores;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_lista_trabajadores_con_incidencias);

        this.recyclerTrabajadores = findViewById(R.id.recyclerListaTrabajadoresIncidencias);
        this.recyclerTrabajadores.setLayoutManager(new LinearLayoutManager(this));

        // RELLENAR LA LISTA
        obtenerTrabajadoresConIncidencias();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

    }

    private void obtenerTrabajadoresConIncidencias() {
        // CREAR UNA INSTANCIA DE CALENDAR PARA OBTENER LA FECHA ACTUAL
        Calendar calendario = Calendar.getInstance();
        calendario.set(Calendar.HOUR_OF_DAY,0);
        calendario.set(Calendar.MINUTE,0);
        calendario.set(Calendar.SECOND,0);
        calendario.set(Calendar.MILLISECOND,0);

        // CREAR UNA INSTANCIA DATE PARA GUARDAR EL INICIO DEL DIA
        Date fechaInicio = calendario.getTime();

        // GUARDAR EL FINAL DEL DIA
        calendario.add(Calendar.DATE,1);
        Date fechaFinal = calendario.getTime();

        // CONSULTA PARA SABER QUE INSTANCIAS SE HAN CREADO EN EL DIA ACTUAL UTILIZANDO LAS FECHAS OBTENIDAS ANTERIORMENTE
        FirebaseFirestore.getInstance().collection("incidencias")
                .whereGreaterThanOrEqualTo("fechaIncidencia", new Timestamp(fechaInicio))
                .whereLessThan("fechaIncidencia", new Timestamp(fechaFinal))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // LISTA PARA GUARDAR LOS TRABAJADORES QUE HAYAN HECHO INCIDENCIAS
                    List<String> dniTrabajadores = new ArrayList<>();
                    // GUARDAR EN LA LISTA EL DNI DE LOS TRABAJADORES
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String dniTrabajador = documentSnapshot.getString("dni");
                        if (dniTrabajador != null && !dniTrabajadores.contains(dniTrabajador)) {
                            dniTrabajadores.add(dniTrabajador);
                        }
                    }
                    // COMPROBAR SI LA LISTA ESTA LLENA PARA CARGAR EL RECYCLERVIEW
                    if(!dniTrabajadores.isEmpty()) {
                        cargarTrabajadores(dniTrabajadores);
                    } else {
                        // LA LISTA ESTA VACIA Y SE MUESTRA UN MENSAJE INDICANDOLO
                        mostrarMensajes(getApplicationContext(),0,"Todavía no hay incidencias");
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    mostrarMensajes(getApplicationContext(),1,"Error al cargar incidencias");
                });
    }

    private void cargarTrabajadores(List<String> dniTrabajadores) {
        // SENTENCIA PARA SABER LOS TRABAJADORES QUE HAY EN LA LISTA QUE HEMOS PASADO
        // LO CUAL INDICA QUE LOS TRABAJADORES QUE ESTEN EN LA LISTA TIENEN INCIDENCIAS
        Query sentencia = FirebaseFirestore.getInstance().collection("usuarios")
                .whereEqualTo("rol", "trabajador")
                .whereIn("dni", dniTrabajadores) // Asegúrate de que "id" es el campo correcto en tu colección de usuarios
                .orderBy("nombre", Query.Direction.ASCENDING);

        // OBTENER LOS TRABAJADORES QUE TIENEN INCIDENCIAS
        FirestoreRecyclerOptions<Trabajador> listaTrabajadores = new FirestoreRecyclerOptions.Builder<Trabajador>()
                .setQuery(sentencia, Trabajador.class).build();

        // LLAMAR AL ADAPTADOR PARA QUE RELLENE LA LISTA CON LOS DATOS OBTENIDOS
        adaptadorTrabajadorSimple = new AdaptadorTrabajadorSimple(listaTrabajadores);
        this.recyclerTrabajadores.setAdapter(adaptadorTrabajadorSimple);

        // PASAMOS AL ADAPTADOR EL INTENTO DEL QUE PROCEDE
        Intent intent = getIntent();
        adaptadorTrabajadorSimple.obtenerIntent(intent);
        adaptadorTrabajadorSimple.obtenerActividad(this);
        adaptadorTrabajadorSimple.startListening();
    }

    // GESTION DE RECURSOS
    @Override
    protected void onResume() {
        super.onResume();
        if (adaptadorTrabajadorSimple != null) {
            adaptadorTrabajadorSimple.notifyDataSetChanged();
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
}
