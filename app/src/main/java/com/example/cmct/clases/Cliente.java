package com.example.cmct.clases;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cmct.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.util.HashMap;

public class Cliente extends Usuario implements Serializable {

    private String localidad;
    private String direccion;
    private String horaEntradaTrabajador;
    private String horaSalidaTrabajador;
    private HashMap<String, String> necesidades;
    private String trabajadorAsignado;

    // OBTENER LA INSTANCIA DE LA BASE DE DATOS DE FIREBASE
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Cliente() {
        super();
    }

    public Cliente(String imagen, String nombre, String apellido1, String apellido2, String telefono, String dni, String correo, String contrasenia, String rol,
                   String localidad, String direccion, String horaEntradaTrabajador, String horaSalidaTrabajador, HashMap<String, String> necesidades, String trabajadorAsignado) {
        super(imagen, nombre, apellido1, apellido2, telefono, dni, correo, contrasenia, rol);

        this.localidad = localidad;
        this.direccion = direccion;
        this.horaEntradaTrabajador = horaEntradaTrabajador;
        this.horaSalidaTrabajador = horaSalidaTrabajador;
        this.necesidades = necesidades;
        this.trabajadorAsignado = trabajadorAsignado;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getHoraEntradaTrabajador() {
        return horaEntradaTrabajador;
    }

    public void setHoraEntradaTrabajador(String horaEntradaTrabajador) {
        this.horaEntradaTrabajador = horaEntradaTrabajador;
    }

    public String getHoraSalidaTrabajador() {
        return horaSalidaTrabajador;
    }

    public void setHoraSalidaTrabajador(String horaSalidaTrabajador) {
        this.horaSalidaTrabajador = horaSalidaTrabajador;
    }

    public HashMap<String, String> getNecesidades() {
        return necesidades;
    }

    public void setNecesidades(HashMap<String, String> necesidades) {
        this.necesidades = necesidades;
    }

    public String getTrabajadorAsignado() {
        return trabajadorAsignado;
    }

    public void setTrabajadorAsignado(String trabajadorAsignado) {
        this.trabajadorAsignado = trabajadorAsignado;
    }

    // METODO PARA REGISTRAR LAS NECESIDADES EN LA BASE DE DATOS
    public void registrarNecesidades(Activity actividad) {
        db.collection("usuarios")
                .whereEqualTo("dni",this.getDni())
                .get()
                .addOnCompleteListener(task -> {
                    String idCliente = task.getResult().getDocuments().get(0).getId();
                    db.collection("usuarios")
                            .document(idCliente)
                            .update("necesidades",this.necesidades)
                            .addOnSuccessListener(task1 -> {
                               mostrarMensajes(actividad,0,"Necesidades registrada con éxito");
                               actividad.finish();
                            })
                            .addOnFailureListener(e -> {
                                mostrarMensajes(actividad,1,"Error al registrar las necesidades");
                            });
                }).addOnFailureListener(e -> {
                    mostrarMensajes(actividad,1,"Error al encontrar al cliente");
                });
    }

    // METODO PARA VALORAR AL TRABAJADOR ASIGNADO
    public void valorarTrabajador(Activity actividad, String nombreTrabajador, float valoracion, Timestamp fechaValoracion) {
        // CREAR UN OBJETO VALORACION PARA INTRODUCIR SUS DATOS EN LA BASE DE DATOS
        Valoracion objetoValoracion = new Valoracion(this.getTrabajadorAsignado(),nombreTrabajador, valoracion,fechaValoracion);

        db.collection("valoraciones")
                .document()
                .set(objetoValoracion)
                .addOnSuccessListener(aVoid -> {
                    // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE DE CONFIRMACION DE LA VALORACION
                    mostrarMensajes(actividad, 0, "Valoración realizada con éxito");

                    // CERRAR PANTALLA
                    actividad.finish();
                })
                .addOnFailureListener(e -> {
                    // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE DE ERROR DE LA VALORACION
                    mostrarMensajes(actividad,1,"Error al valorar al trabajador");
                });
    }

    // MOSTRAR TOAST PERSONALIZADOS DE ERRORES Y DE QUE TODO HA IDO CORRECTO
    private void mostrarMensajes(Activity activiad, int tipo, String mensaje) {
        // MENSAJE DE QUE ES CORRECTO
        if(tipo == 0) {
            LayoutInflater inflater = activiad.getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_personalizado, null);

            TextView text = (TextView) layout.findViewById(R.id.toast_text);
            text.setText(mensaje); // CONFIGURAR EL MENSAJE PERSONALIZADO

            Toast toast = new Toast(activiad.getApplicationContext());
            toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 300);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        } else {
            // MENSAJE DE ERRORES
            LayoutInflater inflater = activiad.getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_personalizado_error, null);

            TextView text = (TextView) layout.findViewById(R.id.toast_text);
            text.setText(mensaje); // CONFIGURAR EL MENSAJE DE ERROR PERSONALIZADO

            Toast toast = new Toast(activiad.getApplicationContext());
            toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 300);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }
    }

    // SE USA PARA SABER SI HAY CLIENTES IGUALES
    // ? : (es una forma mas corta de hacer una condicion else if)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return this.getDni() != null ? this.getDni().equals(cliente.getDni()) : cliente.getDni() == null;
    }

    @Override
    public int hashCode() {
        return this.getDni() != null ? this.getDni().hashCode() : 0;
    }

    // SE USA PARA MOSTRAR EL NOMBRE EN LOS DESPLEGABLES DE ASIGNAR TRABAJO
    @Override
    public String toString() {
        return this.getNombre();
    }
}
