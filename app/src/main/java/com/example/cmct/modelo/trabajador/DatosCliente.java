package com.example.cmct.modelo.trabajador;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class DatosCliente extends AppCompatActivity implements OnMapReadyCallback{
    TextView datosCliente;
    MapView casaCliente;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trabajador_horario_cliente);

        // INICIALIZAR EL MAPA
        casaCliente = findViewById(R.id.mapViewCliente);
        casaCliente.onCreate(savedInstanceState);
        casaCliente.getMapAsync(this);

        datosCliente = findViewById(R.id.tvDatosCliente);

    }

    // RELLENAR EL TextView datosCliente CON LOS DATOS DEL CLIENTE
    private void rellenarDatosCliente() {

    }

    // AÑADIR UN MARCADOR EN LA VENTANA DE GOOGLE MAPS CON LA UBICACION
    // DE LA CASA DEL CLIENTE
    private void mostrarCasaCliente(GoogleMap googleMap, String direccion) {

        // CONFIGURAR EL MapView
        Geocoder geocoder = new Geocoder(this);
        try {
            // LISTA PARA BUSCAR LA UBICACION DEL CLIENTE
            List<Address> addresses = geocoder.getFromLocationName("Los Arenales 11, Zuera, España", 1);

            // COMPROBAR QUE SE HA ENCONTRADO LA DIRECCION
            if (!addresses.isEmpty()) {

                // OBTENER LA LATITUD Y LA LONGITUD DE LA DIRECCION
                double latitud = addresses.get(0).getLatitude();
                double longitud = addresses.get(0).getLongitude();

                // UBICACION
                LatLng ubicacion = new LatLng(latitud, longitud);

                // MARCADOR EN ESA UBICACION
                googleMap.addMarker(new MarkerOptions().position(ubicacion).title("MI CASA"));

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

        mostrarCasaCliente(googleMap, "");

    }
}
