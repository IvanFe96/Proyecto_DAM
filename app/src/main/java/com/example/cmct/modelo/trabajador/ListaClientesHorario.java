package com.example.cmct.modelo.trabajador;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;
import com.example.cmct.clases.Cliente;
import com.example.cmct.modelo.trabajador.adaptadores.AdaptadorClienteHorarios;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ListaClientesHorario extends AppCompatActivity {
    AdaptadorClienteHorarios adaptadorClienteHorarios;
    RecyclerView lista;

    // OBTENER LA INSTANCIA DE LA BASE DE DATOS DE FIREBASE
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trabajador_lista_clientes_horario);

        lista = findViewById(R.id.recyclerListaClientesHorario);

        buscarHorario(getIntent().getStringExtra("dniTrabajador"));
    }

    // BUSCAR LOS CLIENTES EN LOS QUE APARECE EL TRABAJADOR PARA AÑADIRLOS A LA LISTA
    private void buscarHorario(String dniTrabajador) {
        Query sentencia = db
                .collection("usuarios")
                .whereEqualTo("rol", "cliente")
                .whereEqualTo("trabajadorAsignado", dniTrabajador)
                .orderBy("horaEntradaTrabajador");

        FirestoreRecyclerOptions<Cliente> listaClientes = new FirestoreRecyclerOptions.Builder<Cliente>()
                .setQuery(sentencia, Cliente.class)
                .build();

        adaptadorClienteHorarios = new AdaptadorClienteHorarios(listaClientes,this);
        lista.setLayoutManager(new LinearLayoutManager(this));
        lista.setAdapter(adaptadorClienteHorarios);
    }

    // GESTION DE RECURSOS
    @Override
    protected void onResume() {
        super.onResume();
        if (adaptadorClienteHorarios != null) {
            adaptadorClienteHorarios.notifyDataSetChanged();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (adaptadorClienteHorarios != null) {
            adaptadorClienteHorarios.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adaptadorClienteHorarios != null) {
            adaptadorClienteHorarios.stopListening();
        }
    }
}
