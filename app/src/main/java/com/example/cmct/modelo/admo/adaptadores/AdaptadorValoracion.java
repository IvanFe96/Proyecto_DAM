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
import com.example.cmct.clases.Trabajador;
import com.example.cmct.clases.Valoracion;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.List;

/*public class AdaptadorValoracion extends FirestoreRecyclerAdapter<Valoracion, AdaptadorValoracion.DatosHolder> {
    public AdaptadorValoracion(FirestoreRecyclerOptions<Valoracion> options) {super(options);}

    @NonNull
    @Override
    public DatosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflador = LayoutInflater.from(parent.getContext());
        View v = inflador.inflate(R.layout.ver_calificacion_trabajador_layout, parent, false);
        return new DatosHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DatosHolder holder, int position, @NonNull Valoracion modelo) {
        // AÑADIR TODA LA INFORMACION A CADA ITEM
        holder.nombre.setText(modelo.getNombreTrabajador());
        holder.calificacion.setRating(modelo.getCalificacion());
    }

    //CLASE CON EL CONTENEDOR.
    public class DatosHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        RatingBar calificacion;
        public DatosHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.tvNombreApellidosValoracion);
            calificacion = itemView.findViewById(R.id.ratingBarValoracion);

        }

    }
}*/

public class AdaptadorValoracion extends RecyclerView.Adapter<AdaptadorValoracion.DatosHolder> {
    private List<Valoracion> listaValoraciones;

    public AdaptadorValoracion(List<Valoracion> listaValoraciones) {
        this.listaValoraciones = listaValoraciones;
    }

    @NonNull
    @Override
    public DatosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflador = LayoutInflater.from(parent.getContext());
        View v = inflador.inflate(R.layout.ver_calificacion_trabajador_layout, parent, false);
        return new DatosHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DatosHolder holder, int position) {
        Valoracion modelo = listaValoraciones.get(position);
        holder.nombre.setText(modelo.getNombreTrabajador());
        holder.calificacion.setRating(modelo.getCalificacion());
    }

    public void actualizarLista(List<Valoracion> nuevasValoraciones) {
        listaValoraciones.clear();
        listaValoraciones.addAll(nuevasValoraciones);
        notifyDataSetChanged();  // NOTIFICAR AL ADAPTADOR EL CAMBIO DE DAOS
    }


    @Override
    public int getItemCount() {
        return listaValoraciones.size();
    }

    public class DatosHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        RatingBar calificacion;

        public DatosHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.tvNombreApellidosValoracion);
            calificacion = itemView.findViewById(R.id.ratingBarValoracion);
        }
    }
}

