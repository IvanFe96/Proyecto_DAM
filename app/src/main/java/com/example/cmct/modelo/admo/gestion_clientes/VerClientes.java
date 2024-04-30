package com.example.cmct.modelo.admo.gestion_clientes;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;
import com.example.cmct.modelo.Adaptador;

public class VerClientes extends AppCompatActivity {
    Adaptador adaptador;
    RecyclerView recyclerClientes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_ver_clientes);

        String[][] lista = new String[3][4];

        for (int i = 0; i < lista.length; i++) {
            lista[i][0] = "Cliente"+i;
            lista[i][1] = i+""+i+""+i+""+i+""+i+""+i+""+i+""+i+""+i;
            lista[i][2] = "cliente"+i+"@gmail.com";
            lista[i][3] = "C/Los Cliente"+i+" Nº"+i;
        }

        recyclerClientes = findViewById(R.id.recyclerClientes);
        adaptador = new Adaptador(lista);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerClientes.setLayoutManager(linearLayoutManager);
        recyclerClientes.setAdapter(adaptador);
    }
}
