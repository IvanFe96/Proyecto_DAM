package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;
import com.example.cmct.clases.Administrador;
import com.example.cmct.clases.Trabajador;
import com.example.cmct.clases.Utilidades;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModificacionTrabajador extends AppCompatActivity {

    private static final int REQUEST_IMAGEN = 200;

    Intent intent;
    EditText nombre,apellido1,apellido2,correo,telefono;
    ImageView foto;

    boolean fotoRellenada = false;

    Trabajador trabajador;

    // OBTENER LA INSTANCIA DE LA BASE DE DATOS DE FIREBASE
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Administrador administrador;

    // URI DE LA IMAGEN DEL TRABAJADOR
    Uri imagenUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_modificar_trabajador);

        // OBTENER EL ADMINISTRADOR
        obtenerAdministrador();

        foto = findViewById(R.id.imagen);
        nombre = findViewById(R.id.nombre);
        apellido1 = findViewById(R.id.apellido1);
        apellido2 = findViewById(R.id.apellido2);
        correo = findViewById(R.id.correo);
        telefono = findViewById(R.id.telefono);

        // OBTENER EL INTENTO CON LOS DATOS QUE ME PASAN
        intent = getIntent();
        // OBTENER EL ID DEL USUARIO
        String idUsuario = intent.getStringExtra("idusuario");
            
        // OBTENER TODOS LOS DATOS DEL USUARIO DE LA BASE DE DATOS
        db.collection("usuarios").document(idUsuario)
                .get()
                .addOnSuccessListener(documentSnapshot ->  {
                    trabajador = documentSnapshot.toObject(Trabajador.class);
                        
                    if(trabajador != null) {
                        if (trabajador.getImagen() != null && !trabajador.getImagen().isEmpty()) {
                            // CARGAR LA IMAGEN DESDE FIREBASE STORAGE CON PICASSO
                            Picasso.get()
                                    .load(trabajador.getImagen().toString())
                                    .resize(foto.getWidth(), foto.getHeight())
                                    .centerCrop()
                                    .into(foto);
                            // DECIR QUE EL IMAGEVIEW ESTA RELLENADO
                            fotoRellenada = true;

                            // DECIR QUE LA URI ES NULA PARA SABER SI SE HA CAMBIADO LA IMAGEN
                            imagenUri = null;

                        } else {
                            foto.setImageResource(R.drawable.ic_launcher_foreground); // IMAGEN PREDETERMINADA SI NO HAY URL (NO DEBERIA OCURRIR)
                        }

                        nombre.setText(trabajador.getNombre());
                        apellido1.setText(trabajador.getApellido1());
                        apellido2.setText(trabajador.getApellido2());
                        correo.setText(trabajador.getCorreo());
                        telefono.setText(trabajador.getTelefono());

                    } else {
                        Utilidades.mostrarMensajes(this,1,"El trbajador está vacío");
                    }
                })
                .addOnFailureListener(e -> {
                    Utilidades.mostrarMensajes(this,1,"Error al recuperar los datos");
                });
    }

    // CLICK EN EL BOTON DE GUARDAR PARA MODIFICAR EN LA BASE DE DATOS AL TRABAJADOR
    public void clickBotonGuardar(View view) {
        // GUARDAR LOS TIPOS DE ERRORES QUE HAY EN LOS CAMPOS
        String descripcion = "";

        // COMPROBAR SI SE HA SELECCIONADO UNA IMAGEN
        if(!fotoRellenada) {

            descripcion = "- Debes seleccionar una imagen.\n";

        }

        // COMPROBAR SI TODOS LOS CAMPOS ESTAN RELLENADOS
        if(nombre.getText().toString().isEmpty() || apellido1.getText().toString().isEmpty() || apellido2.getText().toString().isEmpty()
                || correo.getText().toString().isEmpty() || telefono.getText().toString().isEmpty()) {

            descripcion += "- Todos los campos deben estar rellenados.\n";

        } else {

            // VALIDAR SI EL NOMBRE Y LOS APELLIDOS NO CONTIENEN DIGITOS
            if(!validarNombreApellidos(nombre.getText().toString(),apellido1.getText().toString(),apellido2.getText().toString())) {

                descripcion += "- El nombre o los apellidos solo pueden contener letras";

            }

            // VALIDAR SI EL CORREO ES CORRECTO
            if (!validarCorreo(correo.getText().toString())) {

                descripcion += "- El correo no es válido.\n";

            }

            // VALIDAR SI EL TELEFONO ES CORRECTO
            if (!validarTelefono(telefono.getText().toString())) {

                descripcion += "- El teléfono no es válido.\n";

            }

        }

        // COMPROBAR QUE LA DESCRIPCION ESTA VACIA PARA MODIFICAR AL TRABAJADOR EN LA BASE DE DATOS
        if(descripcion.isEmpty()) {
            // GUARDAMOS EL CORREO PARA QUE EN CASO DE CAMBIO NOS PODAMOS AUTENTICAR PARA VERIIFICAR DATOS MAS TARDE
            String correoTrabajador = trabajador.getCorreo();

            // OBTENER TODOS LOS CAMPOS PARA EL TRABAJADOR
            trabajador.setNombre(Utilidades.primeraLetraMayuscula(nombre.getText().toString().trim()));
            trabajador.setApellido1(Utilidades.primeraLetraMayuscula(apellido1.getText().toString().trim()));
            trabajador.setApellido2(Utilidades.primeraLetraMayuscula(apellido2.getText().toString().trim()));
            trabajador.setCorreo(correo.getText().toString().trim());
            trabajador.setTelefono(telefono.getText().toString().trim());

            administrador.editarTrabajador(trabajador,correoTrabajador,imagenUri,this);

        } else {
            // ALGUN CAMPO NO CONTIENE LO ESPERADO Y SE MUESTRA UN MENSAJE INDICANDOLO
            Utilidades.mostrarMensajes(this,1, descripcion);
        }
    }

    // COMPROBAR QUE EL NOMBRE Y LOS APELLIDOS ES TEXTO Y NO HAY NUMEROS
    private boolean validarNombreApellidos(String nombre, String apellido1, String apellido2) {
        // UTILIZAR UNA EXPRESION REGULAR QUE PERMITA SOLO LETRAS Y ESPACIOS EN BLANCO
        String regex = "^[\\p{L} ]+$";
        // COMPROBAR QUE LOS CAMPOS CUMPLEN CON LA EXPRESION REGULAR
        return nombre.matches(regex) && apellido1.matches(regex) && apellido2.matches(regex);
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
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGEN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGEN) {
            // OBTENER LA IMAGEN DE LA GALERIA Y ESTABLECERLA COMO IMAGEN EN EL FORMULARIO
            imagenUri = data.getData();

            try {
                // DECODIFICAR LA IMAGEN DESDE LA URI
                Bitmap imagenOriginal = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagenUri);

                // REDIMENSIONAR LA IMAGEN PARA QUE SE AJUSTE AL TAMAÑO DEL IMAGEVIEW
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

    private void obtenerAdministrador() {
        db.collection("usuarios")
                .whereEqualTo("rol", "administrador")
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // OBTENER EL USUARIO ADMINISTRADOR
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        administrador = documentSnapshot.toObject(Administrador.class);
                    } else {
                        Utilidades.mostrarMensajes(this, 1, "No se encontraron administradores");
                    }
                })
                .addOnFailureListener(e -> {
                    Utilidades.mostrarMensajes(this, 1, "Error al buscar datos de administrador");
                });
    }

}
