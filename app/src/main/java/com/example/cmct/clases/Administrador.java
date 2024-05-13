package com.example.cmct.clases;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.cmct.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Administrador extends Usuario implements Serializable {

    // OBTENER LAS INSTANCIAS DE AUTENTICACION Y LA BASE DE DATOS DE FIREBASE
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth autenticacion = FirebaseAuth.getInstance();

    public Administrador() {
        super();
    }

    public Administrador(String imagen, String nombre, String apellido1, String apellido2, String telefono, String dni, String correo, String contrasenia, String rol) {
        super(imagen, nombre, apellido1, apellido2, telefono, dni, correo, contrasenia, rol);
    }

    public void altaTrabajadorAutenticacion(Trabajador trabajador, Activity actividad, Uri imagenUri) {
        // CREAR AL USUARIO EN AUTENTICACION
        autenticacion.createUserWithEmailAndPassword(trabajador.getCorreo(), trabajador.getContrasenia())
                .addOnCompleteListener(actividad, task -> {
                    if (task.isSuccessful()) {

                        // SE OBTIENE EL USUARIO AL SER EL REGISTRO EXITOSO
                        FirebaseUser firebaseUser = task.getResult().getUser();

                        // CERRAR SESION CON EL USUARIO CREADO
                        autenticacion.signOut();

                        if (firebaseUser != null) {

                            // REAUTENTICAR AL ADMINISTRADOR CON EL TOKEN GUARDADO ANTERIORMENTE
                            autenticacion.signInWithEmailAndPassword(this.getCorreo(), this.getContrasenia())
                                    .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                // CREAR EL USUARIO O ACTUALIZARLO EN LA BASE DE DATOS
                                                String idUsuario = firebaseUser.getUid(); // ID DEL USUARIO
                                                subirImagen(idUsuario, imagenUri, trabajador, actividad);
                                            } else {
                                                // ERROR AL REAUTENTICAR AL ADMINISTRADOR
                                                mostrarMensajes(actividad,1,"Error al reautenticar al administrador");
                                            }
                                        }
                                    });
                        }
                    } else {
                        // EL REGISTRO FALLA Y SE INFORMA AL USUARIO
                        mostrarMensajes(actividad,1,"El correo ya existe");
                    }
                });
    }

    private void subirImagen(String idUsuario, Uri imagenUri, Trabajador trabajador, Activity actividad) {
        StorageReference imagenRef = FirebaseStorage.getInstance().getReference().child("imagenes/" + trabajador.getDni());
        imagenRef.putFile(imagenUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imagenRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // URL DE LA IMAGEN SUBIDA
                        String imagenUrl = uri.toString();
                        // REGISTRAR AL USUARIO EN LA BASE DE DATOS
                        registrarEnFirestore(idUsuario, imagenUrl, trabajador, actividad);
                    });
                })
                .addOnFailureListener(e -> {
                    mostrarMensajes(actividad, 1, "Error al subir imagen: " + e.getMessage());
                });
    }

    private void registrarEnFirestore(String idUsuario, String imagenUrl, Trabajador trabajador, Activity actividad) {
        // CREAR UN DOCUMENTO TRABAJADOR
        Map<String, Object> trabajadorBD = new HashMap<>();
        trabajadorBD.put("nombre", trabajador.getNombre());
        trabajadorBD.put("apellido1", trabajador.getApellido1());
        trabajadorBD.put("apellido2", trabajador.getApellido2());
        trabajadorBD.put("dni", trabajador.getDni().toUpperCase());
        trabajadorBD.put("correo", trabajador.getCorreo());
        trabajadorBD.put("telefono", trabajador.getTelefono());
        trabajadorBD.put("contraseña", trabajador.getContrasenia());
        trabajadorBD.put("rol", "trabajador");
        trabajadorBD.put("imagen", imagenUrl);

        db.collection("usuarios").document(idUsuario)
                .set(trabajadorBD)
                .addOnSuccessListener(aVoid -> {
                    // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE DE CONFIRMACION DEL ALTA
                    mostrarMensajes(actividad, 0, "Trabajador dado de alta");

                    // CERRAR PANTALLA
                    actividad.finish();
                })
                .addOnFailureListener(e -> {
                    // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE DE ERROR DEL ALTA
                    mostrarMensajes(actividad,1,"Error al dar el alta");
                });
    }

    public void bajaTrabajadorAutenticacion(DocumentSnapshot snapshot, Activity actividad) {
        db.collection("usuarios").document(snapshot.getId()).
                get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        autenticacion.signInWithEmailAndPassword(task.getResult().getString("correo"),task.getResult().getString("contraseña"))
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        FirebaseUser usuarioAEliminar = task.getResult().getUser();
                                        usuarioAEliminar.delete().addOnCompleteListener(deleteTask -> {
                                            if(deleteTask.isSuccessful()) {
                                                bajaTrabajadorEnFirestore(usuarioAEliminar, actividad);
                                            } else {
                                                mostrarMensajes(actividad,1,"Error al eliminar al trabajador");
                                            }
                                        });
                                    }
                                });
                    }
                });

    }

    private void bajaTrabajadorEnFirestore(FirebaseUser usuarioAEliminar, Activity actividad) {
        // REAUTENTICAR AL ADMINISTRADOR
        autenticacion.signInWithEmailAndPassword(this.getCorreo(), this.getContrasenia())
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // ELIMINAR AL TRABAJADOR EN LA BASE DE DATOS
                            String idUsuarioActual = autenticacion.getCurrentUser().getUid();
                            db.collection("usuarios").document(idUsuarioActual).get().addOnCompleteListener(task1 -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task1.getResult();
                                    // COMPROBAR LOS PERMISOS DEL USUARIO
                                    if (document.exists() && "administrador".equals(document.getString("rol"))) {
                                        // ELIMINAR TRABAJADOR
                                        db.collection("usuarios").document(usuarioAEliminar.getUid())
                                                .delete()
                                                .addOnSuccessListener(documentSnapshot -> {
                                                    mostrarMensajes(actividad, 0, "Trabajador eliminado con éxito");
                                                    actividad.recreate();
                                                })
                                                .addOnFailureListener(e -> {
                                                    mostrarMensajes(actividad, 1, "Error al eliminar trabajador");
                                                });
                                    } else {
                                        // Esconder la opción de eliminar o mostrar un mensaje de error
                                        Log.d("Firestore", "Error getting documents: ", task.getException());
                                    }
                                } else {
                                    Log.d("Firestore", "Error getting documents: ", task.getException());
                                }
                            });
                        } else {
                            // ERROR AL REAUTENTICAR AL ADMINISTRADOR
                            mostrarMensajes(actividad,1,"Error al reautenticar al administrador");
                        }
                    }
                });
    }

    // MOSTRAR TOAST PERSONALIZADOS DE ERRORES Y DE QUE TODO HA IDO CORRECTO
    private void mostrarMensajes(Activity actividad, int tipo, String mensaje) {
        // MENSAJE DE QUE ES CORRECTO
        if(tipo == 0) {
            LayoutInflater inflater = actividad.getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_personalizado, null);

            TextView text = (TextView) layout.findViewById(R.id.toast_text);
            text.setText(mensaje); // CONFIGURAR EL MENSAJE PERSONALIZADO

            Toast toast = new Toast(actividad.getApplicationContext());
            toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 300);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        } else {
            // MENSAJE DE ERRORES
            LayoutInflater inflater = actividad.getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_personalizado_error, null);

            TextView text = (TextView) layout.findViewById(R.id.toast_text);
            text.setText(mensaje); // CONFIGURAR EL MENSAJE DE ERROR PERSONALIZADO

            Toast toast = new Toast(actividad.getApplicationContext());
            toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 300);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }
    }
}
