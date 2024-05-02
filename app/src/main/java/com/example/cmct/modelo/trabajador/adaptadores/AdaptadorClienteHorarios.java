package com.example.cmct.modelo.trabajador.adaptadores;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;
import com.example.cmct.modelo.trabajador.DatosCliente;

import java.time.LocalTime;

public class AdaptadorClienteHorarios extends RecyclerView.Adapter<AdaptadorClienteHorarios.DatosHolder>{
    Cursor c;
    //Adaptador(Cursor c) {this.c = c;}
    String[] lista;

    public AdaptadorClienteHorarios(String[] lista) {this.lista = lista;}

    @NonNull
    @Override
    public DatosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflador = LayoutInflater.from(parent.getContext());
        View v = inflador.inflate(R.layout.ver_nombre_horas_cliente_layout, parent, false);
        return new DatosHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DatosHolder holder, int position) {
        //if (!c.moveToPosition(position)) {return;}

        /*@SuppressLint("Range") Bitmap imagen = c.get;
        @SuppressLint("Range") String nombre = c.getString(c.getColumnIndex("nombre"));*/

        String nombre = lista[position];

        // Añadir informacion al Item del recycler.
        holder.nombre.setText(nombre);

        // Obtener la hora actual
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalTime horaActual = LocalTime.now();
            holder.horario.setText(horaActual.getHour() + ":" + horaActual.getMinute() + " - " + horaActual.getHour() + ":" + horaActual.getMinute());
        }

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
        TextView nombre, horario;
        public DatosHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.tvNombreApellidosTrabajador);
            horario = itemView.findViewById(R.id.tvHorario);

            //ESTABLECER ON CLICK LISTENER AL ITEM DEL RECYCLERVIEW
            itemView.setOnClickListener(this);

        }

        // CLICK EN EL CLIENTE
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), DatosCliente.class);
            v.getContext().startActivity(intent);
        }
    }

}
