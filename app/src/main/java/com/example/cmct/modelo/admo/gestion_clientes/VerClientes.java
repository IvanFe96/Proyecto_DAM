package com.example.cmct.modelo.admo.gestion_clientes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;
import com.example.cmct.clases.Cliente;
import com.example.cmct.modelo.admo.adaptadores.AdaptadorVerClientes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VerClientes extends AppCompatActivity {
    AdaptadorVerClientes adaptadorVerClientes;
    EditText buscadorNombre;
    Spinner buscadorCiudades;
    RecyclerView recyclerClientes;
    Cliente[] lista = new Cliente[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_ver_clientes);

        /*for (int i = 0; i < lista.length; i++) {
            Cliente cliente = new Cliente("Cliente"+i,"Apellido1","Apellido2"
                    ,i+""+i+""+i+""+i+""+i+""+i+""+i+""+i+""+i,i+""+i+""+i+""+i+""+i+""+i+""+i+""+i+""+i,"cliente"+i+"@gmail.com", ""
                    ,"San Mateo de Gállego","C/Los Cliente"+i+" "+i,"8:40","9:20",null, null);
            lista[i] = cliente;
        }*/

        recyclerClientes = findViewById(R.id.recyclerClientes);
        adaptadorVerClientes = new AdaptadorVerClientes(lista);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerClientes.setLayoutManager(linearLayoutManager);
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
                // OBTENER LOS FILTROS
                String filtroNombre = s.toString().toLowerCase();
                String filtroCiudad = buscadorCiudades.getSelectedItem().toString();
                // ACTUALIZAR LA LISTA CON LOS ELEMENTOS FILTRADOS
                adaptadorVerClientes.actualizarDatos(filtrarClientes(lista, filtroNombre, filtroCiudad));
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
                // OBTENER LOS FILTROS
                String filtroNombre = buscadorNombre.getText().toString().toLowerCase();
                String filtroCiudad = parent.getItemAtPosition(position).toString();
                // ACTUALIZAR LA LISTA CON LOS ELEMENTOS FILTRADOS
                adaptadorVerClientes.actualizarDatos(filtrarClientes(lista, filtroNombre, filtroCiudad));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private Cliente[] filtrarClientes(Cliente[] lista, String filtroNombre, String filtroCiudad) {
        // CREAR UNA LISTA PARA ALMACENAR LOS ELEMENTOS FILTRADOS
        List<Cliente> listaFiltrada = new ArrayList<>();

        // RECORRER LA MATRIZ ORIGINAL Y AGREGAR A LA LISTA FILTRADA LOS ELEMENTOS QUE COINCIDAN CON LOS FILTROS
        for (Cliente cliente : lista) {
            boolean nombreCoincide = cliente.getNombre().toLowerCase().contains(filtroNombre);
            boolean ciudadCoincide = cliente.getCiudad().toLowerCase().contains(filtroCiudad.toLowerCase());

            // FILTRAR SOLO POR NOMBRE SI EN EL SPINNER ESTÁ SELECCIONADA LA OPCIÓN "Sin filtro"
            if (filtroCiudad.equals("Todas localidades") && nombreCoincide) {
                listaFiltrada.add(cliente);
            }
            // FILTRAR SOLO POR CIUDAD SI EN EL EDITTEXT NO HAY NADA ESCRITO
            else if (filtroNombre.isEmpty() && ciudadCoincide) {
                listaFiltrada.add(cliente);
            }
            // FILTRAR POR NOMBRE Y CIUDAD A LA VEZ SI SE HA SELECCIONADO UNA CIUDAD EN EL SPINNER Y HAY ALGO ESCRITO EN EL EDITTEXT
            else if (!filtroCiudad.equals("Todas localidades") && !filtroNombre.isEmpty() && nombreCoincide && ciudadCoincide) {
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
