package com.example.cmct.modelo.admo.gestion_trabajadores;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cmct.R;
import com.example.cmct.clases.Administrador;
import com.example.cmct.clases.Cliente;
import com.example.cmct.clases.Trabajador;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AsignarTrabajo extends AppCompatActivity {

    Intent intent;

    TextView nombreTrabajador;

    Spinner[] desplegables;

    TextView[] horarios;

    Administrador administrador;

    Trabajador trabajador;

    // OBTENER LA INSTANCIA DE LA BASE DE DATOS DE FIREBASE
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admo_asignar_trabajo);

        // DATOS DEL ADMINISTRADOR
        obtenerAdministrador();

        desplegables = new Spinner[6];
        desplegables[0] = findViewById(R.id.spinner1AsignarTrabajo);
        desplegables[1] = findViewById(R.id.spinner2AsignarTrabajo);
        desplegables[2] = findViewById(R.id.spinner3AsignarTrabajo);
        desplegables[3] = findViewById(R.id.spinner4AsignarTrabajo);
        desplegables[4] = findViewById(R.id.spinner5AsignarTrabajo);
        desplegables[5] = findViewById(R.id.spinner6AsignarTrabajo);

        horarios = new TextView[12];
        horarios[0] = findViewById(R.id.textClock1AsignarTrabajo);
        horarios[1] = findViewById(R.id.textClock2AsignarTrabajo);
        horarios[2] = findViewById(R.id.textClock3AsignarTrabajo);
        horarios[3] = findViewById(R.id.textClock4AsignarTrabajo);
        horarios[4] = findViewById(R.id.textClock5AsignarTrabajo);
        horarios[5] = findViewById(R.id.textClock6AsignarTrabajo);
        horarios[6] = findViewById(R.id.textClock7AsignarTrabajo);
        horarios[7] = findViewById(R.id.textClock8AsignarTrabajo);
        horarios[8] = findViewById(R.id.textClock9AsignarTrabajo);
        horarios[9] = findViewById(R.id.textClock10AsignarTrabajo);
        horarios[10] = findViewById(R.id.textClock11AsignarTrabajo);
        horarios[11] = findViewById(R.id.textClock12AsignarTrabajo);

        nombreTrabajador = findViewById(R.id.tvNombreApellidosAsignarTrabajo);

        intent = getIntent();
        String dniTrabajador = intent.getStringExtra("trabajador");

        // OBTENER EL TRABAJADOR QUE SE HA PASADO
        db.collection("usuarios").
                whereEqualTo("dni",dniTrabajador)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        // GUARDAR AL TRABAJADOR EN LA VARIABLE
                        trabajador = task.getResult().getDocuments().get(0).toObject(Trabajador.class);

                        // COMPROBAR SI LA ACTIVIDAD ESTA INICIADA DESDE LA LISTA DE TRABAJADORES QUE NO TIENEN ASIGNADOS CLIENTES
                        if(intent.getAction().equals("NUEVO")) {

                            // ESTABLECER EL NOMBRE DEL TRABAJADOR DEL CUAL HEMOS SELECCIONADO EN LA PANTALLA ANTERIOR
                            nombreTrabajador.setText(trabajador.getNombre() + " " + trabajador.getApellido1() + " " + trabajador.getApellido2());

                            // RELLENAR LOS DESPLEGABLES CON LOS CLIENTES QUE NO HAN SIDO ASIGNADOS
                            cargarClientesSinTrabajadorAsignado();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mostrarMensajes(getApplicationContext(),1,"Error al cargar el trabajador");
                    }
                });


    }

    // CLICK EN BOTON ACEPTAR PARA QUE REGISTRE EN LA BASE DE DATOS LOS DATOS RECOGIDOS
    // EN LOS DESPLEGABLES Y EN LOS HORARIOS
    public void clickBotonAceptarAsignarTrabajo(View v) {
        Map<Cliente, HorarioCliente> clientesSeleccionados = new HashMap<>();

        for (int i = 0; i < desplegables.length; i++) {
            Cliente cliente = (Cliente) desplegables[i].getSelectedItem(); // OBTENER EL CLIENTE SELECCIONADO
            String horaInicio = horarios[2 * i].getText().toString();  // OBTENER LA HORA DE INICIO
            String horaFin = horarios[2 * i + 1].getText().toString();  // OBTENER LA HORA DE FINAL

            // COMPROBAR SI EL CLIENTE ESTA EN ALGUN DESPLEGABLE MAS PARA CAMBIAR LA HORA DE FIN POR LA DE MAS TARDE
            if (!clientesSeleccionados.containsKey(cliente)) {
                clientesSeleccionados.put(cliente, new HorarioCliente(horaInicio, horaFin));
            } else {
                clientesSeleccionados.get(cliente).setHoraFin(horaFin); // ACTUALIZAR LA HORA
            }
        }

        // Ahora que tienes todos los datos, puedes mostrar el diálogo de confirmación o procesar los datos como necesites.
        mostrarDialogoDeConfirmacion(clientesSeleccionados);
    }

    // OBTENER LOS CLIENTES QUE NO TIENEN A NINGUN TRABAJADOR ASIGNADO Y QUE TIENEN NECESIDADES
    private void cargarClientesSinTrabajadorAsignado() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios")
                .whereEqualTo("rol", "cliente")
                .whereNotEqualTo("necesidades",null)
                .whereEqualTo("trabajadorAsignado", null)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Cliente> clientes = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        clientes.add(document.toObject(Cliente.class));
                    }

                    // COMPROBAR SI NO HAY CLIENTES PARA MOSTRAR UN MENSAJE INDICANDOLO
                    if (clientes.isEmpty()) {
                        mostrarMensajes(this,0,"No hay clientes para asignar");
                        finish();
                    } else {
                        // SI QUE HAY CLIENTES Y SE RELLENAN LOS SPINNERS
                        actualizarDesplegables(clientes);
                    }
                })
                .addOnFailureListener(e -> {
                    mostrarMensajes(getApplicationContext(), 1, "Error al cargar clientes sin trabajador asignado");
                });
    }


    // ACTUALIZAR LOS DESPLEGABLES CON LOS CLIENTES QUE SE HAN OBTENIDO
    private void actualizarDesplegables(ArrayList<Cliente> nombresClientes) {
        ArrayAdapter<Cliente> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresClientes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        for (Spinner desplegable : desplegables) {
            desplegable.setAdapter(adapter);
        }
    }

    // MOSTRAR UN DIALOGO CON LOS DATOS RECOGIDOS EN PANTALLA
    private void mostrarDialogoDeConfirmacion(Map<Cliente, HorarioCliente> clientes) {
        // LISTA PARA GUARDAR LOS CLIENTES YA CON LOS DATOS CORRECTOS PARA ACTUALIZARLOS MAS TARDE
        List<Cliente> clientesAAsignar = new ArrayList<>();

        // CREAR EL MENSAJE CON LOS DATOS DE LOS CLIENTES Y LAS HORAS
        StringBuilder mensaje = new StringBuilder();
        for (Map.Entry<Cliente, HorarioCliente> cliente : clientes.entrySet()) {
            // ESTABLECERLE AL CLIENTE LA HORA DE ENTRADA Y SALIDA
            cliente.getKey().setHoraEntradaTrabajador(cliente.getValue().getHoraInicio());
            cliente.getKey().setHoraSalidaTrabajador(cliente.getValue().getHoraFin());

            // GUARDAR TODOS LOS DATOS YA CORRECTOS DEL CLIENTE
            clientesAAsignar.add(cliente.getKey());

            // DATOS A MOSTRAR
            mensaje.append("Cliente: ").append(cliente.getKey().getNombre())
                    .append("\nDirección: ").append(cliente.getKey().getDireccion() + ", " + cliente.getKey().getLocalidad())
                    .append("\nHorario: ").append(cliente.getKey().getHoraEntradaTrabajador() + "-" + cliente.getKey().getHoraSalidaTrabajador())
                    .append("\n\n");
        }

        // ESTABLECER EL TITULO Y EL CONTENIDO AL DIALOGO
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar Asignación de Trabajos");
        builder.setMessage(mensaje.toString());

        // OPCION SI PARA GUARDAR LOS DATOS EN LAS BASE DE DATOS
        builder.setPositiveButton("Sí", (dialog, which) -> {
            administrador.asignarTrabajadores(trabajador,clientesAAsignar,this);
        });

        // OPCION NO PARA CERRAR EL DIALOGO
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        // MOSTRAR EL DIALOGO
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // MOSTRAR UN DIALOGO PARA SELECCIONAR LA HORA
    public void mostrarTimePickerDialog(View view) {

        final TextView textView = (TextView) view; // ALMACENAR UNA REFERENCIA AL TEXTVIEW AL QUE HAGO CLICK
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // DETERMINAR SI ES AM O PM
                String amOPm = (hourOfDay < 12) ? "AM" : "PM";

                // Aquí puedes hacer lo que quieras con la hora seleccionada
                String selectedTime = String.format(Locale.getDefault(), "%02d:%02d ", hourOfDay, minute, amOPm);
                textView.setText(selectedTime);
            }
        }, 12, 0, false);
        timePickerDialog.show();
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
                        mostrarMensajes(getApplicationContext(), 1, "No se encontraron administradores");
                    }
                })
                .addOnFailureListener(e -> {
                    mostrarMensajes(getApplicationContext(), 1, "Error al buscar datos de administrador");
                });
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
    private class HorarioCliente {
        private String horaInicio;
        private String horaFin;

        HorarioCliente(String horaInicio, String horaFin) {
            this.horaInicio = horaInicio;
            this.horaFin = horaFin;
        }

        public void setHoraInicio(String horaInicio) {
            this.horaInicio = horaInicio;
        }

        public void setHoraFin(String horaFin) {
            this.horaFin = horaFin;
        }

        public String getHoraInicio() {
            return horaInicio;
        }

        public String getHoraFin() {
            return horaFin;
        }
    }

}
