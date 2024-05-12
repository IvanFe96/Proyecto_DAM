package com.example.cmct.modelo.admo.adaptadores;


import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;
import com.example.cmct.clases.Trabajador;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class AdaptadorVerTrabajadores extends FirestoreRecyclerAdapter<Trabajador, AdaptadorVerTrabajadores.DatosHolder>{
    private FirestoreRecyclerOptions<Trabajador> lista;

    public AdaptadorVerTrabajadores(FirestoreRecyclerOptions<Trabajador> options) {
        super(options);
        this.lista = options;
    }

    @NonNull
    @Override
    public DatosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflador = LayoutInflater.from(parent.getContext());
        View v = inflador.inflate(R.layout.ver_datos_trabajador_layout, parent, false);
        return new DatosHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DatosHolder holder, int position, @NonNull Trabajador modelo) {
        // AÑADIR LOS DATOS AL ITEM DEL RECYCLER
        // CARGAR LA IMAGEN DESDE FIREBASE STORAGE CON PICASSO
        if (modelo.getImagen() != null && !modelo.getImagen().isEmpty()) {
            Picasso.get()
                    .load(modelo.getImagen())
                    .placeholder(R.drawable.ic_launcher_foreground) // IMAGEN DE CARGA
                    .error(R.drawable.ic_launcher_foreground) // IMAGEN EN CASO DE ERROR
                    .into(holder.imagen);
        } else {
            holder.imagen.setImageResource(R.drawable.ic_launcher_foreground); // IMAGEN PREDETERMINADA SI NO HAY URL (NO DEBERIA OCURRIR)
        }
        holder.nombre.setText(modelo.getNombre());
        holder.telefono.setText(modelo.getTelefono());
        holder.correo.setText(modelo.getCorreo());
        holder.dni.setText(modelo.getDni());

    }

    //CLASE CON EL CONTENEDOR
    public class DatosHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView nombre, telefono, correo, dni;
        ImageView imagen;
        public DatosHolder(@NonNull View itemView) {
            super(itemView);

            imagen = itemView.findViewById(R.id.imageViewVerTrabajadores);
            nombre = itemView.findViewById(R.id.tvVerTrabajadoresNombreApellidos);
            telefono = itemView.findViewById(R.id.tvVerTrabajadoresTelefono);
            correo = itemView.findViewById(R.id.tvVerTrabajadoresCorreo);
            dni = itemView.findViewById(R.id.tvVerTrabajadoresDni);

            itemView.setOnCreateContextMenuListener(this);
        }

        //MENU CONTEXTUAL PARA RECYCLER
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.add(getAdapterPosition(), 100, 0, "EDITAR");
            menu.add(getAdapterPosition(), 200, 0, "ELIMINAR");

        }

    }

    // Método para obtener un objeto Trabajador basado en la posición
    public Trabajador obtenerTrabajador(int position) {
        return lista.getSnapshots().get(position);
    }

    public DocumentSnapshot obtenerSnapshot(int position) {
        return super.getSnapshots().getSnapshot(position);
    }
}
