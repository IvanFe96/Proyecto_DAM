package com.example.cmct;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.modelo.admo.EleccionGestion;
import com.example.cmct.modelo.cliente.EleccionCliente;
import com.example.cmct.modelo.trabajador.EleccionTrabajador;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    Button btnIniciarSesion;

    EditText correo, contrasenia;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        correo = findViewById(R.id.mail);
        contrasenia = findViewById(R.id.password);
        btnIniciarSesion = findViewById(R.id.btnIniciarSesion);
    }

    // CLICK DEL BOTON INICIAR SESION
    public void clickBotonIniciarSesion(View view) {

        // COMPROBAR SI LOS CAMPOS DE TEXTO ESTAN VACIOS
        if(correo.getText().toString().isEmpty() || contrasenia.getText().toString().isEmpty()) {

            // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE
            // DE QUE TIENEN QUE ESTAR TODOS LOS CAMPOS RELLENADOS
            mostrarMensajes(getApplicationContext(),1, "Todos los campos deben estar rellenados");

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
                            mostrarMensajes(getApplicationContext(),1, "Correo o contraseña incorrectos");

                        }
                    });
        }
    }

    private void conocerTipoUsuario(FirebaseUser usuario) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios").document(usuario.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {
                        // COMPROBAR SI EL USUARIO TIENE LA CONTRASEÑA POR DEFECTO
                        if(documentSnapshot.getString("contraseña").equals("123456")) {
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
                        mostrarMensajes(getApplicationContext(),1, "No se han podido obtener los datos del usuario porque no existe en la base de datos");

                    }
                })
                .addOnFailureListener(e -> {

                    // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE
                    // DE QUE HA FALLADO EL TIPO DE USUARIO
                    mostrarMensajes(getApplicationContext(),1, "Error al cargar el tipo de usuario");

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

                                userRef.update("contraseña",nuevaContrasenia.getText().toString())
                                        .addOnSuccessListener(aVoid1 -> {

                                            // SE HA ACTUALIZADO LA CONTRASEÑA CORRECTAMENTE
                                            mostrarMensajes(getApplicationContext(),0,"Contraseña actualizada");
                                            // CERRAR EL DIALOGO
                                            dialogo.dismiss();

                                        }).addOnFailureListener(e -> mostrarMensajes(getApplicationContext(), 1,"Error al actualizar la contraseña"));

                            })
                            .addOnFailureListener(e -> mostrarMensajes(getApplicationContext(), 1, "Error al actualizar la contraseña"));
                } else {
                    // MOSTRAR ERROR
                    mostrarMensajes(getApplicationContext(),1,"Las contraseñas no coinciden");
                }
            } else {
                mostrarMensajes(getApplicationContext(), 1, "La contraseña no es válida");
            }
        });

        dialogo.show();
    }

    // COMPROBAR QUE LA CONTRASEÑA TIENE UNA LONGITUD DE 6 O MAS
    private boolean esValida(String contrasenia) {
        return contrasenia != null && contrasenia.length() >= 6;
    }

    // SELECCIONAR LA SIGUIENTE VENTANA A MOSTRAR SEGUN EL TIPO DE USUARIO QUE SEA
    private void seleccionNavegacion(String tipoUsuario, String nombreUsuario) {
        if ("trabajador".equals(tipoUsuario)) {

            mostrarMensajes(getApplicationContext(), 0, "Bienvenido/a " + nombreUsuario);
            //LANZA UNA ACTIVIDAD PARA TRABAJADORES
            startActivity(new Intent(this, EleccionTrabajador.class));

        } else if ("cliente".equals(tipoUsuario)) {

            mostrarMensajes(getApplicationContext(), 0, "Bienvenido/a " + nombreUsuario);
            // LANZA UNA ACTIVIDAD PARA CLIENTES
            startActivity(new Intent(this, EleccionCliente.class));

        } else if ("administrador".equals(tipoUsuario)) {

            mostrarMensajes(getApplicationContext(), 0, "Bienvenido/a " + nombreUsuario);
            // LANZA UNA ACTIVIDAD PARA ADMINISTRADORES
            startActivity(new Intent(this, EleccionGestion.class));

        } else {

            // MANEJAO DE OTROS CASOS
            mostrarMensajes(getApplicationContext(),1, "Tipo de usuario desconocido");

        }

        finish();
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

}