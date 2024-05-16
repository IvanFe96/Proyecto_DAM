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
import com.example.cmct.clases.Cliente;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class AdaptadorVerClientes extends FirestoreRecyclerAdapter<Cliente, AdaptadorVerClientes.DatosHolder> {
    public AdaptadorVerClientes(FirestoreRecyclerOptions<Cliente> options) {
        super(options);
    }

    @NonNull
    @Override
    public DatosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflador = LayoutInflater.from(parent.getContext());
        View v = inflador.inflate(R.layout.ver_datos_cliente_layout, parent, false);
        return new DatosHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DatosHolder holder, int position, @NonNull Cliente modelo) {
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
        holder.telefono.setText(modelo.getTelefono());
        holder.correo.setText(modelo.getCorreo());
        holder.direccion.setText(modelo.getDireccion() + "," + modelo.getLocalidad());
    }

    // CLASE CON EL CONTENEDOR
    public class DatosHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView nombre, telefono, correo, direccion;
        ImageView imagen;
        public DatosHolder(@NonNull View itemView) {
            super(itemView);

            imagen = itemView.findViewById(R.id.imageViewVerClientes);
            nombre = itemView.findViewById(R.id.tvVerClientesNombreApellidos);
            telefono = itemView.findViewById(R.id.tvVerClientesTelefono);
            correo = itemView.findViewById(R.id.tvVerClientesCorreo);
            direccion = itemView.findViewById(R.id.tvVerClientesDireccion);

            // ESTABLECER MENU CONTEXTUAL AL ITEM DEL RECYCLERVIEW
            itemView.setOnCreateContextMenuListener(this);

        }

        // MENU CONTEXTUAL PARA RECYCLER
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.add(getAdapterPosition(), 100, 0, "EDITAR");
            menu.add(getAdapterPosition(), 200, 0, "ELIMINAR");

        }

    }

    // METODO PARA OBTENER EL CLIENTE DE LA BASE DE DATOS
    public DocumentSnapshot obtenerSnapshot(int position) {
        return super.getSnapshots().getSnapshot(position);
    }
}
