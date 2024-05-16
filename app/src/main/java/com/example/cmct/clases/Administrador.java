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
import com.google.firebase.firestore.QuerySnapshot;
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

    // DAR DE ALTA UN TRABAJADOR EN AUTENTICACION
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
                                                subirImagenTrabajador(idUsuario, imagenUri, trabajador, actividad);
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

    // SUBIR LA IMAGEN DEL TRABAJADOR AL ALMACENAMIENTO DE IMAGENES
    private void subirImagenTrabajador(String idUsuario, Uri imagenUri, Trabajador trabajador, Activity actividad) {
        StorageReference imagenRef = FirebaseStorage.getInstance().getReference().child("imagenes/" + trabajador.getDni());
        imagenRef.putFile(imagenUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imagenRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // URL DE LA IMAGEN SUBIDA
                        String imagenUrl = uri.toString();
                        // REGISTRAR AL USUARIO EN LA BASE DE DATOS
                        registrarTrabajadorEnFirestore(idUsuario, imagenUrl, trabajador, actividad);
                    });
                })
                .addOnFailureListener(e -> {
                    mostrarMensajes(actividad, 1, "Error al subir imagen: " + e.getMessage());
                });
    }

    // DAR DE ALTA A UN TRABAJADOR EN LA BASE DE DATOS
    private void registrarTrabajadorEnFirestore(String idUsuario, String imagenUrl, Trabajador trabajador, Activity actividad) {
        // CREAR UN DOCUMENTO TRABAJADOR
        Map<String, Object> trabajadorBD = new HashMap<>();
        trabajadorBD.put("nombre", trabajador.getNombre());
        trabajadorBD.put("apellido1", trabajador.getApellido1());
        trabajadorBD.put("apellido2", trabajador.getApellido2());
        trabajadorBD.put("dni", trabajador.getDni().toUpperCase());
        trabajadorBD.put("correo", trabajador.getCorreo());
        trabajadorBD.put("telefono", trabajador.getTelefono());
        trabajadorBD.put("contrasenia", trabajador.getContrasenia());
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

    // ELIMINAR AL TRABAJADOR DE AUTENTICACION
    public void bajaTrabajadorAutenticacion(DocumentSnapshot snapshot, Activity actividad) {
        db.collection("usuarios").document(snapshot.getId()).
                get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        autenticacion.signInWithEmailAndPassword(task.getResult().getString("correo"),task.getResult().getString("contrasenia"))
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        FirebaseUser usuarioAEliminar = task.getResult().getUser();
                                        usuarioAEliminar.delete().addOnCompleteListener(deleteTask -> {
                                            if(deleteTask.isSuccessful()) {
                                                eliminarImagenTrabajadorDeStorage(snapshot, actividad);
                                                eliminarIncidenciasTrabajador(snapshot,actividad);
                                                bajaTrabajadorEnFirestore(usuarioAEliminar, actividad);
                                                eliminarFichajesTrabajador(snapshot,actividad);
                                            } else {
                                                mostrarMensajes(actividad,1,"Error al eliminar al trabajador");
                                            }
                                        });
                                    }
                                });
                    }
                });

    }

    // ELIMINAR LA IMAGEN DEL TRABAJADOR DEL ALMACENAMIENTO DE IMAGENES
    public void eliminarImagenTrabajadorDeStorage(DocumentSnapshot snapshot, Activity actividad) {
        Trabajador trabajador = snapshot.toObject(Trabajador.class);
        if (trabajador.getDni() != null && !trabajador.getDni().isEmpty()) {
            // OBTENER LA REFERENCIA A LA IMAGEN EN STORAGE
            StorageReference imagenRef = FirebaseStorage.getInstance().getReference().child("imagenes/" + trabajador.getDni());

            // ELIMINAR LA IMAGEN
            imagenRef.delete().addOnSuccessListener(aVoid -> {

            }).addOnFailureListener(e -> {
                // MOSTRAR ERROR SI LA ELIMINACION FALLA
                mostrarMensajes(actividad, 1, "Error al eliminar imagen: " + e.getMessage());
            });
        } else {
            mostrarMensajes(actividad, 1, "No se encontró un DNI válido para eliminar la imagen.");
        }
    }

    // ELIMINAR AL TRABAJADOR EN LA BASE DE DATOS
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
                                        //  ERROR POR FALTA DE PERMISOS O EL USUARIO NO EXISTE
                                        mostrarMensajes(actividad,1,"No existe usuario o no tienes permisos suficientes");
                                    }
                                } else {
                                    // ERROR AL ELIMINAR AL USUARIO
                                    mostrarMensajes(actividad,1,"Error al eliminar al usuario");
                                }
                            });
                        } else {
                            // ERROR AL REAUTENTICAR AL ADMINISTRADOR
                            mostrarMensajes(actividad,1,"Error al reautenticar al administrador");
                        }
                    }
                });
    }

    // ELIMINAR LAS INCIDENCIAS QUE TENGA EL TRABAJADOR
    private void eliminarIncidenciasTrabajador(DocumentSnapshot snapshot, Activity actividad) {
        // OBTENER LOS DATOS DEL TRABAJADOR PARA UTILIZARLOS EN LA CONSULTA SIGUIENTE
        Trabajador trabajador = snapshot.toObject(Trabajador.class);

        // REAUTENTICAR AL ADMINISTRADOR
        autenticacion.signInWithEmailAndPassword(this.getCorreo(), this.getContrasenia())
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // ACCEDER A LA COLECCION DE INCIDENCIAS
                            db.collection("incidencias")
                                    .whereEqualTo("dni",trabajador.getDni())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                // RECORRER TODAS LAS INCIDENCIAS QUE TENGA ESE TRABAJADOR PARA ELIMINARLAS
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    db.collection("incidencias").document(document.getId()).delete()
                                                            .addOnSuccessListener(aVoid ->{})
                                                            .addOnFailureListener(e -> mostrarMensajes(actividad, 1, "Error al eliminar una incidencia"));
                                                }
                                            } else {
                                                mostrarMensajes(actividad, 1, "Error al obtener las incidencias para eliminar.");
                                            }
                                        }
                                    });
                        } else {
                            // ERROR AL REAUTENTICAR AL ADMINISTRADOR
                            mostrarMensajes(actividad,1,"Error al reautenticar al administrador");
                        }
                    }
                    });
    }

    // ELIMINAR LOS FICHAJES QUE TENGA EL TRABAJADOR
    private void eliminarFichajesTrabajador(DocumentSnapshot snapshot, Activity actividad) {
        // OBTENER LOS DATOS DEL TRABAJADOR PARA UTILIZARLOS EN LA CONSULTA SIGUIENTE
        Trabajador trabajador = snapshot.toObject(Trabajador.class);

        // REAUTENTICAR AL ADMINISTRADOR
        autenticacion.signInWithEmailAndPassword(this.getCorreo(), this.getContrasenia())
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // ACCEDER A LA COLECCION DE INCIDENCIAS
                            db.collection("fichajes")
                                    .whereEqualTo("dni",trabajador.getDni())
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                // RECORRER TODAS LAS INCIDENCIAS QUE TENGA ESE TRABAJADOR PARA ELIMINARLAS
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    db.collection("incidencias").document(document.getId()).delete()
                                                            .addOnSuccessListener(aVoid ->{})
                                                            .addOnFailureListener(e -> mostrarMensajes(actividad, 1, "Error al eliminar una incidencia"));
                                                }
                                            } else {
                                                mostrarMensajes(actividad, 1, "Error al obtener las incidencias para eliminar.");
                                            }
                                        }
                                    });
                        } else {
                            // ERROR AL REAUTENTICAR AL ADMINISTRADOR
                            mostrarMensajes(actividad,1,"Error al reautenticar al administrador");
                        }
                    }
                });
    }

    // EDITAR LOS DATOS DEL TRABAJADOR
    public void editarTrabajador(Trabajador trabajador, String correoTrabajador, Uri imagenUri, Activity actividad) {

        // ACTUALIZAR LOS DATOS
        Map<String, Object> datosActualizados = new HashMap<>();
        datosActualizados.put("nombre", trabajador.getNombre());
        datosActualizados.put("apellido1", trabajador.getApellido1());
        datosActualizados.put("apellido2", trabajador.getApellido2());
        datosActualizados.put("telefono", trabajador.getTelefono());

        // COMPROBAR SI SE HA CAMBIADO LA IMAGEN
        if(imagenUri != null) {
            // LA IMAGEN ES DIFERENTE A LA DE ANTES
            actualizarImagenTrabajador(imagenUri, trabajador, actividad, new Callback<String>() {
                @Override
                public void onSuccess(String imagenUrl) {
                    datosActualizados.put("imagen",imagenUrl); // INCLUIR LA NUEVA IMAGEN
                }

                @Override
                public void onFailure(Exception e) {
                    mostrarMensajes(actividad, 1, "Error al actualizar la imagen en Firestore: " + e.getMessage());
                }
            });
        }

        // INICIAR SESION CON EL TRABAJADOR EN AUTENTICACION PARA MODIFICAR EL CORREO EN CASO DE QUE SE HAYA CAMBIADO
        autenticacion.signInWithEmailAndPassword(correoTrabajador, trabajador.getContrasenia())
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // COMPROBAR SI EL CORREO ES DIFERENTE Y SI ES ASI ACTUALIZARLO
                            FirebaseUser usuario = autenticacion.getCurrentUser();
                            if (usuario != null && !usuario.getEmail().equals(trabajador.getCorreo())) {
                                // ENVIAMOS AL USUARIO UN CORREO DE VERIFICACION PARA COMPROBAR QUE EL USUARIO TIENE ACCESO AL NUEVO CORREO
                                usuario.verifyBeforeUpdateEmail(trabajador.getCorreo())
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                datosActualizados.put("correo",trabajador.getCorreo());
                                                // REAUTENTICAR AL ADMINISTRADOR
                                                autenticacion.signInWithEmailAndPassword(Administrador.this.getCorreo(), Administrador.this.getContrasenia())
                                                        .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if (task.isSuccessful()) {
                                                                    actualizarTrabajadorFirestore(usuario.getUid(), datosActualizados, actividad);
                                                                } else {
                                                                    // ERROR AL REAUTENTICAR AL ADMINISTRADOR
                                                                    mostrarMensajes(actividad,1,"Error al reautenticar al administrador");
                                                                }
                                                            }
                                                        });
                                            } else {
                                                mostrarMensajes(actividad, 1, "Error al actualizar el correo electrónico: " + task1.getException().getMessage());
                                            }
                                        });
                            } else {
                                actualizarTrabajadorFirestore(usuario.getUid(), datosActualizados, actividad);
                            }

                        } else {
                            // ERROR AL REAUTENTICAR AL TRABAJADOR
                            mostrarMensajes(actividad,1,"Error al reautenticar al trabajador");
                        }
                    }
                });

    }

    // ACTUALIZAR LOS DATOS DEL TRABAJADOR EN LA BASE DE DATOS
    private void actualizarTrabajadorFirestore(String idUsuario, Map<String,Object> datosActualizados, Activity actividad) {
        db.collection("usuarios").document(idUsuario)
                .update(datosActualizados)
                .addOnSuccessListener(aVoid -> {
                    // MOSTRAR MENSAJE DE EXITO
                    mostrarMensajes(actividad, 0, "Datos actualizados con éxito.");

                    // CERRAR PANTALLA
                    actividad.finish();
                })
                .addOnFailureListener(e -> mostrarMensajes(actividad, 1, "Error al actualizar datos: " + e.getMessage()));
    }

    // ACTUALIZAR LA IMAGEN DEL TRABAJADOR
    private String actualizarImagenTrabajador(Uri imagenUri,Trabajador trabajador, Activity actividad, Callback<String> callback) {
        StorageReference imagenRef = FirebaseStorage.getInstance().getReference().child("imagenes/" + trabajador.getDni());
        imagenRef.putFile(imagenUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imagenRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        callback.onSuccess(uri.toString());  // USAR CALLBACK PARA DEVOLVER LA URL
                    });
                })
                .addOnFailureListener(e -> {
                    mostrarMensajes(actividad, 1, "Error al subir imagen: " + e.getMessage());
                    callback.onFailure(e);
                });

        return "";
    }

    // DAR DE ALTA UN CLIENTE EN AUTENTICACION
    public void altaClienteAutenticacion(Cliente cliente, Activity actividad, Uri imagenUri) {
        // CREAR AL USUARIO EN AUTENTICACION
        autenticacion.createUserWithEmailAndPassword(cliente.getCorreo(), cliente.getContrasenia())
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
                                                subirImagenCliente(idUsuario, imagenUri, cliente, actividad);
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

    // SUBIR LA IMAGEN DEL CLIENTE AL ALMACENAMIENTO DE IMAGENES
    private void subirImagenCliente(String idUsuario, Uri imagenUri, Cliente cliente, Activity actividad) {
        StorageReference imagenRef = FirebaseStorage.getInstance().getReference().child("imagenes/" + cliente.getDni());
        imagenRef.putFile(imagenUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imagenRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // URL DE LA IMAGEN SUBIDA
                        String imagenUrl = uri.toString();
                        cliente.setImagen(imagenUrl);
                        // REGISTRAR AL USUARIO EN LA BASE DE DATOS
                        registrarClienteEnFirestore(idUsuario, cliente, actividad);
                    });
                })
                .addOnFailureListener(e -> {
                    mostrarMensajes(actividad, 1, "Error al subir imagen: " + e.getMessage());
                });
    }

    // DAR DE ALTA A UN CLIENTE EN LA BASE DE DATOS
    private void registrarClienteEnFirestore(String idUsuario, Cliente cliente, Activity actividad) {
        db.collection("usuarios").document(idUsuario)
                .set(cliente)
                .addOnSuccessListener(aVoid -> {
                    // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE DE CONFIRMACION DEL ALTA
                    mostrarMensajes(actividad, 0, "Cliente dado de alta");

                    // CERRAR PANTALLA
                    actividad.finish();
                })
                .addOnFailureListener(e -> {
                    // MOSTRAR UN TOAST PERSONALIZADO MOSTRANDO UN MENSAJE DE ERROR DEL ALTA
                    mostrarMensajes(actividad,1,"Error al dar el alta");
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

    // INTERFAZ PARA DEVOLVER DATOS
    private interface Callback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }

}
