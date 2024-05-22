package com.example.cmct.modelo.admo.gestion_clientes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;
import com.example.cmct.clases.Administrador;
import com.example.cmct.clases.Cliente;
import com.example.cmct.clases.Utilidades;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModificacionCliente extends AppCompatActivity {

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
        setContentView(R.layout.admo_modificar_cliente);

        // OBTENER EL ADMINISTRADOR
        obtenerAdministrador();

        foto = findViewById(R.id.imagen);
        nombre = findViewById(R.id.nombre);
        apellido1 = findViewById(R.id.apellido1);
        apellido2 = findViewById(R.id.apellido2);
        correo = findViewById(R.id.correo);
        telefono = findViewById(R.id.telefono);
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


        // OBTENER EL INTENTO CON LOS DATOS QUE ME PASAN
        intent = getIntent();
        // OBTENER EL ID DEL USUARIO
        String idUsuario = intent.getStringExtra("idusuario");

        // OBTENER TODOS LOS DATOS DEL USUARIO DE LA BASE DE DATOS
        db.collection("usuarios").document(idUsuario)
                .get()
                .addOnSuccessListener(documentSnapshot ->  {
                    cliente = documentSnapshot.toObject(Cliente.class);

                    if(cliente != null) {
                        if (cliente.getImagen() != null && !cliente.getImagen().isEmpty()) {
                            // CARGAR LA IMAGEN DESDE FIREBASE STORAGE CON PICASSO
                            Picasso.get()
                                    .load(cliente.getImagen().toString())
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

                        nombre.setText(cliente.getNombre());
                        apellido1.setText(cliente.getApellido1());
                        apellido2.setText(cliente.getApellido2());
                        correo.setText(cliente.getCorreo());
                        telefono.setText(cliente.getTelefono());
                        direccion.setText(cliente.getDireccion());
                        localidades.setSelection(opcionesCiudad.indexOf(cliente.getLocalidad()));

                    } else {
                        Utilidades.mostrarMensajes(this,1,"El cliente está vacío");
                    }
                })
                .addOnFailureListener(e -> {
                    Utilidades.mostrarMensajes(this,1,"Error al recuperar los datos");
                });
    }

    // CLICK EN EL BOTON DE GUARDAR PARA DAR DE ALTA EN LA BASE DE DATOS AL CLIENTE
    public void clickBotonGuardar(View view) {
        // GUARDAR LOS TIPOS DE ERRORES QUE HAY EN LOS CAMPOS
        List<String> errores = new ArrayList<>();

        // COMPROBAR SI SE HA SELECCIONADO UNA IMAGEN
        if(!fotoRellenada) {

            errores.add("- Debes seleccionar una imagen.");

        }

        // COMPROBAR SI TODOS LOS CAMPOS ESTAN RELLENADOS
        if(nombre.getText().toString().isEmpty() || apellido1.getText().toString().isEmpty() || apellido2.getText().toString().isEmpty()
                || correo.getText().toString().isEmpty() || telefono.getText().toString().isEmpty() || direccion.getText().toString().isEmpty()) {

            errores.add("- Todos los campos deben estar rellenados.");

        } else {

            // VALIDAR SI EL NOMBRE Y LOS APELLIDOS NO CONTIENEN DIGITOS
            if(!Utilidades.validarNombreApellidos(nombre.getText().toString(),apellido1.getText().toString(),apellido2.getText().toString())) {

                errores.add("- El nombre o los apellidos solo pueden contener letras");

            }

            // VALIDAR SI EL CORREO ES CORRECTO
            if (!Utilidades.validarCorreo(correo.getText().toString())) {

                errores.add("- El correo no es válido.");

            }

            // VALIDAR SI EL TELEFONO ES CORRECTO
            if (!Utilidades.validarTelefono(telefono.getText().toString())) {

                errores.add("- El teléfono no es válido.");

            }

            if(!Utilidades.validarDireccion(direccion.getText().toString(), localidades.getSelectedItem().toString(),this)) {

                errores.add("- La dirección no es válida.");

            }
        }

        // COMPROBAR QUE LA DESCRIPCION ESTA VACIA PARA DAR DE ALTA AL CLIENTE EN LA BASE DE DATOS
        if(errores.isEmpty()) {

            // GUARDAMOS EL CORREO PARA QUE EN CASO DE CAMBIO NOS PODAMOS AUTENTICAR PARA VERIIFICAR DATOS MAS TARDE
            String correoCliente = cliente.getCorreo();

            // OBTENER TODOS LOS CAMPOS PARA EL CLIENTE
            cliente.setNombre(Utilidades.primeraLetraMayuscula(nombre.getText().toString().trim()));
            cliente.setApellido1(Utilidades.primeraLetraMayuscula(apellido1.getText().toString().trim()));
            cliente.setApellido2(Utilidades.primeraLetraMayuscula(apellido2.getText().toString().trim()));
            cliente.setCorreo(correo.getText().toString().trim());
            cliente.setTelefono(telefono.getText().toString().trim());
            cliente.setLocalidad(localidades.getSelectedItem().toString());
            cliente.setDireccion(Utilidades.primeraLetraMayuscula(direccion.getText().toString()));

            // MOSTRAR MENSAJE DE ESPERA
            Utilidades.esperar(this);
            // ACTUALIZAR CLIENTE
            administrador.editarCliente(cliente,correoCliente,imagenUri,this);

        } else {
            // ALGUN CAMPO NO CONTIENE LO ESPERADO Y SE MUESTRA UN MENSAJE INDICANDOLO
            Utilidades.mostrarMensajes(this,1,String.join("\n", errores));
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
