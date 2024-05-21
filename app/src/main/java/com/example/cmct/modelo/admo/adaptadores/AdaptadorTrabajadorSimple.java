package com.example.cmct.modelo.admo.adaptadores;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;
import com.example.cmct.clases.Incidencia;
import com.example.cmct.clases.Trabajador;
import com.example.cmct.clases.Utilidades;
import com.example.cmct.modelo.admo.gestion_trabajadores.AsignarTrabajo;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class AdaptadorTrabajadorSimple extends FirestoreRecyclerAdapter<Trabajador, AdaptadorTrabajadorSimple.DatosHolder> {
    Intent intentpadre;
    Activity actividadPadre;

    public AdaptadorTrabajadorSimple(FirestoreRecyclerOptions<Trabajador> options) {
        super(options);
    }

    @NonNull
    @Override
    public DatosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflador = LayoutInflater.from(parent.getContext());
        View v = inflador.inflate(R.layout.ver_nombre_trabajador_layout, parent, false);
        return new DatosHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DatosHolder holder, int position, @NonNull Trabajador modelo) {
        // AÑADIR LOS DATOS AL ITEM DEL RECYCLER
        // CARGAR LA IMAGEN DESDE FIREBASE STORAGE CON PICASSO
        if (modelo.getImagen() != null && !modelo.getImagen().isEmpty()) {
            Picasso.get()
                    .load(modelo.getImagen())
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.ic_launcher_foreground) // IMAGEN DE CARGA
                    .error(R.drawable.ic_launcher_foreground) // IMAGEN EN CASO DE ERROR
                    .into(holder.imagen);
        } else {
            holder.imagen.setImageResource(R.drawable.ic_launcher_foreground); // IMAGEN PREDETERMINADA SI NO HAY URL (NO DEBERIA OCURRIR)
        }
        holder.nombre.setText(modelo.getNombre());

    }

    // ACTUALIZAR LOS DATOS DE LA LISTA
    public void updateOptions(FirestoreRecyclerOptions<Trabajador> newOptions) {
        super.updateOptions(newOptions);
        this.notifyDataSetChanged();
    }


    //CLASE CON EL CONTENEDOR.
    public class DatosHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nombre;
        ImageView imagen;
        public DatosHolder(@NonNull View itemView) {
            super(itemView);

            imagen = itemView.findViewById(R.id.imageViewTrabajador);
            nombre = itemView.findViewById(R.id.tvNombreApellidosTrabajador);

            //ESTABLECER ON CLICK LISTENER AL ITEM DEL RECYCLERVIEW
            itemView.setOnClickListener(this);

        }

        // CLICK EN EL TRABAJADOR
        @Override
        public void onClick(View v) {
            // COMPROBAMOS SI ES LA VENTANA DE ASIGNAR TRABAJO O LA DE INCIDENCIAS
            if(intentpadre.getAction().equals("ASIGNARTRABAJO")) {
                // VAMOS A LA SIGUIENTE PANTALLA PARA ASIGNAR NUEVO TRABAJO AL TRABAJADOR
                Intent intent = new Intent(v.getContext(), AsignarTrabajo.class);
                intent.setAction("NUEVO");
                intent.putExtra("trabajador", getSnapshots().getSnapshot(getAdapterPosition()).toObject(Trabajador.class).getDni());
                v.getContext().startActivity(intent);
            } else if (intentpadre.getAction().equals("INCIDENCIAS")) {
                // DECLARAR UNA LISTA PARA GUARDAR LOS DATOS DE LAS INCIDENCIAS
                ArrayList<Incidencia> incidencias = new ArrayList<Incidencia>();

                // OBTENER LAS INCIDENCIAS DEL TRABAJADOR AL QUE SE LE HA HECHO CLICK
                FirebaseFirestore.getInstance().collection("incidencias")
                        .whereEqualTo("dni", getSnapshots().getSnapshot(getAdapterPosition()).get("dni"))
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                // OBTENER LOS DATOS QUE SE VAN A MOSTRAR EN EL DIALOGO
                                incidencias.add(new Incidencia(document.getString("dni"), document.getString("tipo"), document.getString("descripcion"), (Timestamp) document.get("fechaIncidencia")));

                                // AGREGAR CADA INCIDENCIA COMO UNA OPCION EN EL MENU
                                popupMenu.getMenu().add(document.getString("tipo"));
                            }

                            popupMenu.setOnMenuItemClickListener(item -> {
                                // MOSTRAR UN DIALOGO CUANDO SE SELECCIONE UNA INCIDENCIA
                                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                builder.setTitle(incidencias.get(item.getItemId()).getTipo());
                                builder.setMessage(incidencias.get(item.getItemId()).getDescripcion());
                                builder.show();
                                return true;
                            });
                            popupMenu.show();
                        })
                        .addOnFailureListener(e -> {
                            Utilidades.mostrarMensajes(actividadPadre,1,"Error al cargar las incidencias");
                        });
            }
        }
    }

    // OBTENER EL INTENTO DEL QUE PROCEDE
    public void obtenerIntent(Intent intentPadre) {
        this.intentpadre = intentPadre;
    }

    // OBTENER LA ACTIVIDAD DE LA QUE PROCEDE
    public void obtenerActividad(Activity actividad) {
        this.actividadPadre = actividad;
    }

}
