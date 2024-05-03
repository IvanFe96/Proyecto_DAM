package com.example.cmct.modelo.admo.gestion_clientes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;
import com.example.cmct.clases.Cliente;
import com.example.cmct.clases.Trabajador;

import java.io.IOException;

public class AltaModificacionCliente extends AppCompatActivity {

    private static final int REQUEST_IMAGEN = 100;

    Intent intent;
    EditText nombre, apellido1, apellido2, correo, telefono, dni, direccion;
    ImageView foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_alta_modificar_cliente);

        foto = findViewById(R.id.imagen);
        nombre = findViewById(R.id.nombre);
        apellido1 = findViewById(R.id.apellido1);
        apellido2 = findViewById(R.id.apellido2);
        correo = findViewById(R.id.correo);
        telefono = findViewById(R.id.telefono);
        dni = findViewById(R.id.dni);
        direccion = findViewById(R.id.direccion);

        // COMPROBAR SI EL USUARIO QUIERE EDITAR A UN CLIENTE
        intent = getIntent();
        if(intent.getAction().equals("EDITAR")) {
            Cliente cliente = (Cliente) intent.getSerializableExtra("cliente");
            //foto = ;
            nombre.setText(cliente.getNombre());
            apellido1.setText(cliente.getApellido1());
            apellido2.setText(cliente.getApellido2());
            correo.setText(cliente.getCorreo());
            telefono.setText(cliente.getTelefono());
            dni.setText(cliente.getDni());
            direccion.setText(cliente.getDireccion());
        }
    }

    // CLICK PARA ABRIR LA GALERIA Y ELEGIR LA IMAGEN
    public void clickImagen(View view) {
        //MOSTRAR LA GALERIA DEL MOVIL
        intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGEN) {
            // OBTENER LA IMAGEN DE LA GALERIA Y ESTABLECERLA COMO IMAGEN EN EL FORMULARIO
            Uri imageUri = data.getData();

            try {
                // Decodifica la imagen desde la URI
                Bitmap imagenOriginal = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                // Redimensiona la imagen para que se ajuste al tamaño del ImageView
                int ancho = foto.getWidth();
                int alto = foto.getHeight();
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(imagenOriginal, ancho, alto, false);

                // SE ESTABLECE LA IMAGEN EN SU CONTENEDOR
                foto.setImageBitmap(scaledBitmap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
