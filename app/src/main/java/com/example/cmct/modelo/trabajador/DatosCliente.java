package com.example.cmct.modelo.trabajador;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;
import com.example.cmct.clases.Cliente;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DatosCliente extends AppCompatActivity implements OnMapReadyCallback{
    TextView nombreCliente, datosCliente;
    MapView casaCliente;
    ImageView imagenCliente;

    // OBTENER LA INSTANCIA DE LA BASE DE DATOS DE FIREBASE
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Cliente cliente;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trabajador_horario_cliente);

        // OBTENER LOS DATOS DEL CLIENTE
        obtenerCliente();

        // INICIALIZAR EL MAPA
        casaCliente = findViewById(R.id.mapViewCliente);
        casaCliente.onCreate(savedInstanceState);
        casaCliente.getMapAsync(this);

        nombreCliente = findViewById(R.id.tvNombreApellidosClienteHorario);
        datosCliente = findViewById(R.id.tvDatosCliente);
        imagenCliente = findViewById(R.id.imageViewCliente);

    }

    // OBTENER TODOS LOS DATOS DEL CLIENTE
    private void obtenerCliente() {
        db.collection("usuarios")
                .whereEqualTo("dni",getIntent().getStringExtra("dniCliente"))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    cliente = queryDocumentSnapshots.getDocuments().get(0).toObject(Cliente.class);
                    rellenarDatosCliente();
                })
                .addOnFailureListener(e -> mostrarMensajes(getApplicationContext(),1,"Error al obtener los datos del cliente"));
    }

    // RELLENAR EL TextView datosCliente CON LOS DATOS DEL CLIENTE
    private void rellenarDatosCliente() {
        // ESTABLECER EL NOMBRE DEL CLIENTE
        nombreCliente.setText(cliente.getNombre()+" "+cliente.getApellido1()+" "+cliente.getApellido2());

        // MOSTRAR ALGUNOS DATOS IMPORTANTES DEL CLIENTE
        StringBuilder datos = new StringBuilder();
        datos.append("NECESIDADES:");
        for(Map.Entry<String, String> necesidad : cliente.getNecesidades().entrySet())
        {
            datos.append("\n- "+ necesidad.getValue());
        }
        datos.append("\n\nHORARIO: "+cliente.getHoraEntradaTrabajador()+"-"+cliente.getHoraSalidaTrabajador());
        datos.append("\n\nDOMICILIO: "+cliente.getDireccion()+", "+cliente.getLocalidad());
        datos.append("\n\nCORREO: "+cliente.getCorreo());
        datos.append("\n\nTELÉFONO: "+cliente.getTelefono());

        datosCliente.setText(datos);

        // CARGAR LA IMAGEN DESDE FIREBASE STORAGE CON PICASSO
        Picasso.get()
                .load(cliente.getImagen().toString())
                .resize(500, 500)
                .centerCrop()
                .into(imagenCliente);
    }

    // AÑADIR UN MARCADOR EN LA VENTANA DE GOOGLE MAPS CON LA UBICACION
    // DE LA CASA DEL CLIENTE
    private void mostrarCasaCliente(GoogleMap googleMap, String direccion) {

        // CONFIGURAR EL MapView
        Geocoder geocoder = new Geocoder(this);
        try {
            // LISTA PARA BUSCAR LA UBICACION DEL CLIENTE
            List<Address> addresses = geocoder.getFromLocationName(direccion+", España", 1);

            // COMPROBAR QUE SE HA ENCONTRADO LA DIRECCION
            if (!addresses.isEmpty()) {

                // OBTENER LA LATITUD Y LA LONGITUD DE LA DIRECCION
                double latitud = addresses.get(0).getLatitude();
                double longitud = addresses.get(0).getLongitude();

                // UBICACION
                LatLng ubicacion = new LatLng(latitud, longitud);

                // MARCADOR EN ESA UBICACION
                googleMap.addMarker(new MarkerOptions().position(ubicacion).title(cliente.getNombre()));

                // MOVER LA CAMARA A LA UBICACION
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15f));

            } else {
                // MOSTRAR UN MENSAJE SI NO SE HA ENCONTRADO LA DIRECCION
                Toast.makeText(this, "La dirección no se encontró", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            // MOSTRAR MENSAJE DE ERROR SI NO SE HA PODIDO OBTENER LA DIRECCION
            e.printStackTrace();
            Toast.makeText(this, "Error al obtener la dirección", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mostrarCasaCliente(googleMap, cliente.getDireccion()+","+cliente.getLocalidad());

    }
    // MOSTRAR TOAST PERSONALIZADOS DE ERRORES Y DE QUE TODO HA IDO CORRECTO
    private void mostrarMensajes(Context contexto, int tipo, String mensaje) {
        // MENSAJE DE QUE ES CORRECTO
        if(tipo == 0) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_personalizado, null);

            TextView text = (TextView) layout.findViewById(R.id.toast_text);
            text.setText(mensaje); // CONFIGURAR EL MENSAJE PERSONALIZADO

            Toast toast = new Toast(contexto.getApplicationContext());
            toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 300);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        } else {
            // MENSAJE DE ERRORES
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_personalizado_error, null);

            TextView text = (TextView) layout.findViewById(R.id.toast_text);
            text.setText(mensaje); // CONFIGURAR EL MENSAJE DE ERROR PERSONALIZADO

            Toast toast = new Toast(contexto.getApplicationContext());
            toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 300);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }
    }

}
