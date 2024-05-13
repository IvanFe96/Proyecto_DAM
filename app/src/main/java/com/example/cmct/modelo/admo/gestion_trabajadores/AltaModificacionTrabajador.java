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
import com.example.cmct.clases.Trabajador;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AltaModificacionTrabajador extends AppCompatActivity {

    private static final int REQUEST_IMAGEN = 200;

    Intent intent;
    EditText nombre,apellido1,apellido2,correo,telefono,dni;
    ImageView foto;
    boolean fotoRellenada = false;
    Trabajador trabajador;

    // OBTENER LAS INSTANCIAS DE AUTENTICACION Y LA BASE DE DATOS DE FIREBASE
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth autenticacion = FirebaseAuth.getInstance();

    // OBTENER LA INSTANCIA DE ALMACENAMIENTO DE IMAGENES Y LA REFERENCIA
    FirebaseStorage almacenamientoImagenes = FirebaseStorage.getInstance();
    StorageReference referenciaImagenes = almacenamientoImagenes.getReference();

    // URI DE LA IMAGEN DEL TRABAJADOR
    Uri imagenUri;

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
            // OBTENER EL ID DEL USUARIO
            String idUsuario = intent.getStringExtra("idusuario");
            
            // OBTENER TODOS LOS DATOS DEL USUARIO DE LA BASE DE DATOS
            db.collection("usuarios").document(idUsuario)
                    .get()
                    .addOnSuccessListener(documentSnapshot ->  {
                        trabajador = documentSnapshot.toObject(Trabajador.class);
                        
                        if(trabajador != null) {
                            // CARGAR LA IMAGEN DESDE FIREBASE STORAGE CON PICASSO
                            if (trabajador.getImagen() != null && !trabajador.getImagen().isEmpty()) {
                                Picasso.get()
                                        .load(trabajador.getImagen())
                                        .resize(foto.getWidth(), foto.getHeight())
                                        .centerCrop()
                                        .into(foto);
                            } else {
                                foto.setImageResource(R.drawable.ic_launcher_foreground); // IMAGEN PREDETERMINADA SI NO HAY URL (NO DEBERIA OCURRIR)
                            }

                            nombre.setText(trabajador.getNombre());
                            apellido1.setText(trabajador.getApellido1());
                            apellido2.setText(trabajador.getApellido2());
                            correo.setText(trabajador.getCorreo());
                            telefono.setText(trabajador.getTelefono());
                            dni.setText(trabajador.getDni());

                        } else {
                            Toast.makeText(this, "algo anda mal", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        mostrarMensajes(getApplicationContext(),1,"Error al recuperar los datos");
                    });
        } else {
            // SE QUIERE DAR DE ALTA UN NUEVO TRABAJADOR
            trabajador = new Trabajador();
        }
    }

    // CLICK EN EL BOTON DE GUARDAR PARA DAR DE ALTA EN LA BASE DE DATOS AL TRABAJADOR
    public void clickBotonGuardar(View view) {
        // GUARDAR LOS TIPOS DE ERRORES QUE HAY EN LOS CAMPOS
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

            // VALIDAR SI EL DNI ES CORRECTO
            if (!validarDni(dni.getText().toString())) {

                descripcion += "- El DNI no es válido.\n";

            }
        }

        // COMPROBAR QUE LA DESCRIPCION ESTA VACIA PARA DAR DE ALTA AL TRABAJADOR EN LA BASE DE DATOS
        if(descripcion.isEmpty()) {
            // OBTENER TODOS LOS CAMPOS PARA EL TRABAJADOR
            trabajador.setNombre(nombre.getText().toString());
            trabajador.setApellido1(apellido1.getText().toString());
            trabajador.setApellido2(apellido2.getText().toString());
            trabajador.setCorreo(correo.getText().toString());
            trabajador.setTelefono(telefono.getText().toString());
            trabajador.setDni(dni.getText().toString());

            // CREAR UN DOCUMENTO TRABAJADOR
            Map<String, Object> trabajadorBD = new HashMap<>();
            trabajadorBD.put("nombre", trabajador.getNombre());
            trabajadorBD.put("apellido1", trabajador.getApellido1());
            trabajadorBD.put("apellido2", trabajador.getApellido2());
            trabajadorBD.put("dni", trabajador.getDni().toUpperCase());
            trabajadorBD.put("correo", trabajador.getCorreo());
            trabajadorBD.put("telefono", trabajador.getTelefono());
            trabajadorBD.put("rol", "trabajador");

            // COMPROBAR SI SE QUIERE DAR DE ALTA UN NUEVO USUARIO
            // PARA NO MODIFICAR LA CONTRASEÑA EN CASO DE QUE SE ESTE EDITANDO
            if(intent.getAction().equals("NUEVO")) {
                // SE QUIERE DAR DE ALTA UN USUARIO NUEVO POR LO QUE SE ESTABLECE UNA CONTRASEÑA POR DEFECTO
                trabajadorBD.put("contraseña","123456");
            } else {
                // SE QUIERE EDITAR AL TRABAJADOR POR LO QUE LA CONTRASEÑA SE QUEDA IGUAL QUE LA QUE TIENE
                trabajadorBD.put("contraseña",trabajador.getContrasenia());
            }

            // REGISTRAR AL USUARIO EN AUTENTICACION Y DARLO DE ALTA EN LA BASE DE DATOS
            registrarUsuario(trabajadorBD);

        } else {
            // ALGUN CAMPO NO CONTIENE LO ESPERADO Y SE MUESTRA UN MENSAJE INDICANDOLO
            mostrarMensajes(getApplicationContext(),1, descripcion);
        }
    }

    // REGISTRAR USUARIO EN LA BASE DE DATOS
    private void registrarUsuario(Map<String, Object> trabajadorBD) {

        StorageReference imagenRef = referenciaImagenes.child("imagenes/" + imagenUri.getLastPathSegment());

        UploadTask uploadTask = imagenRef.putFile(imagenUri);

        // AÑADIR LA IMAGEN AL ALMACENAMIENTO DE IMAGENES
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // URL DE DESCARGA DEL ARCHIVO SUBIDO
                        String urlDescarga = uri.toString();

                        // GUARDAR LA URL DE DESCARGA EN EL TRABAJADOR
                        trabajadorBD.put("imagen", urlDescarga);

                        // CREAR AL USUARIO EN AUTENTICACION
                        autenticacion.createUserWithEmailAndPassword(trabajadorBD.get("correo").toString(), trabajadorBD.get("contraseña").toString())
                                .addOnCompleteListener(AltaModificacionTrabajador.this, task -> {
                                    if (task.isSuccessful()) {
                                        // SE OBTIENE EL USUARIO AL SER EL REGISTRO EXITOSO
                                        FirebaseUser firebaseUser = task.getResult().getUser();

                                        if (firebaseUser != null) {

                                            // CREAR EL USUARIO O ACTUALIZARLO EN LA BASE DE DATOS
                                            String userId = firebaseUser.getUid(); // ID DEL USUARIO
                                            db.collection("usuarios").document(userId)
                                                    .set(trabajadorBD)
                                                    .addOnSuccessListener(aVoid -> {

                                                        // COMPROBAR SI SE QUERIA DAR DE ALTA UN NUEVO TRABAJADOR
                                                        if(intent.getAction().equals("NUEVO")) {
                                                            // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE DE CONFIRMACION DEL ALTA
                                                            mostrarMensajes(getApplicationContext(), 0, "Trabajador dado de alta");
                                                        } else {
                                                            // SE QUIERE EDITAR AL TRABAJADOR
                                                            // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE DE CONFIRMACION DE LA MODIFICACION
                                                            mostrarMensajes(getApplicationContext(), 0, "Trabajador modificado");
                                                        }
                                                        // CERRAR PANTALLA
                                                        finish();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE DE ERROR DEL ALTA
                                                        mostrarMensajes(getApplicationContext(),1,"Error al dar el alta");
                                                    });
                                        }
                                    } else {
                                        // EL REGISTRO FALLA Y SE INFORMA AL USUARIO
                                        mostrarMensajes(getApplicationContext(),1,"El correo ya existe");
                                    }
                                });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE DE ERROR DEL GUARDADO DE LA IMAGEN
                mostrarMensajes(getApplicationContext(),1,"Error al guardar la imagen");
            }
        });

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

}
