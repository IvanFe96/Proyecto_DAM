package com.example.cmct.modelo;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;

public class Adaptador extends RecyclerView.Adapter<Adaptador.DatosHolder>{
    Cursor c;
    //Adaptador(Cursor c) {this.c = c;}
    String[][] lista;
    public Adaptador(String[][] lista) {this.lista = lista;}

    @NonNull
    @Override
    public DatosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflador = LayoutInflater.from(parent.getContext());
        View v = inflador.inflate(R.layout.cliente_layout, parent, false);
        return new DatosHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DatosHolder holder, int position) {
        //if (!c.moveToPosition(position)) {return;}

        /*@SuppressLint("Range") Bitmap imagen = c.get;
        @SuppressLint("Range") String nombre = c.getString(c.getColumnIndex("nombre"));
        @SuppressLint("Range") String telefono = c.getString(c.getColumnIndex("cordillera"));
        @SuppressLint("Range") String correo = c.getString(c.getColumnIndex("n_remontes"));
        @SuppressLint("Range") String direccion = c.getString(c.getColumnIndex("fecha_ult_visita"));*/

        String nombre = lista[position][0];
        String telefono = lista[position][1];
        String correo = lista[position][2];
        String direccion = lista[position][3];

        // Añadir informacion al Item del recycler.
        holder.imagen.setImageResource(R.drawable.ic_launcher_foreground);
        holder.nombre.setText(nombre);
        holder.telefono.setText(telefono);
        holder.correo.setText(correo);
        holder.direccion.setText(direccion);
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

    //CLASE CON EL CONTENEDOR.
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

            //ESTABLECER MENU CONTEXTUAL AL ITEM DEL RECYCLERVIEW
            itemView.setOnCreateContextMenuListener(this);

        }

        //MENU CONTEXTUAL PARA RECYCLER
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(getAdapterPosition(), 100, 0, "ELIMINAR");
        }

    }
}
