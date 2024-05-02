package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;
import com.example.cmct.modelo.admo.adaptadores.AdaptadorTrabajadorSimple;

public class ListaTrabajadoresAsignarTrabajo extends AppCompatActivity {
    AdaptadorTrabajadorSimple adaptadorTrabajadorSimple;
    RecyclerView lista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_lista_trabajadores_asignar_trabajo);

        String[] lista = new String[4];

        for (int i = 0; i < lista.length; i++) {
            lista[i] = "Trabajador"+i;
        }

        this.lista = findViewById(R.id.recyclerListaTrabajadoresAsignarTrabajo);
        adaptadorTrabajadorSimple = new AdaptadorTrabajadorSimple(lista);

        // PASAMOS AL ADAPTADOR EL INTENTO DEL QUE PROCEDE
        Intent intent = getIntent();
        adaptadorTrabajadorSimple.obtenerIntent(intent);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        this.lista.setLayoutManager(linearLayoutManager);
        this.lista.setAdapter(adaptadorTrabajadorSimple);
    }
}
