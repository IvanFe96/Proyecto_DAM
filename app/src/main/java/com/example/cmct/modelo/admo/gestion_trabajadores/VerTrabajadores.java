package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;
import com.example.cmct.clases.Trabajador;
import com.example.cmct.modelo.admo.adaptadores.AdaptadorVerTrabajadores;

import java.util.ArrayList;
import java.util.List;

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

        for (int i = 0; i < lista.length; i++) {
            Trabajador trabajador = new Trabajador("1","Trabajador"+i,"Apellido1","Apellido2"
                    ,i+""+i+""+i+""+i+""+i+""+i+""+i+""+i+""+i,i+""+i+""+i+""+i+""+i+""+i+""+i+""+i+""+i,"trabajador"+i+"@gmail.com");
            lista[i] = trabajador;
        }

        recyclerTrabajadores = findViewById(R.id.recyclerTrabajadores);
        adaptadorVerTrabajadores = new AdaptadorVerTrabajadores(lista);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerTrabajadores.setLayoutManager(linearLayoutManager);
        recyclerTrabajadores.setAdapter(adaptadorVerTrabajadores);

        botonAltaTrabajador = findViewById(R.id.btnAltaTrabajador);

        buscador = findViewById(R.id.etBuscadorTrabajadoresVerTrabajadores);
        buscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // FILTRAR ELEMENTOS EN LA MATRIZ ORIGINAL SEGUN EL TEXTO DEL BUSCADOR
                String filtro = s.toString().toLowerCase();
                Trabajador[] listaFiltrada = filtrarTrabajadores(lista, filtro);

                // ACTUALIZAR LA LISTA CON LOS ELEMENTOS FILTRADOS
                adaptadorVerTrabajadores.actualizarDatos(listaFiltrada);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private Trabajador[] filtrarTrabajadores(Trabajador[] lista, String filtro) {
        // CREAR UNA LISTA PARA ALMACENAR LOS ELEMENTOS FILTRADOS
        List<Trabajador> listaFiltrada = new ArrayList<>();

        // RECORRER LA MATRIZ ORIGINAL Y AGREGAR A LA LISTA FILTRADA LOS ELEMENTOS QUE COINCIDAN CON EL FILTRO
        for (Trabajador trabajador : lista) {
            if (trabajador.getNombre().toLowerCase().contains(filtro)) {
                listaFiltrada.add(trabajador);
            }
        }

        // CONVERTIR LA LISTA FILTRADA EN UNA MATRIZ
        return listaFiltrada.toArray(new Trabajador[0]);
    }

    // CLICK DEL BOTON PARA HACER EL FORMULARIO DE ALTA TRABAJADOR
    public void clickBotonAltaTrabajador(View view) {
        Intent intent = new Intent(this, AltaModificacionTrabajador.class);
        intent.setAction("NUEVO");
        startActivity(intent);
    }

    @Override
    public boolean onContextItemSelected( MenuItem item) {
        int posicion = item.getItemId();

        if(posicion == 100) {

            // SE QUIERE EDITAR LA INFORMACION DEL TRABAJADOR
            Intent intent = new Intent(this, AltaModificacionTrabajador.class);
            intent.putExtra("trabajador", lista[item.getGroupId()]);
            intent.setAction("EDITAR");
            startActivity(intent);

        } else {

            // SE QUIERE ELIMINAR AL TRABAJADOR
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
