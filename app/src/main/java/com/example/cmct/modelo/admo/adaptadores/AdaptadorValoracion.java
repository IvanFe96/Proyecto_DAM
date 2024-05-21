package com.example.cmct.modelo.admo.adaptadores;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;
import com.example.cmct.clases.Valoracion;

import java.util.List;


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

