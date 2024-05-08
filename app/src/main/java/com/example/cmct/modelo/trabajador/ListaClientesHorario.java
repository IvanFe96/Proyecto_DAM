package com.example.cmct.modelo.trabajador;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;
import com.example.cmct.modelo.trabajador.adaptadores.AdaptadorClienteHorarios;

public class ListaClientesHorario extends AppCompatActivity {
    AdaptadorClienteHorarios adaptadorClienteHorarios;
    RecyclerView lista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trabajador_lista_clientes_horario);

        String[] lista = new String[4];

        for (int i = 0; i < lista.length; i++) {
            lista[i] = "Cliente"+i;
        }

        this.lista = findViewById(R.id.recyclerListaClientesHorario);
        adaptadorClienteHorarios = new AdaptadorClienteHorarios(lista);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        this.lista.setLayoutManager(linearLayoutManager);
        this.lista.setAdapter(adaptadorClienteHorarios);
    }
}
