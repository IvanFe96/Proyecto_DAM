package com.example.cmct.modelo.admo.adaptadores;


import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;
import com.example.cmct.modelo.admo.gestion_trabajadores.AsignarTrabajo;

public class AdaptadorTrabajadorSimple extends RecyclerView.Adapter<AdaptadorTrabajadorSimple.DatosHolder>{
    Cursor c;
    //Adaptador(Cursor c) {this.c = c;}
    String[] lista;
    Intent intentpadre;

    public AdaptadorTrabajadorSimple(String[] lista) {this.lista = lista;}

    @NonNull
    @Override
    public DatosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflador = LayoutInflater.from(parent.getContext());
        View v = inflador.inflate(R.layout.ver_nombre_trabajador_layout, parent, false);
        return new DatosHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DatosHolder holder, int position) {
        //if (!c.moveToPosition(position)) {return;}

        /*@SuppressLint("Range") Bitmap imagen = c.get;
        @SuppressLint("Range") String nombre = c.getString(c.getColumnIndex("nombre"));*/

        String nombre = lista[position];

        // Añadir informacion al Item del recycler.
        holder.imagen.setImageResource(R.drawable.ic_launcher_foreground);
        holder.nombre.setText(nombre);
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
                intent.putExtra("nombre", lista[getAdapterPosition()]);
                v.getContext().startActivity(intent);
            } else if (intentpadre.getAction().equals("INCIDENCIAS")) {
                //MOSTRAMOS UN MENU CON LAS INCIDENCIAS DE ESE TRABAJADOR
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.inflate(R.menu.menu_incidencias);

                // AQUI AÑADIREMOS LOS RESULTADOS DE LA CONSULTA DE LA BASE DE DATOS AL MENU
                //popupMenu.getMenu().add("33");

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        //String incidencia = item.getTitle()

                        if(id == R.id.menu_opcion1) {
                            // Lógica para la opción 1
                            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                            builder.setTitle(item.getTitle());
                            builder.setMessage("Esto es una prueba de la descripción de una incidencia.");
                            builder.show();
                        } else if (id == R.id.menu_opcion2) {
                            // Lógica para la opción 2
                        }

                        return false;
                    }
                });
                popupMenu.show();
            }
        }
    }

    // OBTENER EL INTENTO DEL QUE PROCEDE
    public void obtenerIntent(Intent intentPadre) {
        this.intentpadre = intentPadre;
    }
}
