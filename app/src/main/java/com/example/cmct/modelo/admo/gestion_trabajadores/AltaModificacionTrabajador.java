package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;
import com.example.cmct.clases.Trabajador;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AltaModificacionTrabajador extends AppCompatActivity {

    private static final int REQUEST_IMAGEN = 200;

    Intent intent;
    EditText nombre,apellido1,apellido2,correo,telefono,dni;
    ImageView foto;
    boolean fotoRellenada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_alta_modificar_trabajador);

        foto = findViewById(R.id.imagen);
        nombre = findViewById(R.id.nombre);
        apellido1 = findViewById(R.id.apellido1);
        apellido2 = findViewById(R.id.apellido2);
        correo = findViewById(R.id.correo);
        telefono = findViewById(R.id.telefono);
        dni = findViewById(R.id.dni);

        // COMPROBAR SI EL USUARIO QUIERE EDITAR A UN TRABAJADOR
        intent = getIntent();
        if(intent.getAction().equals("EDITAR")) {
            Trabajador trabajador = (Trabajador) intent.getSerializableExtra("trabajador");
            Toast.makeText(this, trabajador.getNombre(), Toast.LENGTH_SHORT).show();
            //foto = ;
            nombre.setText(trabajador.getNombre());
            apellido1.setText(trabajador.getApellido1());
            apellido2.setText(trabajador.getApellido2());
            correo.setText(trabajador.getCorreo());
            telefono.setText(trabajador.getTelefono());
            dni.setText(trabajador.getDni());
        }
    }

    // CLICK EN EL BOTON DE GUARDAR PARA DAR DE ALTA EN LA BASE DE DATOS AL TRABAJADOR
    public void clickBotonGuardar(View view) {
        // CREAR UN DIALOGO EN CASO DE QUE HAYA ALGO MAL EN LOS CAMPOS RELLENADOS
        final AlertDialog.Builder ventana = new AlertDialog.Builder(this);
        ventana.setTitle("ERROR");
        String descripcion = "";

        // COMPROBAR SI SE HA SELECCIONADO UNA IMAGEN
        if(!fotoRellenada) {

            descripcion = "- Debes seleccionar una imagen.\n";

        }

        // COMPROBAR SI TODOS LOS CAMPOS ESTAN RELLENADOS
        if(nombre.getText().toString().isEmpty() || apellido1.getText().toString().isEmpty() || apellido2.getText().toString().isEmpty()
                || correo.getText().toString().isEmpty() || telefono.getText().toString().isEmpty() || dni.getText().toString().isEmpty()) {

            descripcion += "- Todos los campos deben estar rellenados.\n";

        } else {

            // VALIDAR SI EL CORREO ES CORRECTO
            if (!validarCorreo(correo.getText().toString())) {

                descripcion += "- El correo no es válido.\n";

            }

            // VALIDAR SI EL TELEFONO ES CORRECTO
            if (!validarTelefono(telefono.getText().toString())) {

                descripcion += "- El teléfono no es válido.\n";

            }

            // VALIDAR SI EL DNI ES CORRECTO
            if (!validarDni(dni.getText().toString())) {

                descripcion += "- El DNI no es válido.\n";

            }
        }

        // COMPROBAR QUE LA DESCRIPCION ESTA VACIA PARA DAR DE ALTA AL TRABAJADOR EN LA BASE DE DATOS
        if(descripcion.isEmpty()) {

        } else {
            // ALGUN CAMPO NO CONTIENE LO ESPERADO Y SE MUESTRA UN MENSAJE INDICANDOLO
            ventana.setMessage(descripcion);
            ventana.show();
        }
    }

    // COMPROBAR QUE EL CORREO ES UNA CUENTA DE GMAIL
    private boolean validarCorreo(String correo) {
        return correo.contains("@gmail.com");
    }

    private boolean validarTelefono(String telefono) {
        // PATRON PARA VALIDAR NUMEROS DE TELEFONO
        String patron = "^[6789]\\d{8}$";

        // COMPILAR LA EXPRESION
        Pattern pattern = Pattern.compile(patron);

        // VERIFICAR SI EL NUMERO COINCIDE CON EL PATRON
        Matcher matcher = pattern.matcher(telefono);
        return matcher.matches();
    }

    // COMPROBAR QUE EL DNI ES CORRECTO
    private boolean validarDni(String dni) {
        // VERIFICAR QUE EL DNI TIENE LONGITUD 9
        if (dni == null || dni.length() != 9) {
            return false;
        }

        // EXTRAER EL NUMERO Y LA LETRA DEL DNI
        String numero = dni.substring(0, 8);
        String letra = dni.substring(8).toUpperCase();

        // VERIFICAR QUE EL NUMERO DEL DNI ES UN NUMERO
        try {
            Integer.parseInt(numero);
        } catch (NumberFormatException e) {
            return false;
        }

        // CALCULAR LA LETRA CORRESPONDIENTE AL DNI
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        int indice = Integer.parseInt(numero) % 23;
        char letraCalculada = letras.charAt(indice);

        // COMPROBAR SI LA LETRA CALCULADA COINCIDE CON LA LETRA DEL DNI
        return letraCalculada == letra.charAt(0);
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

                // INDICAR QUE LA FOTO ESTA RELLENADA
                fotoRellenada = true;

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
