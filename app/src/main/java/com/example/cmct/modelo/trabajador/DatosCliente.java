package com.example.cmct.modelo.trabajador;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;
import com.example.cmct.clases.Cliente;
import com.example.cmct.clases.Utilidades;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class DatosCliente extends AppCompatActivity implements OnMapReadyCallback{
    TextView nombreCliente, datosCliente;
    MapView casaCliente;
    ImageView imagenCliente;

    // OBTENER LA INSTANCIA DE LA BASE DE DATOS DE FIREBASE
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Cliente cliente;
    GoogleMap mapa;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trabajador_horario_cliente);

        // INICIALIZAR EL MAPA
        casaCliente = findViewById(R.id.mapViewCliente);
        casaCliente.onCreate(savedInstanceState);
        casaCliente.getMapAsync(this);

        // OBTENER LOS DATOS DEL CLIENTE
        obtenerCliente();

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
                    mostrarCasaCliente(mapa, cliente.getDireccion()+","+cliente.getLocalidad());
                    rellenarDatosCliente();
                })
                .addOnFailureListener(e -> Utilidades.mostrarMensajes(this,1,"Error al obtener los datos del cliente"));
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
        SimpleDateFormat formatoHoras = new SimpleDateFormat("HH:mm", Locale.getDefault());
        formatoHoras.setTimeZone(TimeZone.getTimeZone("Europa/Madrid"));
        datos.append("\n\nHORARIO: "+formatoHoras.format(cliente.getHoraEntradaTrabajador().toDate())+"-"+formatoHoras.format(cliente.getHoraSalidaTrabajador().toDate()));
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
        mapa = googleMap;
    }

}
