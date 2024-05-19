package com.example.cmct.modelo.admo.gestion_trabajadores;

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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ListaTrabajadoresAsignarTrabajo extends AppCompatActivity {
    AdaptadorTrabajadorSimple adaptadorTrabajadorSimple;
    RecyclerView recyclerTrabajadores;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_lista_trabajadores_asignar_trabajo);

        this.recyclerTrabajadores = findViewById(R.id.recyclerListaTrabajadoresAsignarTrabajo);
        this.recyclerTrabajadores.setLayoutManager(new LinearLayoutManager(this));

        // RELLENAR LA LISTA
        obtenerTrabajadores();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        this.recyclerTrabajadores.setLayoutManager(linearLayoutManager);
        this.recyclerTrabajadores.setAdapter(adaptadorTrabajadorSimple);
    }

    // OBTENER LOS TRABAJADORES QUE SI ESTAN ASIGNADOS PARA DESPUES SABER CUALES NO ESTAN ASIGNADOS
    private void obtenerTrabajadores() {
        // LISTA PARA GUARDAR LOS DNIS DE LOS TRABAJADORES QUE SI ESTAN ASIGNADOS
        List<String> trabajadoresAsignados = new ArrayList<String>();

        FirebaseFirestore.getInstance().collection("usuarios")
                .whereEqualTo("rol", "cliente")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // COMPROBAR QUE EL CLIENTE TIENE EL CAMPO DEL TRABAJADOR ASIGNADO
                            // Y QUE ESTE NO ES NULO LO QUE INDICA QUE TIENE UN TRABAJADOR ASIGNADO
                            if (document.contains("trabajadorAsignado") && document.getString("trabajadorAsignado") != null) {
                                trabajadoresAsignados.add(document.getString("trabajadorAsignado"));
                            }
                        }
                        // BUSCAR LOS TRABAJADORES QUE NO TIENEN CLIENTES ASIGNADOS
                        buscarTrabajadoresNoAsignados(trabajadoresAsignados);
                    } else {
                        mostrarMensajes(getApplicationContext(),1,"Error al obtener datos");
                    }
                });
    }

    private void buscarTrabajadoresNoAsignados(List<String> trabajadoresAsignados) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // BUSCAR TODOS LOS TRABAJADORES PARA COMPARARLOS CON LA LISTA PASADA
        db.collection("usuarios")
                .whereEqualTo("rol", "trabajador")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // LISTA PARA GUARDAR LOS TRABAJADORES QUE NO TIENEN ASIGNADO TRABAJO
                        List<String> trabajadoresNoAsignados = new ArrayList<>();

                        // RECORRER LOS TRABAJADORES OBTENIDOS
                        for (DocumentSnapshot document : task.getResult()) {
                            String dni = document.getString("dni");
                            // COMPROBAR SI EL TRABAJADOR NO ESTA EN LA LISTA DE LOS TRABAJADORES QUE SI QUE TIENEN TRABAJO ASIGNADO
                            if (!trabajadoresAsignados.contains(dni)) {
                                // GUARDAR EL TRABAJADOR EN LA LISTA DE LOS NO ASIGNADOS
                                trabajadoresNoAsignados.add(document.getString("dni"));
                            }
                        }

                        // COMPROBAR SI LA LISTA ESTA LLENA PARA CARGAR EL RECYCLERVIEW
                        if(!trabajadoresNoAsignados.isEmpty()) {
                            // ACTUALIZAR EL ADAPTADOR CON LA LISTA DE TRABAJADORES NO ASIGNADOS
                            cargarTrabajadores(trabajadoresNoAsignados);
                        } else {
                            // LA LISTA ESTA VACIA Y SE MUESTRA UN MENSAJE INDICANDOLO
                            mostrarMensajes(getApplicationContext(),0,"Todos los trabajadores están asignados");
                            finish();
                        }

                    } else {
                        mostrarMensajes(getApplicationContext(),1,"Error al obtener datos");
                    }
                });
    }

    // PASAR AL ADAPTADOR LA LISTA DE TRABAJADORES QUE NO TIENEN ASIGNADO TRABAJO PARA MOSTRARLOS EN LA LISTA
    private void cargarTrabajadores(List<String> dniTrabajadores) {
        // SENTENCIA PARA SABER LOS TRABAJADORES QUE HAY EN LA LISTA QUE HEMOS PASADO
        // LO CUAL INDICA QUE LOS TRABAJADORES QUE ESTEN EN LA LISTA NO TIENEN NINGUN CLIENTE ASIGNADO
        Query sentencia = FirebaseFirestore.getInstance().collection("usuarios")
                .whereEqualTo("rol", "trabajador")
                .whereIn("dni", dniTrabajadores)
                .orderBy("nombre", Query.Direction.ASCENDING);

        // OBTENER LOS TRABAJADORES QUE NO TIENEN CLIENTES ASIGNADOS
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
