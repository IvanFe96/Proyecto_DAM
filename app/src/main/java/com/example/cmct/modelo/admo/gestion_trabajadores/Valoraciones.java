package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;
import com.example.cmct.modelo.admo.adaptadores.AdaptadorValoracion;

public class Valoraciones extends AppCompatActivity {
    AdaptadorValoracion adaptadorValoraciones;
    RecyclerView recyclerValoraciones;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_ver_valoraciones);

        String[][] lista = new String[3][2];

        for (int i = 0; i < lista.length; i++) {
            lista[i][0] = "Trabajador"+i;
            lista[i][1] = String.valueOf(i);
        }

        recyclerValoraciones = findViewById(R.id.recyclerValoraciones);
        adaptadorValoraciones = new AdaptadorValoracion(lista);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerValoraciones.setLayoutManager(linearLayoutManager);
        recyclerValoraciones.setAdapter(adaptadorValoraciones);
    }
}
