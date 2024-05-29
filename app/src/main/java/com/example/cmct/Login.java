package com.example.cmct;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.clases.Utilidades;
import com.example.cmct.modelo.admo.EleccionGestion;
import com.example.cmct.modelo.admo.gestion_trabajadores.AltaTrabajador;
import com.example.cmct.modelo.cliente.EleccionCliente;
import com.example.cmct.modelo.trabajador.EleccionTrabajador;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import okhttp3.internal.Util;

public class Login extends AppCompatActivity {

    Button btnIniciarSesion;
    ImageView imagenOjo;

    EditText correo, contrasenia;

    boolean contraseniaVisible = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        correo = findViewById(R.id.mail);
        contrasenia = findViewById(R.id.password);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
        imagenOjo = findViewById(R.id.imagenOcultarMostrarContra);
    }

    // CLICK DEL BOTON INICIAR SESION
    public void clickBotonIniciarSesion(View view) {
        // MOSTRAR MENSAJE DE ESPERA
        Utilidades.esperar(this);

        // COMPROBAR SI LOS CAMPOS DE TEXTO ESTAN VACIOS
        if(correo.getText().toString().isEmpty() || contrasenia.getText().toString().isEmpty()) {
            // CERRAR EL DIALOGO DE ESPERA
            Utilidades.cerrarEspera();
            // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE
            // DE QUE TIENEN QUE ESTAR TODOS LOS CAMPOS RELLENADOS
            Utilidades.mostrarMensajes(this,1, "Todos los campos deben estar rellenados");

        } else {
            // INTENTAR LOGEARSE
            FirebaseAuth autenticacion = FirebaseAuth.getInstance();

            autenticacion.signInWithEmailAndPassword(correo.getText().toString(), contrasenia.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {

                            // LOGIN EXITOSO Y OBTENER EL USUARIO
                            FirebaseUser usuario = autenticacion.getCurrentUser();

                            if (usuario != null) {
                                // CONSULTAR EN LA BASE DE DATOS PARA OBTENER LA INFORMACION DEL USUARIO
                                conocerTipoUsuario(usuario);
                            }

                        } else {

                            // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE
                            // DE QUE EL CORREO O LA CONTRASEÑA SON INCORRECTOS
                            Utilidades.mostrarMensajes(this,1, "Correo o contraseña incorrectos");
                            // CERRAR EL DIALOGO DE ESPERA
                            Utilidades.cerrarEspera();
                        }
                    });
        }
    }

    // CLICK EN LA IMAGEN DEL OJO PARA MOSTRAR U OCULTAR LA CONTRASEÑA
    public void clickMostrarOcultarContrasenia(View view) {
        // COMPROBAR SI LA CONTRASEÑA ES VISIBLE O NO
        if (!contraseniaVisible) {
            // MOSTRAR LA CONTRASEÑA
            contrasenia.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
            contraseniaVisible = true;
            imagenOjo.setImageResource(R.drawable.ver_contrasenia);

        } else {
            // OCULTAR LA CONTRASEÑA
            contrasenia.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            contraseniaVisible = false;
            imagenOjo.setImageResource(R.drawable.esconder_contrasenia);
        }
    }
    private void conocerTipoUsuario(FirebaseUser usuario) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios").document(usuario.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {
                        // COMPROBAR SI EL USUARIO TIENE LA CONTRASEÑA POR DEFECTO
                        if(documentSnapshot.getString("contrasenia").equals("123456")) {
                            // CERRAR EL DIALOGO DE ESPERA
                            Utilidades.cerrarEspera();
                            // SE MUESTRA UN DIALOGO PARA CAMBIAR LA CONTRASEÑA
                            mostrarDialogoCambioContrasenia(usuario);
                        } else {
                            // EL USUARIO YA TIENE SU PROPIA CONTRASEÑA
                            String nombreUsuario = documentSnapshot.getString("nombre");
                            String tipoUsuario = documentSnapshot.getString("rol");
                            seleccionNavegacion(tipoUsuario,nombreUsuario);
                        }

                    } else {

                        // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE
                        // DE QUE NO SE PUEDEN OBTENER LOS DATOS
                        Utilidades.mostrarMensajes(this,1, "No se han podido obtener los datos del usuario porque no existe en la base de datos");

                    }
                })
                .addOnFailureListener(e -> {

                    // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE
                    // DE QUE HA FALLADO EL TIPO DE USUARIO
                    Utilidades.mostrarMensajes(this,1, "Error al cargar el tipo de usuario");

                });
    }

    // MOSTRAR EL DIALOGO DE CAMBIO DE CONTRASEÑA DEL USUARIO
    private void mostrarDialogoCambioContrasenia(FirebaseUser usuario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflador = this.getLayoutInflater();
        View dialogView = inflador.inflate(R.layout.dialogo_cambio_contrasenia, null);
        builder.setView(dialogView);

        EditText nuevaContrasenia = dialogView.findViewById(R.id.etNewPassword);
        EditText repetirContrasenia = dialogView.findViewById(R.id.etConfirmPassword);
        Button btnCambioContrasenia = dialogView.findViewById(R.id.btnChangePassword);

        builder.setTitle("CAMBIO DE CONTRASEÑA");

        AlertDialog dialogo = builder.create();

        btnCambioContrasenia.setOnClickListener(view -> {
            // COMPROBAR QUE LA CONTRASEÑA ES VALIDA
            if(esValida(nuevaContrasenia.getText().toString()) && esValida(repetirContrasenia.getText().toString())) {

                // COMRPOBAR QUE LAS CONTRASEÑAS COINCIDEN
                if (nuevaContrasenia.getText().toString().equals(repetirContrasenia.getText().toString())) {

                    // CAMBIAR LA CONTRASEÑA EN AUTENTICACION
                    usuario.updatePassword(nuevaContrasenia.getText().toString())
                            .addOnSuccessListener(aVoid -> {
                                // CONTRASEÑA CAMBIADA CON EXITO EN AUTENTICACION
                                // SE ACTUALIZA LA CONTRASEÑA EN LA BASE DE DATOS
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                DocumentReference userRef = db.collection("usuarios").document(usuario.getUid());

                                userRef.update("contrasenia",nuevaContrasenia.getText().toString())
                                        .addOnSuccessListener(aVoid1 -> {

                                            // SE HA ACTUALIZADO LA CONTRASEÑA CORRECTAMENTE
                                            Utilidades.mostrarMensajes(this,0,"Contraseña actualizada.\nIntroduce tu nueva contraseña");
                                            // CERRAR EL DIALOGO
                                            dialogo.dismiss();

                                        }).addOnFailureListener(e -> Utilidades.mostrarMensajes(this, 1,"Error al actualizar la contraseña"));

                            })
                            .addOnFailureListener(e -> Utilidades.mostrarMensajes(this, 1, "Error al actualizar la contraseña"));
                } else {
                    // MOSTRAR ERROR
                    Utilidades.mostrarMensajes(this,1,"Las contraseñas no coinciden");
                }
            } else {
                Utilidades.mostrarMensajes(this, 1, "La contraseña no es válida");
            }
        });

        dialogo.show();
    }

    // COMPROBAR QUE LA CONTRASEÑA TIENE UNA LONGITUD DE 6 O MAS
    private boolean esValida(String contrasenia) {
        return contrasenia != null && contrasenia.length() >= 6 && !contrasenia.equals("123456");
    }

    // SELECCIONAR LA SIGUIENTE VENTANA A MOSTRAR SEGUN EL TIPO DE USUARIO QUE SEA
    private void seleccionNavegacion(String tipoUsuario, String nombreUsuario) {
        // CERRAR EL DIALOGO DE ESPERA
        Utilidades.cerrarEspera();

        if ("trabajador".equals(tipoUsuario)) {

            Utilidades.mostrarMensajes(this, 2, "Bienvenido/a " + nombreUsuario);
            //LANZA UNA ACTIVIDAD PARA TRABAJADORES
            startActivity(new Intent(this, EleccionTrabajador.class));

        } else if ("cliente".equals(tipoUsuario)) {

            Utilidades.mostrarMensajes(this, 2, "Bienvenido/a " + nombreUsuario);
            // LANZA UNA ACTIVIDAD PARA CLIENTES
            startActivity(new Intent(this, EleccionCliente.class));

        } else if ("administrador".equals(tipoUsuario)) {

            Utilidades.mostrarMensajes(this, 2, "Bienvenido/a " + nombreUsuario);
            // LANZA UNA ACTIVIDAD PARA ADMINISTRADORES
            startActivity(new Intent(this, EleccionGestion.class));

        } else {

            // MANEJAO DE OTROS CASOS
            Utilidades.mostrarMensajes(this,1, "Tipo de usuario desconocido");
            // CERRAR EL DIALOGO DE ESPERA
            Utilidades.cerrarEspera();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Limpiar los campos de texto cada vez que la actividad se reanuda
        if (correo != null && contrasenia != null) {
            correo.setText("");
            contrasenia.setText("");
        }

        // Restablecer cualquier estado o configuración que pueda haberse modificado durante la sesión anterior
        contraseniaVisible = false;  // Restablecer la visibilidad de la contraseña
        imagenOjo.setImageResource(R.drawable.esconder_contrasenia);  // Restablecer el ícono del ojo

        // Opcional: Si manejas sesiones o caches que deben limpiarse
        FirebaseAuth.getInstance().signOut(); // Asegurar que la sesión está cerrada
    }
}