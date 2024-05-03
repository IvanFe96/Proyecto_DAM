package com.example.cmct.modelo.admo.gestion_clientes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;
import com.example.cmct.clases.Cliente;
import com.example.cmct.modelo.admo.adaptadores.AdaptadorVerClientes;
import com.example.cmct.modelo.admo.adaptadores.AdaptadorVerTrabajadores;
import com.example.cmct.modelo.admo.gestion_trabajadores.AltaModificacionTrabajador;

import java.util.ArrayList;
import java.util.List;

public class VerClientes extends AppCompatActivity {
    AdaptadorVerClientes adaptadorVerClientes;
    EditText buscador;
    RecyclerView recyclerClientes;
    Cliente[] lista = new Cliente[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_ver_clientes);

        for (int i = 0; i < lista.length; i++) {
            Cliente cliente = new Cliente("1","Cliente"+i,"Apellido1","Apellido2"
                    ,i+""+i+""+i+""+i+""+i+""+i+""+i+""+i+""+i,i+""+i+""+i+""+i+""+i+""+i+""+i+""+i+""+i,"cliente"+i+"@gmail.com"
                    ,"C/Los Cliente"+i+" Nº"+i,"8:40","9:20",null);
            lista[i] = cliente;
        }

        recyclerClientes = findViewById(R.id.recyclerClientes);
        adaptadorVerClientes = new AdaptadorVerClientes(lista);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerClientes.setLayoutManager(linearLayoutManager);
        recyclerClientes.setAdapter(adaptadorVerClientes);

        // AÑADIR UN LISTENER AL BUSCADOR
        buscador = findViewById(R.id.etBuscadorClientesVerClientes);
        buscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            // CUANDO SE ESCRIBA EN EL BUSCADOR SE ACTUALIZARA LA LISTA CON EL FILTRO QUE SE ESCRIBA
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // FILTRAR ELEMENTOS EN LA MATRIZ ORIGINAL SEGUN EL TEXTO DEL BUSCADOR
                String filtro = s.toString().toLowerCase();
                Cliente[] listaFiltrada = filtrarClientes(lista, filtro);

                // ACTUALIZAR LA LISTA CON LOS ELEMENTOS FILTRADOS
                adaptadorVerClientes.actualizarDatos(listaFiltrada);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private Cliente[] filtrarClientes(Cliente[] lista, String filtro) {
        // CREAR UNA LISTA PARA ALMACENAR LOS ELEMENTOS FILTRADOS
        List<Cliente> listaFiltrada = new ArrayList<>();

        // RECORRER LA MATRIZ ORIGINAL Y AGREGAR A LA LISTA FILTRADA LOS ELEMENTOS QUE COINCIDAN CON EL FILTRO
        for (Cliente cliente : lista) {
            if (cliente.getNombre().toLowerCase().contains(filtro)) {
                listaFiltrada.add(cliente);
            }
        }

        // CONVERTIR LA LISTA FILTRADA EN UNA MATRIZ
        return listaFiltrada.toArray(new Cliente[0]);
    }

    // CLICK DEL BOTON PARA HACER EL FORMULARIO DE ALTA TRABAJADOR
    public void clickBotonAltaCliente(View view) {
        Intent intent = new Intent(this, AltaModificacionCliente.class);
        intent.setAction("NUEVO");
        startActivity(intent);
    }

    @Override
    public boolean onContextItemSelected( MenuItem item) {
        int posicion = item.getItemId();

        if(posicion == 100) {

            // SE QUIERE EDITAR LA INFORMACION DEL CLIENTE
            Intent intent = new Intent(this, AltaModificacionCliente.class);
            intent.putExtra("cliente",lista[item.getGroupId()]);
            intent.setAction("EDITAR");
            startActivity(intent);

        } else {

            // SE QUIERE ELIMINAR AL CLIENTE
            AlertDialog.Builder dialogo = new AlertDialog.Builder(this);
            dialogo.setTitle("¿Estás seguro de eliminar a " + lista[item.getGroupId()].getNombre() + "?");

            // BOTON PARA QUE ELIMINE AL TRABAJADOR DE LA BASE DE DATOS
            dialogo.setNegativeButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

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
}
