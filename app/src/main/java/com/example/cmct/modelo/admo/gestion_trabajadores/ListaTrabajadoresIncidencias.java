package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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
        Calendar c = Calendar.getInstance();
        int anioActual = c.get(Calendar.YEAR);
        int mesActual = c.get(Calendar.MONTH);
        int diaActual = c.get(Calendar.DAY_OF_MONTH);
        tvFecha.setText(anioActual+"/"+mesActual+"/"+diaActual);

        this.recyclerTrabajadores = findViewById(R.id.recyclerListaTrabajadoresIncidencias);
        this.recyclerTrabajadores.setLayoutManager(new LinearLayoutManager(this));

        // RELLENAR LA LISTA
        obtenerTrabajadoresConIncidencias();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

    }

    private void obtenerTrabajadoresConIncidencias() {
        Calendar calendario = Calendar.getInstance();
        SimpleDateFormat formatearFecha = new SimpleDateFormat("yyyy/MM/dd");

        // OBTENER LA FECHA QUE ESTA PUESTA EN EL TEXTO
        try {
            Date fechaActual = formatearFecha.parse(tvFecha.toString());
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
                        Utilidades.mostrarMensajes(this,2,"No hay incidencias que mostrar");
                    }
                })
                .addOnFailureListener(e -> {
                    Utilidades.mostrarMensajes(this,1,"Error al cargar incidencias");
                });
    }

    // CLICK EN LA IMAGEN DE CALENDARIO PARA SELECCIONAR UNA FECHA Y MOSTRAR LAS INCIDENCIAS DE LA FECHA SELECCIONADA
    public void SeleccionarFecha(View view) {

        Calendar calendario = Calendar.getInstance();
        SimpleDateFormat formatearFecha = new SimpleDateFormat("yyyy/MM/dd");

        // OBTENER LA FECHA QUE ESTA PUESTA EN EL TEXTO
        try {
            Date date = formatearFecha.parse(tvFecha.toString());
            calendario.setTime(date);
        } catch (ParseException e) {
            // SI HAY UN ERROR AL PARSEAR LA FECHA SE UTILIZA LA FECHA ACTUAL
            calendario.setTime(new Date());
        }

        // CREAR UN DIALOGO CON FORMATO DE CALENDARIO PARA MOSTRAR LAS INCIDENCIAS DE LA FECHA QUE SE SELECCIONE
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int anio, int mesAnio, int diaMes) {
                        String fechaSeleccionada = String.format(Locale.getDefault(), "%d-%02d-%02d", anio, mesAnio + 1, diaMes);
                        tvFecha.setText(fechaSeleccionada);
                        // ACTUALIZAR LA LISTA
                        obtenerTrabajadoresConIncidencias();
                    }
                }, calendario.get(Calendar.YEAR), calendario.get(Calendar.MONTH), calendario.get(Calendar.DAY_OF_MONTH));

        // NO PERMITIR QUE EL USUARIO PUEDA SELECCIONAR UNA FECHA QUE AUN NO HA LLEGADO
        datePickerDialog.getDatePicker().setMaxDate(calendario.getTimeInMillis());

        // MOSTRAR EL DIALOGO
        datePickerDialog.show();
    }

    private void cargarTrabajadores(List<String> dniTrabajadores) {
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

}
