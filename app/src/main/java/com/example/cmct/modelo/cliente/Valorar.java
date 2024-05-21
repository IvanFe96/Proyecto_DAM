package com.example.cmct.modelo.cliente;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;
import com.example.cmct.clases.Cliente;
import com.example.cmct.clases.Trabajador;
import com.example.cmct.clases.Utilidades;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class Valorar extends AppCompatActivity {

    Button botonAceptar;
    RatingBar valoracion;
    ImageView imagenTrabajador;

    Cliente cliente;
    Trabajador trabajadorAsignado;

    // OBTENER LA INSTANCIA DE LA BASE DE DATOS DE FIREBASE
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cliente_valorar);

        obtenerCliente();

        botonAceptar = findViewById(R.id.btnAceptarValorar);
        valoracion = findViewById(R.id.ratingBarValorar);
        imagenTrabajador = findViewById(R.id.ivTrabajadorValorar);
    }

    // CLICK DEL BOTON ACEPTAR PARA GUARDAR LA VALORACION DEL CLIENTE EN LA BASE DE DATOS
    public void clickBotonAceptar(View view) {
        // VALORAR AL TRABAJADOR
        cliente.valorarTrabajador(this, trabajadorAsignado.getNombre()+" "+trabajadorAsignado.getApellido1()+" "+trabajadorAsignado.getApellido2(),valoracion.getRating(), Timestamp.now());
    }

    // OBTENER EL CLIENTE REGISTRADO
    private void obtenerCliente() {
        // OBTENER LA ID DEL USUARIO CLIENTE QUE ESTA AUTENTICADO
        String idUsuario = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // OBTENER LOS DATOS DEL CLIENTE
        db.collection("usuarios")
                .document(idUsuario)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.exists()) {
                        // OBTENER EL OBJETO TRABAJADOR
                        cliente = queryDocumentSnapshots.toObject(Cliente.class);
                        // COMPROBAR SI EL CLIENTE NO TIENE TRABAJADOR ASIGNADO PARA CERRAR LA PANTALLA Y MOSTRAR UN MENSAJE
                        if(!tieneTrabajadorAsignado()) {
                            Utilidades.mostrarMensajes(this,2,"Todavía no tienes asignado un trabajador");
                            finish();
                        } else {
                            // EL CLIENTE TIENE UN TRABAJADOR ASIGNADO Y SE BUSCA SU IMAGEN
                            obtenerImagenTrabajadorAsignado();
                        }
                    } else {
                        Utilidades.mostrarMensajes(this, 1, "No se ha encontrado al cliente");
                    }
                })
                .addOnFailureListener(e -> {
                    Utilidades.mostrarMensajes(this, 1, "Error al buscar datos del cliente");
                });
    }

    // OBTENER LA IMAGEN DEL TRABAJADOR ASIGNADO
    private void obtenerImagenTrabajadorAsignado() {
        // OBTENER LOS DATOS DEL CLIENTE
        db.collection("usuarios")
                .whereEqualTo("dni",cliente.getTrabajadorAsignado())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        trabajadorAsignado = queryDocumentSnapshots.getDocuments().get(0).toObject(Trabajador.class);
                        // ESTABLECER LA IMAGEN DEL TRABAJADOR
                        Picasso.get()
                                .load(trabajadorAsignado.getImagen())
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .placeholder(R.drawable.ic_launcher_foreground) // IMAGEN DE CARGA
                                .error(R.drawable.ic_launcher_foreground) // IMAGEN EN CASO DE ERROR
                                .resize(700,700)
                                .into(imagenTrabajador);

                    } else {
                        Utilidades.mostrarMensajes(this, 1, "No se ha encontrado al cliente");
                    }
                })
                .addOnFailureListener(e -> {
                    Utilidades.mostrarMensajes(this, 1, "Error al buscar datos del cliente");
                });
    }

    // METODO QUE DEVUELVE TRUE SI EL CLIENTE TIENE UN TRABAJADOR ASOCIADO
    private boolean tieneTrabajadorAsignado() {
        return cliente.getTrabajadorAsignado() != null;
    }

}