package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;
import com.example.cmct.clases.Trabajador;
import com.example.cmct.clases.Utilidades;
import com.example.cmct.modelo.admo.adaptadores.AdaptadorTrabajadorSimple;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListaTrabajadoresIncidencias extends AppCompatActivity {

    ImageView ivCalendario;
    TextView tvFecha;
    AdaptadorTrabajadorSimple adaptadorTrabajadorSimple;
    RecyclerView recyclerTrabajadores;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_lista_trabajadores_con_incidencias);

        ivCalendario = findViewById(R.id.ivCalendario);
        tvFecha = findViewById(R.id.tvMostrarFecha);

        // OBTENER LA FECHA DEL DIA ACTUAL
        SimpleDateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaActual = formatoFecha.format(new Date());
        tvFecha.setText(fechaActual);

        this.recyclerTrabajadores = findViewById(R.id.recyclerListaTrabajadoresIncidencias);
        this.recyclerTrabajadores.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar con opciones vacías
        FirestoreRecyclerOptions<Trabajador> listaVaciaParaInicializar = new FirestoreRecyclerOptions.Builder<Trabajador>()
                .setQuery(FirebaseFirestore.getInstance().collection("usuarios").whereEqualTo("dni", "NO_EXISTE"), Trabajador.class)
                .build();

        adaptadorTrabajadorSimple = new AdaptadorTrabajadorSimple(listaVaciaParaInicializar);
        recyclerTrabajadores.setAdapter(adaptadorTrabajadorSimple);
        adaptadorTrabajadorSimple.startListening();

        // RELLENAR LA LISTA
        obtenerTrabajadoresConIncidencias();

    }

    private void obtenerTrabajadoresConIncidencias() {
        Calendar calendario = Calendar.getInstance();
        SimpleDateFormat formatearFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // OBTENER LA FECHA QUE ESTA PUESTA EN EL TEXTO
        try {
            Date fechaActual = formatearFecha.parse(tvFecha.getText().toString());
            calendario.setTime(fechaActual);
        } catch (ParseException e) {
            // SI HAY UN ERROR AL PARSEAR LA FECHA SE UTILIZA LA FECHA ACTUAL
            calendario.setTime(new Date());
        }

        // CREAR UNA INSTANCIA DATE PARA GUARDAR EL INICIO DEL DIA
        Date fechaInicio = calendario.getTime();

        // GUARDAR EL FINAL DEL DIA
        calendario.add(Calendar.DATE,1);
        Date fechaFinal = calendario.getTime();

        // SENTENCIA PARA BUSCAR LAS INCIDENCIAS QUE HAY EN LA FECHA SELECCIONADA
        Query sentencia = FirebaseFirestore.getInstance().collection("incidencias")
                .whereGreaterThanOrEqualTo("fechaIncidencia", new Timestamp(fechaInicio))
                .whereLessThan("fechaIncidencia", new Timestamp(fechaFinal));

        // ESTABLECEMOS UN LISTENER PARA QUE LA LISTA ESTE ESUCHANDO CONSTANTEMENTE CAMBIOS EN LA BASE DE DATOS
        sentencia.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Utilidades.mostrarMensajes(ListaTrabajadoresIncidencias.this, 1, "Error al cargar incidencias");
                    return;
                }

                // LISTA DE TRABAJADORES CON INCIDENCIAS
                List<String> dniTrabajadores = new ArrayList<>();
                for (DocumentSnapshot trabajador : queryDocumentSnapshots) {
                    String dni = trabajador.getString("dni");
                    if (dni != null && !dniTrabajadores.contains(dni)) {
                        dniTrabajadores.add(dni);
                    }
                }

                // COMPROBAR QUE LA LISTA NO ESTE VACIA
                if (!dniTrabajadores.isEmpty()) {
                    // SE CARGA LA LISTA DE TRABAJADORES
                    cargarTrabajadores(dniTrabajadores,fechaInicio,fechaFinal);
                } else {
                    // NO HAY TRABAJADORES ESE DIA, SE CARGA UNA LISTA VACIA Y SE MUESTRA UN MENSAJE INDICANDOLO
                    cargarListaVacia();
                    Utilidades.mostrarMensajes(ListaTrabajadoresIncidencias.this, 2, "No hay incidencias que mostrar");
                }
            }
        });

    }

    // CLICK EN LA IMAGEN DE CALENDARIO PARA SELECCIONAR UNA FECHA Y MOSTRAR LAS INCIDENCIAS DE LA FECHA SELECCIONADA
    public void SeleccionarFecha(View view) {

        Calendar calendario = Calendar.getInstance();
        SimpleDateFormat formatearFecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // OBTENER LA FECHA QUE ESTA PUESTA EN EL TEXTO
        try {
            Date date = formatearFecha.parse(tvFecha.getText().toString());
            calendario.setTime(date);
        } catch (ParseException e) {
            // SI HAY UN ERROR AL PARSEAR LA FECHA SE UTILIZA LA FECHA ACTUAL
            calendario.setTime(new Date());
        }

        // CREAR UN DIALOGO CON FORMATO DE CALENDARIO PARA MOSTRAR LAS INCIDENCIAS DE LA FECHA QUE SE SELECCIONE
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int anio, int mesAnio, int diaMes) {
                        calendario.set(Calendar.YEAR, anio);
                        calendario.set(Calendar.MONTH, mesAnio);
                        calendario.set(Calendar.DAY_OF_MONTH, diaMes);
                        String fechaSeleccionada = formatearFecha.format(calendario.getTime());
                        tvFecha.setText(fechaSeleccionada);
                        // ACTUALIZAR LA LISTA
                        obtenerTrabajadoresConIncidencias();
                    }
                }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH));

        // NO PERMITIR QUE EL USUARIO PUEDA SELECCIONAR UNA FECHA QUE AUN NO HA LLEGADO
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        // MOSTRAR EL DIALOGO
        datePickerDialog.show();
    }

    private void cargarTrabajadores(List<String> dniTrabajadores, Date fechaInicio, Date fechaFinal) {
        // SENTENCIA PARA SABER LOS TRABAJADORES QUE HAY EN LA LISTA QUE HEMOS PASADO
        // LO CUAL INDICA QUE LOS TRABAJADORES QUE ESTEN EN LA LISTA TIENEN INCIDENCIAS
        Query sentencia = FirebaseFirestore.getInstance().collection("usuarios")
                .whereEqualTo("rol", "trabajador")
                .whereIn("dni", dniTrabajadores)
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
        // PASAR LA FECHA PARA PODER FILTRAR LAS INCIDENCIA POR LA FECHA SELECCIONADA
        adaptadorTrabajadorSimple.obtenerFecha(fechaInicio, fechaFinal);
        adaptadorTrabajadorSimple.startListening();
    }

    private void cargarListaVacia() {
        Query sentencia = FirebaseFirestore.getInstance().collection("usuarios")
                .whereEqualTo("rol", "trabajador")
                .whereEqualTo("dni", "NO_EXISTE");

        FirestoreRecyclerOptions<Trabajador> options = new FirestoreRecyclerOptions.Builder<Trabajador>()
                .setQuery(sentencia, Trabajador.class).build();

        adaptadorTrabajadorSimple.updateOptions(options);
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
    @Override
    protected void onStop() {
        super.onStop();
        if (adaptadorTrabajadorSimple != null) {
            adaptadorTrabajadorSimple.stopListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adaptadorTrabajadorSimple != null) {
            adaptadorTrabajadorSimple.startListening();
        }
    }

}
