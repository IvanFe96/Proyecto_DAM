package com.example.cmct.modelo.admo.adaptadores;


import android.database.Cursor;
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
import com.example.cmct.clases.Trabajador;

public class AdaptadorVerTrabajadores extends RecyclerView.Adapter<AdaptadorVerTrabajadores.DatosHolder>{
    Cursor c;
    //Adaptador(Cursor c) {this.c = c;}
    Trabajador[] lista;
    public AdaptadorVerTrabajadores(Trabajador[] lista) {this.lista = lista;}

    @NonNull
    @Override
    public DatosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflador = LayoutInflater.from(parent.getContext());
        View v = inflador.inflate(R.layout.ver_datos_trabajador_layout, parent, false);
        return new DatosHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DatosHolder holder, int position) {
        //if (!c.moveToPosition(position)) {return;}

        /*Bitmap imagen = c.get;*/

        String nombre = lista[position].getNombre();
        String dni = lista[position].getDni();
        String telefono = lista[position].getTelefono();
        String correo = lista[position].getCorreo();

        // Añadir informacion al Item del recycler.
        holder.imagen.setImageResource(R.drawable.ic_launcher_foreground);
        holder.nombre.setText(nombre);
        holder.telefono.setText(telefono);
        holder.correo.setText(correo);
        holder.dni.setText(dni);
    }

    @Override
    public int getItemCount() {
        //return c.getCount();
        return lista.length;
    }

    public void swapCursor(Cursor newCursor) {
        if (c != null) {
            c.close();
        }
        c = newCursor;
        notifyDataSetChanged();
    }

    // ACTUALIZAR LA LISTA CON EL NUEVO FILTRO
    public void actualizarDatos(Trabajador[] nuevosDatos) {
        lista = nuevosDatos;
        notifyDataSetChanged();
    }

    //CLASE CON EL CONTENEDOR.
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

            // ESTABLECER MENU CONTEXTUAL AL ITEM DEL RECYCLERVIEW
            itemView.setOnCreateContextMenuListener(this);

        }

        //MENU CONTEXTUAL PARA RECYCLER
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.add(getAdapterPosition(), 100, 0, "EDITAR");
            menu.add(getAdapterPosition(), 200, 0, "ELIMINAR");

        }

    }
}
