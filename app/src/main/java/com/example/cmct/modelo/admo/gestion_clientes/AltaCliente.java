package com.example.cmct.modelo.admo.gestion_clientes;

import android.content.Context;
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
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;
import com.example.cmct.clases.Administrador;
import com.example.cmct.clases.Cliente;
import com.example.cmct.clases.Utilidades;
import com.example.cmct.modelo.admo.gestion_trabajadores.AltaTrabajador;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AltaCliente extends AppCompatActivity {

    private static final int REQUEST_IMAGEN = 100;

    Intent intent;
    EditText nombre, apellido1, apellido2, correo, telefono, dni, direccion;
    Spinner localidades;
    ImageView foto;
    boolean fotoRellenada = false;
    Button botonGuardar;
    Cliente cliente;
    Administrador administrador;

    // OBTENER LA INSTANCIA DE  LA BASE DE DATOS DE FIREBASE
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // URI DE LA IMAGEN DEL CLIENTE
    Uri imagenUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_alta_cliente);

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

        // OBTENER EL ADMINISTRADOR
        obtenerAdministrador();

        // INICIALIZAR TRABAJADOR NUEVO
        cliente = new Cliente();

        // RELLENAR EL DESPLEGABLE DE CIUDADES
        List<String> opcionesCiudad = Arrays.asList("Zuera", "San Mateo de Gállego", "Villanueva de Gállego", "Ontinar del Salz");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opcionesCiudad);
        // ESPECIFICAR EL DISEÑO QUE SE UTILIZARA CUANDO SE MUESTREN LAS OPCIONES
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // ESTABLECER EL ADAPTADOR AL DESPLEGABLE
        localidades.setAdapter(adapter);

    }

    // CLICK EN EL BOTON DE GUARDAR PARA DAR DE ALTA EN LA BASE DE DATOS AL CLIENTE
    public void clickBotonGuardar(View view) {
        // GUARDAR LOS TIPOS DE ERRORES QUE HAY EN LOS CAMPOS
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
            cliente.setNombre(Utilidades.primeraLetraMayuscula(nombre.getText().toString().trim()));
            cliente.setApellido1(Utilidades.primeraLetraMayuscula(apellido1.getText().toString().trim()));
            cliente.setApellido2(Utilidades.primeraLetraMayuscula(apellido2.getText().toString().trim()));
            cliente.setCorreo(correo.getText().toString().trim());
            cliente.setTelefono(telefono.getText().toString().trim());
            cliente.setDni(dni.getText().toString().toUpperCase().trim());
            cliente.setDireccion(Utilidades.primeraLetraMayuscula(direccion.getText().toString()));
            cliente.setTrabajadorAsignado(null);
            cliente.setHoraEntradaTrabajador(null);
            cliente.setHoraSalidaTrabajador(null);
            cliente.setNecesidades(null);
            cliente.setRol("cliente");
            cliente.setContrasenia("123456");
            cliente.setLocalidad(localidades.getSelectedItem().toString());

            // MOSTRAR MENSAJE DE ESPERA
            Utilidades.esperar(this);
            // VERIFICAR QUE EL DNI NO ESTA EN LA BASE DE DATOS PARA DAR DE ALTA AL CLIENTE
            verificarDniExistente(cliente);

        } else {
            // ALGUN CAMPO NO CONTIENE LO ESPERADO Y SE MUESTRA UN MENSAJE INDICANDOLO
            Utilidades.mostrarMensajes(this,1,descripcion);
        }
    }

    // VERIFICAR QUE EL DNI NO EXISTE EN LA BASE DE DATOS
    private void verificarDniExistente(Cliente cliente) {
        db.collection("usuarios")
                .whereEqualTo("dni", cliente.getDni())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                // DNI EXISTE SE MUESTRA POR PANTALLA
                                Utilidades.mostrarMensajes(AltaCliente.this, 1, "El DNI ya está registrado en la base de datos");
                            } else {
                                // DNI NO EXISTE, REGISTRAR AL USUARIO
                                administrador.altaClienteAutenticacion(cliente, AltaCliente.this, imagenUri);
                            }
                        } else {
                            Utilidades.mostrarMensajes(AltaCliente.this, 1, "Error al verificar el DNI");
                        }
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
