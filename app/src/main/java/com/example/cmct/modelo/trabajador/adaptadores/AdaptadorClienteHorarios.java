package com.example.cmct.modelo.trabajador.adaptadores;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cmct.R;
import com.example.cmct.clases.Cliente;
import com.example.cmct.modelo.trabajador.DatosCliente;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.time.LocalTime;

public class AdaptadorClienteHorarios extends FirestoreRecyclerAdapter<Cliente, AdaptadorClienteHorarios.DatosHolder> {

    private final Activity actividadPadre;

    public AdaptadorClienteHorarios(FirestoreRecyclerOptions<Cliente> options, Activity actividadPadre) {
        super(options);
        this.actividadPadre = actividadPadre;
    }

    @NonNull
    @Override
    public DatosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflador = LayoutInflater.from(parent.getContext());
        View v = inflador.inflate(R.layout.ver_nombre_horas_cliente_layout, parent, false);
        return new DatosHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DatosHolder holder, int position, @NonNull Cliente modelo) {
        Log.d("Adapter", "Binding: " + modelo.getNombre());
        // AÑADIR INFORMACION AL ITEM DEL RecyclerView
        holder.nombre.setText(modelo.getNombre()+" "+modelo.getApellido1()+" "+modelo.getApellido2());
        holder.horario.setText(modelo.getHoraEntradaTrabajador()+"-"+modelo.getHoraSalidaTrabajador());

    }

    @Override
    public void onDataChanged() {
        if (getItemCount() == 0) {
            LayoutInflater inflater = actividadPadre.getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_personalizado, null);

            TextView text = (TextView) layout.findViewById(R.id.toast_text);
            text.setText("Todavía no tienes horario asignado"); // CONFIGURAR EL MENSAJE PERSONALIZADO

            Toast toast = new Toast(actividadPadre.getApplicationContext());
            toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 300);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
            actividadPadre.finish();
        } else {
            this.notifyDataSetChanged();
        }
    }

    //CLASE CON EL CONTENEDOR
    public class DatosHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nombre, horario;
        public DatosHolder(@NonNull View itemView) {
            super(itemView);

            nombre = itemView.findViewById(R.id.tvNombreApellidosCliente);
            horario = itemView.findViewById(R.id.tvHorarioVerHorario);

            //ESTABLECER ON CLICK LISTENER AL ITEM DEL RECYCLERVIEW
            itemView.setOnClickListener(this);

        }

        // CLICK EN EL CLIENTE PARA ABRIR UNA NUEVA PANTALLA CON TODOS LOS DATOS DEL CLIENTE
        @Override
        public void onClick(View v) {
            Intent intento = new Intent(v.getContext(), DatosCliente.class);
            intento.putExtra("dniCliente",getSnapshots().getSnapshot(getAdapterPosition()).toObject(Cliente.class).getDni());
            v.getContext().startActivity(intento);
        }
    }

}
