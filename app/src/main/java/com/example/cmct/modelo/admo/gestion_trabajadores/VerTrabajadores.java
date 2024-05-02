package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;
import com.example.cmct.modelo.admo.adaptadores.AdaptadorVerTrabajadores;

public class VerTrabajadores extends AppCompatActivity {
    AdaptadorVerTrabajadores adaptadorVerTrabajadores;
    RecyclerView recyclerTrabajadores;
    Button botonAltaTrabajador;
    String[][] lista = new String[4][4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_ver_trabajadores);

        for (int i = 0; i < lista.length; i++) {
            lista[i][0] = "Trabajador"+i;
            lista[i][1] = i+""+i+""+i+""+i+""+i+""+i+""+i+""+i+"N";
            lista[i][2] = i+""+i+""+i+""+i+""+i+""+i+""+i+""+i+""+i;
            lista[i][3] = "trabajador"+i+"@gmail.com";
        }

        recyclerTrabajadores = findViewById(R.id.recyclerTrabajadores);
        adaptadorVerTrabajadores = new AdaptadorVerTrabajadores(lista);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerTrabajadores.setLayoutManager(linearLayoutManager);
        recyclerTrabajadores.setAdapter(adaptadorVerTrabajadores);

        botonAltaTrabajador = findViewById(R.id.btnAltaTrabajador);
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
            // EL USUARIO QUIERE EDITAR LA INFORMACION DEL TRABAJADOR
            Intent intent = new Intent(this, AltaModificacionTrabajador.class);
            intent.putExtra("nombre",lista[item.getGroupId()][0]);
            intent.setAction("EDITAR");
            startActivity(intent);
        } else {
            // EL USUARIO QUIERE ELIMINAR AL TRABAJADOR

        }
        return super.onContextItemSelected(item);
    }

}
