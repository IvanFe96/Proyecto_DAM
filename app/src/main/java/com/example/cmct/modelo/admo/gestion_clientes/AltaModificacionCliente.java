package com.example.cmct.modelo.admo.gestion_clientes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;
import com.example.cmct.clases.Cliente;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AltaModificacionCliente extends AppCompatActivity {

    private static final int REQUEST_IMAGEN = 100;

    Intent intent;
    EditText nombre, apellido1, apellido2, correo, telefono, dni, direccion;
    Spinner localidades;
    ImageView foto;
    boolean fotoRellenada = false;
    Button botonGuardar;
    Cliente cliente;

    // OBTENER LAS INSTANCIAS DE AUTENTICACION Y LA BASE DE DATOS DE FIREBASE
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth autenticacion = FirebaseAuth.getInstance();

    // OBTENER LA INSTANCIA DE ALMACENAMIENTO DE IMAGENES Y LA REFERENCIA
    FirebaseStorage almacenamientoImagenes = FirebaseStorage.getInstance();
    StorageReference referenciaImagenes = almacenamientoImagenes.getReference();

    // URI DE LA IMAGEN DEL CLIENTE
    Uri imagenUri;

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
        localidades = findViewById(R.id.spinnerLocalidades);
        botonGuardar = findViewById(R.id.btnGuardarCliente);

        // RELLENAR EL DESPLEGABLE DE CIUDADES
        List<String> opcionesCiudad = Arrays.asList("Zuera", "San Mateo de Gállego", "Villanueva de Gállego", "Ontinar del Salz");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesCiudad);
        // ESPECIFICAR EL DISEÑO QUE SE UTILIZARA CUANDO SE MUESTREN LAS OPCIONES
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // ESTABLECER EL ADAPTADOR AL DESPLEGABLE
        localidades.setAdapter(adapter);

        // COMPROBAR SI EL USUARIO QUIERE EDITAR A UN CLIENTE
        intent = getIntent();
        if(intent.getAction().equals("EDITAR")) {
            // OBTENER EL CLIENTE QUE SE HA SELECCIONADO EN LA ANTERIOR PANTALLA
            cliente = (Cliente) intent.getSerializableExtra("cliente");

            // RELLENAR LOS CAMPOS CON LOS DATOS DEL CLIENTE QUE SE QUIERE MODIFICAR
            //foto = ;
            nombre.setText(cliente.getNombre());
            apellido1.setText(cliente.getApellido1());
            apellido2.setText(cliente.getApellido2());
            correo.setText(cliente.getCorreo());
            telefono.setText(cliente.getTelefono());
            dni.setText(cliente.getDni());
            direccion.setText(cliente.getDireccion());

            // OBTENER EL ÍNDICE DE LA CIUDAD DEL CLIENTE EN LA LISTA DE OPCIONES
            int index = opcionesCiudad.indexOf(cliente.getCiudad());

            // ESTABLECER LA CIUDAD DEL CLIENTE COMO LA SELECCIÓN ACTUAL DEL SPINNER
            localidades.setSelection(index);
        }
    }

    // CLICK EN EL BOTON DE GUARDAR PARA DAR DE ALTA EN LA BASE DE DATOS AL CLIENTE
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
                || correo.getText().toString().isEmpty() || telefono.getText().toString().isEmpty() || dni.getText().toString().isEmpty() || direccion.getText().toString().isEmpty()) {

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

            if(!validarDireccion(direccion.getText().toString(), localidades.getSelectedItem().toString())) {

                descripcion += "- La dirección no es válida.";

            }
        }

        // COMPROBAR QUE LA DESCRIPCION ESTA VACIA PARA DAR DE ALTA AL CLIENTE EN LA BASE DE DATOS
        if(descripcion.isEmpty()) {

            // OBTENER TODOS LOS CAMPOS PARA EL CLIENTE
            cliente.setNombre(nombre.getText().toString());
            cliente.setApellido1(apellido1.getText().toString());
            cliente.setApellido2(apellido2.getText().toString());
            cliente.setCorreo(correo.getText().toString());
            cliente.setTelefono(telefono.getText().toString());
            cliente.setDni(dni.getText().toString());
            cliente.setDireccion(direccion.getText().toString());
            cliente.setTrabajadorAsignado(null);
            cliente.setHoraEntradaTrabajador(null);
            cliente.setHoraSalidaTrabajador(null);
            cliente.setNecesidades(null);

            // CREAR UN OBJETO CLIENTE
            Map<String, Object> clienteBD = new HashMap<>();
            clienteBD.put("nombre", cliente.getNombre());
            clienteBD.put("apellido1", cliente.getApellido1());
            clienteBD.put("apellido2", cliente.getApellido2());
            clienteBD.put("dni", cliente.getDni().toUpperCase());
            clienteBD.put("correo", cliente.getCorreo());
            clienteBD.put("telefono", cliente.getTelefono());
            clienteBD.put("rol", "cliente");
            clienteBD.put("direccion",cliente.getDireccion());
            clienteBD.put("trabajadorAsignado",cliente.getTrabajadorAsignado());
            clienteBD.put("horaEntradaTrabajador",cliente.getHoraEntradaTrabajador());
            clienteBD.put("horaSalidaTrabajador",cliente.getHoraSalidaTrabajador());
            clienteBD.put("necesidades",cliente.getNecesidades());

            // COMPROBAR SI SE QUIERE DAR DE ALTA UN NUEVO USUARIO
            // PARA NO MODIFICAR LA CONTRASEÑA EN CASO DE QUE SE ESTE EDITANDO
            /*if(intent.getAction().equals("NUEVO")) {
                // SE QUIERE DAR DE ALTA UN USUARIO NUEVO POR LO QUE SE ESTABLECE UNA CONTRASEÑA POR DEFECTO
                trabajadorBD.put("contraseña","123456");
            }*/
            clienteBD.put("contraseña","123456");

            // REGISTRAR AL USUARIO EN AUTENTICACION Y DARLO DE ALTA EN LA BASE DE DATOS
            registrarUsuario(clienteBD);

            // CERRAR PANTALLA
            finish();
        } else {
            // ALGUN CAMPO NO CONTIENE LO ESPERADO Y SE MUESTRA UN MENSAJE INDICANDOLO
            ventana.setMessage(descripcion);
            ventana.show();
        }
    }

    // REGISTRAR USUARIO EN LA BASE DE DATOS
    private void registrarUsuario(Map<String, Object> clienteBD) {

        // RUTA EN LA QUE SE VA A GUARDAR LA IMAGEN CON EL DNI DEL CLIENTE
        StorageReference imagenRef = referenciaImagenes.child("imagenes/" + clienteBD.get("dni"));

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
                        clienteBD.put("imagen", urlDescarga);

                        // CREAR AL USUARIO EN AUTENTICACION
                        autenticacion.createUserWithEmailAndPassword(clienteBD.get("correo").toString(), clienteBD.get("contraseña").toString())
                                .addOnCompleteListener(AltaModificacionCliente.this, task -> {
                                    if (task.isSuccessful()) {
                                        // SE OBTIENE EL USUARIO AL SER EL REGISTRO EXITOSO
                                        FirebaseUser firebaseUser = autenticacion.getCurrentUser();
                                        if (firebaseUser != null) {
                                            // CREAR EL USUARIO EN LA BASE DE DATOSCrear o actualizar el documento del usuario en Firestore
                                            String userId = firebaseUser.getUid(); // ID DEL USUARIO
                                            db.collection("usuarios").document(userId)
                                                    .set(clienteBD)
                                                    .addOnSuccessListener(aVoid -> {
                                                        // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE DE CONFIRMACION DEL ALTA
                                                        LayoutInflater inflater = getLayoutInflater();
                                                        View layout = inflater.inflate(R.layout.toast_personalizado, null);

                                                        TextView text = layout.findViewById(R.id.toast_text);
                                                        text.setText("Cliente dado de alta ");

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
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE DE ERROR DEL GUARDADO DE LA IMAGEN
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toast_personalizado_error, null);

                TextView text = (TextView) layout.findViewById(R.id.toast_text);
                text.setText("Error al guardar la imagen");

                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });

    }

    // COMPROBAR QUE LA DIRECCION SE PUEDE ENCONTRAR EN GOOGLE MAPS
    private boolean validarDireccion(String direccion, String localidad) {

        // COMPROBAR SI LA DIRECCIÓN CONTIENE AL MENOS UN NÚMERO Y UNA PALABRA
        if (!direccion.matches(".*\\d+.*") || !direccion.matches(".*\\b\\w+\\b.*")) {
            return false;
        }

        // OBTENER EL GEOCODER PARA BUSCAR LA UBICACION
        Geocoder geocoder = new Geocoder(this);
        try {
            // LISTA PARA BUSCAR LA UBICACION DEL CLIENTE
            List<Address> addresses = geocoder.getFromLocationName(direccion+","+ localidad+", España", 1);

            // COMPROBAR QUE SE HA ENCONTRADO LA DIRECCION
            if (!addresses.isEmpty()) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            // MOSTRAR MENSAJE DE ERROR SI NO SE HA PODIDO OBTENER LA DIRECCION
            e.printStackTrace();
        }

        return false;
    }

    // COMPROBAR QUE EL NOMBRE Y LOS APELLIDOS ES TEXTO Y NO HAY NUMEROS
    private boolean validarNombreApellidos(String nombre, String apellido1, String apellido2) {
        // UTILIZAR UNA EXPRESION REGULAR QUE PERMITA SOLO LETRAS Y ESPACIOS EN BLANCO
        String regex = "^[\\p{L} ]+$";
        // COMPROBAR QUE LOS CAMPOS CUMPLEN CON LA EXPRESION REGULAR
        return nombre.matches(regex) && apellido1.matches(regex) && apellido2.matches(regex);
    }

    // COMPROBAR QUE EL CORREO CONTIENE @gmail.com
    private boolean validarCorreo(String correo) {
        return correo.contains("@gmail.com");
    }

    // COMPROBAR QUE EL TELEFONO INTRODUCIDO ES UN NÚMERO ESPAÑOL
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
