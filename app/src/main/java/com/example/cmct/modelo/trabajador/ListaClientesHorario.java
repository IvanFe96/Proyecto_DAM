package com.example.cmct.modelo.trabajador;

import android.content.Context;
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
                .whereEqualTo("trabajadorAsignado", dniTrabajador);

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
