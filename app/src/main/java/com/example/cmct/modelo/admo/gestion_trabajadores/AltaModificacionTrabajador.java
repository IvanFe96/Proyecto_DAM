package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;
import com.example.cmct.clases.Trabajador;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;

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
            trabajador = (Trabajador) intent.getSerializableExtra("trabajador");

            //foto = ;
            nombre.setText(trabajador.getNombre());
            apellido1.setText(trabajador.getApellido1());
            apellido2.setText(trabajador.getApellido2());
            correo.setText(trabajador.getCorreo());
            telefono.setText(trabajador.getTelefono());
            dni.setText(trabajador.getDni());
        } else {
            // SE QUIERE DAR DE ALTA UN NUEVO TRABAJADOR
            trabajador = new Trabajador();
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
            // OBTENER TODOS LOS CAMPOS PARA EL TRABAJADOR
            trabajador.setNombre(nombre.getText().toString());
            trabajador.setApellido1(apellido1.getText().toString());
            trabajador.setApellido2(apellido2.getText().toString());
            trabajador.setCorreo(correo.getText().toString());
            trabajador.setTelefono(telefono.getText().toString());
            trabajador.setDni(dni.getText().toString());

            // CREAR UN OBJETO TRABAJADOR
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
            /*if(intent.getAction().equals("NUEVO")) {
                // SE QUIERE DAR DE ALTA UN USUARIO NUEVO POR LO QUE SE ESTABLECE UNA CONTRASEÑA POR DEFECTO
                trabajadorBD.put("contraseña","123456");
            }*/
            trabajadorBD.put("contraseña","123456");
            // REGISTRAR AL USUARIO EN AUTENTICACION Y DARLO DE ALTA EN LA BASE DE DATOS
            registrarUsuario(trabajadorBD);

        } else {
            // ALGUN CAMPO NO CONTIENE LO ESPERADO Y SE MUESTRA UN MENSAJE INDICANDOLO
            ventana.setMessage(descripcion);
            ventana.show();
        }
    }

    // REGISTRAR USUARIO EN LA BASE DE DATOS
    private void registrarUsuario(Map<String, Object> trabajadorBD) {
        // CREAR AL USUARIO EN AUTENTICACION
        autenticacion.createUserWithEmailAndPassword(trabajadorBD.get("correo").toString(), trabajadorBD.get("contraseña").toString())
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // SE OBTIENE EL USUARIO AL SER EL REGISTRO EXITOSO
                        FirebaseUser firebaseUser = autenticacion.getCurrentUser();
                        if (firebaseUser != null) {
                            // CREAR EL USUARIO EN LA BASE DE DATOSCrear o actualizar el documento del usuario en Firestore
                            String userId = firebaseUser.getUid(); // ID DEL USUARIO
                            db.collection("usuarios").document(userId)
                                    .set(trabajadorBD)
                                    .addOnSuccessListener(aVoid -> {
                                        // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE DE CONFIRMACION DEL ALTA
                                        LayoutInflater inflater = getLayoutInflater();
                                        View layout = inflater.inflate(R.layout.toast_personalizado, null);

                                        TextView text = layout.findViewById(R.id.toast_text);
                                        text.setText("Trabajador dado de alta ");

                                        Toast toast = new Toast(getApplicationContext());
                                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                                        toast.setDuration(Toast.LENGTH_LONG);
                                        toast.setView(layout);
                                        toast.show();

                                        // CERRAR PANTALLA
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE DE ERROR DEL ALTA
                                        LayoutInflater inflater = getLayoutInflater();
                                        View layout = inflater.inflate(R.layout.toast_personalizado_error, null);

                                        TextView text = (TextView) layout.findViewById(R.id.toast_text);
                                        text.setText("Error al dar el alta");

                                        Toast toast = new Toast(getApplicationContext());
                                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                                        toast.setDuration(Toast.LENGTH_LONG);
                                        toast.setView(layout);
                                        toast.show();
                                    });
                        }
                    } else {
                        // EL REGISTRO FALLA Y SE INFORMA AL USUARIO
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_personalizado_error, null);

                        TextView text = (TextView) layout.findViewById(R.id.toast_text);
                        text.setText("Error al dar el alta");

                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
                    }
                });
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
                // DECODIFICAR LA IMAGEN DESDE LA URI
                Bitmap imagenOriginal = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

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
