package com.example.cmct.modelo.admo.gestion_clientes;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;
import com.example.cmct.clases.Administrador;
import com.example.cmct.clases.Cliente;
import com.example.cmct.clases.Trabajador;
import com.example.cmct.modelo.admo.adaptadores.AdaptadorVerClientes;
import com.example.cmct.modelo.admo.adaptadores.AdaptadorVerTrabajadores;
import com.example.cmct.modelo.admo.gestion_trabajadores.ModificacionTrabajador;
import com.example.cmct.modelo.admo.gestion_trabajadores.VerTrabajadores;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VerClientes extends AppCompatActivity {
    AdaptadorVerClientes adaptadorVerClientes;
    EditText buscadorNombre;
    Spinner buscadorCiudades;
    RecyclerView recyclerClientes;
    Administrador administrador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_ver_clientes);

        // OBTENER EL ADMINISTRADOR
        obtenerAdministrador();

        recyclerClientes = findViewById(R.id.recyclerClientes);

        // CONSULTA PARA OBTENER LOS USUARIOS QUE SEAN TRABAJADORES
        Query sentencia = FirebaseFirestore.getInstance().collection("usuarios")
                .whereEqualTo("rol", "cliente")
                .orderBy("nombre", Query.Direction.ASCENDING);

        // OBTENER LOS USUARIOS SEGUN LA CONSULTA Y HACER QUE SEAN DEL TIPO TRABAJADOR
        FirestoreRecyclerOptions<Cliente> lista = new FirestoreRecyclerOptions.Builder<Cliente>()
                .setQuery(sentencia, Cliente.class).build();

        // CONFIGURAR LA LISTA CON LOS DATOS DE LA BASE DE DATOS
        adaptadorVerClientes = new AdaptadorVerClientes(lista);

        recyclerClientes.setHasFixedSize(true);
        recyclerClientes.setLayoutManager(new LinearLayoutManager(this));
        recyclerClientes.setAdapter(adaptadorVerClientes);

        // AÑADIR UN LISTENER AL BUSCADOR POR NOMBRE
        buscadorNombre = findViewById(R.id.etBuscadorClientesVerClientes);
        buscadorNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            // CUANDO SE ESCRIBA EN EL BUSCADOR SE ACTUALIZARA LA LISTA CON EL FILTRO QUE SE ESCRIBA
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // FILTRAR ELEMENTOS DE LA LISTA
                actualizarSentencia(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        buscadorCiudades = findViewById(R.id.spinnerCiudadesVerClientes);
        // RELLENAR EL DESPLEGABLE DE CIUDADES
        List<String> opcionesCiudad = Arrays.asList("Todas localidades", "Zuera", "San Mateo de Gállego", "Villanueva de Gállego", "Ontinar del Salz");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesCiudad);
        // ESPECIFICAR EL DISEÑO QUE SE UTILIZARA CUANDO SE MUESTREN LAS OPCIONES
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // ESTABLECER EL ADAPTADOR AL DESPLEGABLE
        buscadorCiudades.setAdapter(adapter);

        // AÑADIR UN LISTENER AL BUSCADOR POR CIUDAD
        buscadorCiudades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // FILTRAR ELEMENTOS DE LA LISTA
                actualizarSentencia(buscadorNombre.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    // GESTION DE RECURSOS
    @Override
    protected void onResume() {
        super.onResume();
        if (adaptadorVerClientes != null) {
            adaptadorVerClientes.notifyDataSetChanged();
        }
    }

    // EMPEZAR A ESCUCHAR LOS CAMBIOS DE LA BASE DE DATOS
    @Override
    protected void onStart() {
        super.onStart();
        if (adaptadorVerClientes != null) {
            adaptadorVerClientes.startListening();
        }
    }

    // DEJAR DE ESCUCHAR LOS CAMBIOS DE LA BASE DE DATOS
    @Override
    protected void onStop() {
        super.onStop();
        if (adaptadorVerClientes != null) {
            adaptadorVerClientes.stopListening();
        }
    }

    private void actualizarSentencia(String texto) {
        if (adaptadorVerClientes != null) {
            adaptadorVerClientes.stopListening(); // DETENER EL LISTENER
        }

        // COMPROBAR SI SE HA SELECCIONADO ALGUNA LOCALIDAD EN EL DESPLEGABLE PARA TAMBIEN FILTRAR POR LOCALIDAD
        Query sentencia = null;

        // FILTRAR SOLO POR TEXTO
        if(buscadorCiudades.getSelectedItem().toString().equals("Todas localidades") && !texto.isEmpty()) {
            sentencia = FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .whereEqualTo("rol", "cliente")
                    .orderBy("nombre")
                    .startAt(texto)
                    .endAt(texto + "\uf8ff");
        } else if(!buscadorCiudades.getSelectedItem().toString().equals("Todas localidades") && !texto.isEmpty()){
            // FILTRAR POR LOCALIDAD Y TEXTO
            sentencia = FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .whereEqualTo("rol", "cliente")
                    .whereEqualTo("localidad",buscadorCiudades.getSelectedItem().toString())
                    .orderBy("nombre")
                    .startAt(texto)
                    .endAt(texto + "\uf8ff");
        } else if(texto.isEmpty()){
            // FILTRAR SOLO POR LOCALIDAD
            // FILTRAR POR TODAS LAS LOCALIDADES
            if(buscadorCiudades.getSelectedItem().toString().equals("Todas localidades")) {
                sentencia = FirebaseFirestore.getInstance()
                        .collection("usuarios")
                        .whereEqualTo("rol", "cliente")
                        .orderBy("nombre");
            } else {
                // FILTRAR POR UNA LOCALIDAD ESPECIFICA
                sentencia = FirebaseFirestore.getInstance()
                        .collection("usuarios")
                        .whereEqualTo("rol", "cliente")
                        .whereEqualTo("localidad", buscadorCiudades.getSelectedItem().toString())
                        .orderBy("nombre");
            }
        }

        FirestoreRecyclerOptions<Cliente> nuevaLista = new FirestoreRecyclerOptions.Builder<Cliente>()
                .setQuery(sentencia, Cliente.class)
                .build();

        adaptadorVerClientes = new AdaptadorVerClientes(nuevaLista);
        recyclerClientes.setAdapter(adaptadorVerClientes);
        adaptadorVerClientes.startListening(); // INICIAR EL ESCUCHADOR CON EL NUEVO ADAPTADOR
    }

    // CLICK DEL BOTON PARA HACER EL FORMULARIO DE ALTA CLIENTE
    public void clickBotonAltaCliente(View view) {
        Intent intent = new Intent(this, AltaCliente.class);
        intent.setAction("NUEVO");
        startActivity(intent);
    }

    @Override
    public boolean onContextItemSelected( MenuItem item) {
        int posicion = item.getGroupId();

        if(item.getItemId() == 100) {
            // SE QUIERE EDITAR LA INFORMACION DEL CLIENTE
            // OBTENER EL ID DEL USUARIO EN LA BASE DE DATOS
            String idUsuario = adaptadorVerClientes.obtenerSnapshot(posicion).getId();

            // INICIAR EL INTENT Y PASAR EL ID DEL CLIENTE
            Intent intent = new Intent(this, ModificacionCliente.class);
            intent.putExtra("idusuario", idUsuario);
            intent.setAction("EDITAR");
            startActivity(intent);

        } else {
            // OBTENER EL DOCUMENTO A ELIMINAR
            DocumentSnapshot snapshot = adaptadorVerClientes.obtenerSnapshot(posicion);
            Cliente cliente = snapshot.toObject(Cliente.class);

            // SE QUIERE ELIMINAR AL CLIENTE
            AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
            dialogo.setTitle("¿Estás seguro de eliminar a " + cliente.getNombre() + "?");

            // BOTON PARA QUE ELIMINE AL CLIENTE DE LA BASE DE DATOS
            dialogo.setNegativeButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // DAR DE BAJA AL CLIENTE
                    administrador.bajaClienteAutenticacion(snapshot, VerClientes.this);

                    // ACTUALIZAR LA LISTA
                    onResume();

                    // CERRAR EL DIALOGO
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

    private void obtenerAdministrador() {
        FirebaseFirestore.getInstance().collection("usuarios")
                .whereEqualTo("rol", "administrador")
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // OBTENER EL USUARIO ADMINISTRADOR
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        administrador = documentSnapshot.toObject(Administrador.class);
                    } else {
                        mostrarMensajes(getApplicationContext(), 1, "No se encontraron administradores");
                    }
                })
                .addOnFailureListener(e -> {
                    mostrarMensajes(getApplicationContext(), 1, "Error al buscar datos de administrador");
                });
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
