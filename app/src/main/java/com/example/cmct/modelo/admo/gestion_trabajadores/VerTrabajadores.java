package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;
import com.example.cmct.clases.Trabajador;
import com.example.cmct.modelo.admo.adaptadores.AdaptadorVerTrabajadores;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class VerTrabajadores extends AppCompatActivity {
    AdaptadorVerTrabajadores adaptadorVerTrabajadores;
    RecyclerView recyclerTrabajadores;
    Button botonAltaTrabajador;
    EditText buscador;
    Trabajador[] lista = new Trabajador[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_ver_trabajadores);

        // CONFIGURAR LA LISTA CON LOS DATOS DE LA BASE DE DATOS
        recyclerTrabajadores = findViewById(R.id.recyclerTrabajadores);

        // CONSULTA PARA OBTENER LOS USUARIOS QUE SEAN TRABAJADORES
        Query sentencia = FirebaseFirestore.getInstance().collection("usuarios")
                .whereEqualTo("rol", "trabajador")
                .orderBy("nombre", Query.Direction.ASCENDING);

        // OBTENER LOS USUARIOS SEGUN LA CONSULTA Y HACER QUE SEAN DEL TIPO TRABAJADOR
        FirestoreRecyclerOptions<Trabajador> lista = new FirestoreRecyclerOptions.Builder<Trabajador>()
                .setQuery(sentencia, Trabajador.class).build();

        adaptadorVerTrabajadores = new AdaptadorVerTrabajadores(lista);

        recyclerTrabajadores.setHasFixedSize(true);
        recyclerTrabajadores.setLayoutManager(new LinearLayoutManager(this));
        recyclerTrabajadores.setAdapter(adaptadorVerTrabajadores);

        botonAltaTrabajador = findViewById(R.id.btnAltaTrabajador);

        buscador = findViewById(R.id.etBuscadorTrabajadoresVerTrabajadores);
        buscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // FILTRAR ELEMENTOS DE LA LISTA SEGUN EL TEXTO DEL BUSCADOR
                actualizarSentencia(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void actualizarSentencia(String texto) {
        if (adaptadorVerTrabajadores != null) {
            adaptadorVerTrabajadores.stopListening(); // DETENER EL LISTENER
        }

        Query sentencia = FirebaseFirestore.getInstance()
                .collection("usuarios")
                .whereEqualTo("rol", "trabajador")
                .orderBy("nombre")
                .startAt(texto)
                .endAt(texto + "\uf8ff");

        FirestoreRecyclerOptions<Trabajador> nuevasOpciones = new FirestoreRecyclerOptions.Builder<Trabajador>()
                .setQuery(sentencia, Trabajador.class)
                .build();

        adaptadorVerTrabajadores = new AdaptadorVerTrabajadores(nuevasOpciones);
        recyclerTrabajadores.setAdapter(adaptadorVerTrabajadores);
        adaptadorVerTrabajadores.startListening(); // INICIAR EL ESCUCHADOR CON EL NUEVO ADAPTADOR
    }

    // CLICK DEL BOTON PARA HACER EL FORMULARIO DE ALTA TRABAJADOR
    public void clickBotonAltaTrabajador(View view) {
        Intent intent = new Intent(this, AltaModificacionTrabajador.class);
        intent.setAction("NUEVO");
        startActivity(intent);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int posicion = item.getItemId();

        if(posicion == 100) {

            // SE QUIERE EDITAR LA INFORMACION DEL TRABAJADOR
            Intent intent = new Intent(this, AltaModificacionTrabajador.class);
            intent.putExtra("trabajador", adaptadorVerTrabajadores.obtenerTrabajador(posicion));
            intent.setAction("EDITAR");
            startActivity(intent);

        } else {

            // SE QUIERE ELIMINAR AL TRABAJADOR
            AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
            dialogo.setTitle("¿Estás seguro de eliminar a " + adaptadorVerTrabajadores.obtenerTrabajador(posicion).getNombre() + "?");

            // BOTON PARA QUE ELIMINE AL TRABAJADOR DE LA BASE DE DATOS
            dialogo.setNegativeButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // OBTENER EL DOCUMENTO A ELIMINAR
                    DocumentSnapshot snapshot = adaptadorVerTrabajadores.obtenerSnapshot(posicion);
                    snapshot.getReference().delete()
                            .addOnSuccessListener(aVoid -> {
                                mostrarMensajes(getApplicationContext(), 0, "Trabajador eliminado con éxito");
                            })
                            .addOnFailureListener(e -> {
                                mostrarMensajes(getApplicationContext(), 0, "Error al eliminar trabajador");
                            });

                    // Cerrar el diálogo
                    dialog.dismiss();
                }
            });

            // BOTON PARA QUE CIERRE EL DIALOGO
            dialogo.setPositiveButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            // MOSTRAR EL DIALOGO
            dialogo.show();
        }
        return super.onContextItemSelected(item);
    }

    // GESTION DE RECURSOS
    @Override
    protected void onResume() {
        super.onResume();
        if (adaptadorVerTrabajadores != null) {
            adaptadorVerTrabajadores.startListening();
        }
    }

    // EMPEZAR A ESCUCHAR LOS CAMBIOS DE LA BASE DE DATOS
    @Override
    protected void onStart() {
        super.onStart();
        if (adaptadorVerTrabajadores != null) {
            adaptadorVerTrabajadores.startListening();
        }
    }

    // DEJAR DE ESCUCHAR LOS CAMBIOS DE LA BASE DE DATOS
    @Override
    protected void onStop() {
        super.onStop();
        if (adaptadorVerTrabajadores != null) {
            adaptadorVerTrabajadores.stopListening();
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
