package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;
import com.example.cmct.clases.Utilidades;
import com.example.cmct.clases.Valoracion;
import com.example.cmct.modelo.admo.adaptadores.AdaptadorValoracion;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VerValoraciones extends AppCompatActivity {
    AdaptadorValoracion adaptadorValoraciones;
    RecyclerView recyclerValoraciones;

    // OBTENER LA INSTANCIA DE LA BASE DE DATOS DE FIREBASE
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_ver_valoraciones);

        recyclerValoraciones = findViewById(R.id.recyclerValoraciones);
        inicializarAdaptador();

        buscarTrabajadoresValorados();
    }

    // METODO PARA INICIALIZAR EL ADAPTADOR ANTES DE QUE SE CARGUEN TODOS LOS DATOS
    private void inicializarAdaptador() {
        List<Valoracion> listaVacia = new ArrayList<>(); // Lista inicial vacía
        adaptadorValoraciones = new AdaptadorValoracion(listaVacia);
        recyclerValoraciones.setLayoutManager(new LinearLayoutManager(this));
        recyclerValoraciones.setAdapter(adaptadorValoraciones);
    }

    // METODO PARA BUSCAR LOS TRABAJADORES QUE TIENEN MENOS DE TRES ESTRELLAS DE VALORACION
    private void buscarTrabajadoresValorados() {
        db.collection("valoraciones")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    HashMap<String, List<Float>> valoracionesPorTrabajador = new HashMap<>();
                    HashMap<String, String> nombresPorDNI = new HashMap<>();

                    // ORGANIZAR LAS VALORACIONES PARA CADA TRABAJADOR
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        String dni = snapshot.getString("dni");
                        float valoracion = snapshot.getDouble("calificacion").floatValue();
                        // COMPROBAR SI EL TRABAJADOR NO ESTA EN LA LISTA PARA INICIALIZAR UNA NUEVA LISTA
                        if (!valoracionesPorTrabajador.containsKey(dni)) {
                            // AÑADIR UN NUEVO TRABAJADOR
                            valoracionesPorTrabajador.put(dni, new ArrayList<>());
                        }
                        // AÑADIR LA VALORACION A LA LISTA DE VALORACIONES DE ESE TRABAJADOR
                        valoracionesPorTrabajador.get(dni).add(valoracion);

                        // BUSCAR EL NOMBRE DEL TRABAJADOR PARA DESPUES PONERLO EN LA LISTA
                        nombresPorDNI.put(dni, snapshot.getString("nombreTrabajador"));
                    }

                    List<Valoracion> listaValoraciones = new ArrayList<>();
                    // CALCULAR LA MEDIA DE TODAS LAS CALIFICACIONES DEL TRABAJADOR
                    for (String dni : valoracionesPorTrabajador.keySet()) {
                        List<Float> valoraciones = valoracionesPorTrabajador.get(dni);
                        // SUMAR TODAS LAS CALIFICACIONES
                        float suma = 0;
                        for (Float calificacion : valoraciones) {
                            suma += calificacion;
                        }
                        // OBTENER LA MEDIA DE LAS CALIFICACIONES TOTALES DEL TRABAJADOR
                        float media = suma / valoraciones.size();
                        // COMPROBAR SI LA MEDIA ES MENOR DE TRES PARA MOSTRAR AL TRABAJADOR EN LA LISTA
                        if (media < 3.0) {
                            listaValoraciones.add(new Valoracion(nombresPorDNI.get(dni), nombresPorDNI.get(dni), media, null));
                        }
                    }
                    // ACTUALIZAR LA LISTA CON LOS DATOS OBTENIDOS DE LA BASE DE DATOS
                    adaptadorValoraciones.actualizarLista(listaValoraciones);
                })
                .addOnFailureListener(e -> {
                    Utilidades.mostrarMensajes(this,1,"Error al obtener las valoraciones");
                });
    }
}
