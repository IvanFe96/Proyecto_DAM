package com.example.cmct;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.modelo.admo.EleccionGestion;
import com.example.cmct.modelo.cliente.EleccionCliente;
import com.example.cmct.modelo.trabajador.EleccionTrabajador;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_personalizado_error, null);

            TextView text = (TextView) layout.findViewById(R.id.toast_text);
            text.setText("Todos los campos deben estar rellenados");

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();

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
                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.toast_personalizado_error, null);

                            TextView text = (TextView) layout.findViewById(R.id.toast_text);
                            text.setText("Correo o contraseña incorrectos");

                            Toast toast = new Toast(getApplicationContext());
                            toast.setGravity(Gravity.BOTTOM, 0, 0);
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setView(layout);
                            toast.show();
                        }
                    });
        }
    }

    private void conocerTipoUsuario(FirebaseUser usuario) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(usuario.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String tipoUsuario = documentSnapshot.getString("rol");
                        seleccionNavegacion(tipoUsuario);
                    } else {
                        // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE
                        // DE QUE NO SE PUEDEN OBTENER LOS DATOS 
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_personalizado_error, null);

                        TextView text = (TextView) layout.findViewById(R.id.toast_text);
                        text.setText("Correo o contraseña incorrectos");

                        Toast toast = new Toast(getApplicationContext());
                        toast.setGravity(Gravity.BOTTOM, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
                    }
                })
                .addOnFailureListener(e -> {
                    // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE
                    // DE QUE HA FALLADO EL TIPO DE USUARIO
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_personalizado_error, null);

                    TextView text = (TextView) layout.findViewById(R.id.toast_text);
                    text.setText("Error al cargar el tipo de usuario");

                    Toast toast = new Toast(getApplicationContext());
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                });
    }

    private void seleccionNavegacion(String tipoUsuario) {
        if ("trabajador".equals(tipoUsuario)) {
            //LANZA UNA ACTIVIDAD PARA TRABAJADORES
            startActivity(new Intent(this, EleccionTrabajador.class));
        } else if ("cliente".equals(tipoUsuario)) {
            // LANZA UNA ACTIVIDAD PARA CLIENTES
            startActivity(new Intent(this, EleccionCliente.class));
        } else if ("administrador".equals(tipoUsuario)) {
            // LANZA UNA ACTIVIDAD PARA ADMINISTRADORES
            startActivity(new Intent(this, EleccionGestion.class));
        } else {
            // MANEJAO DE OTROS CASOS
            Toast.makeText(this, "Tipo de usuario desconocido", Toast.LENGTH_SHORT).show();
        }

        finish();
    }

}