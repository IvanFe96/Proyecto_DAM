package com.example.cmct.modelo.admo.adaptadores;


import android.database.Cursor;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;

public class AdaptadorValoracion extends RecyclerView.Adapter<AdaptadorValoracion.DatosHolder>{
    Cursor c;
    //Adaptador(Cursor c) {this.c = c;}
    String[][] lista;
    public AdaptadorValoracion(String[][] lista) {this.lista = lista;}

    @NonNull
    @Override
    public DatosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflador = LayoutInflater.from(parent.getContext());
        View v = inflador.inflate(R.layout.ver_calificacion_trabajador_layout, parent, false);
        return new DatosHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DatosHolder holder, int position) {
        //if (!c.moveToPosition(position)) {return;}

        /*Bitmap imagen = c.get;
        String nombre = c.getString(c.getColumnIndex("nombre"));
        float calificacion*/

        String nombre = lista[position][0];
        float nota = Float.parseFloat(lista[position][1]);

        // Añadir informacion al Item del recycler.
        holder.imagen.setImageResource(R.drawable.ic_launcher_foreground);
        holder.nombre.setText(nombre);
        holder.calificacion.setRating(nota);
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
        TextView nombre;
        ImageView imagen;
        RatingBar calificacion;
        public DatosHolder(@NonNull View itemView) {
            super(itemView);

            imagen = itemView.findViewById(R.id.imageViewValoracion);
            nombre = itemView.findViewById(R.id.tvNombreApellidosValoracion);
            calificacion = itemView.findViewById(R.id.ratingBarValoracion);

            //ESTABLECER MENU CONTEXTUAL AL ITEM DEL RECYCLERVIEW
            itemView.setOnCreateContextMenuListener(this);

        }

        //MENU CONTEXTUAL PARA RECYCLER
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            menu.add(getAdapterPosition(), 100, 0, "TOMAR DECISION");

        }
    }
}
