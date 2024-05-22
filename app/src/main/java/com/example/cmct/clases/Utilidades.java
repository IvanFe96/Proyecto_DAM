package com.example.cmct.clases;

import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cmct.R;
import com.example.cmct.modelo.admo.gestion_trabajadores.AltaTrabajador;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilidades {

    // COMPROBAR QUE EL NOMBRE Y LOS APELLIDOS ES TEXTO Y NO HAY NUMEROS
    public static boolean validarNombreApellidos(String nombre, String apellido1, String apellido2) {
        // UTILIZAR UNA EXPRESION REGULAR QUE PERMITA SOLO LETRAS Y ESPACIOS EN BLANCO
        String regex = "^[\\p{L} ]+$";
        // COMPROBAR QUE LOS CAMPOS CUMPLEN CON LA EXPRESION REGULAR
        return nombre.matches(regex) && apellido1.matches(regex) && apellido2.matches(regex);
    }

    // COMPROBAR QUE EL CORREO ES UNA CUENTA DE GMAIL
    public static boolean validarCorreo(String correo) {
        return correo.contains("@gmail.com");
    }

    public static boolean validarTelefono(String telefono) {
        // PATRON PARA VALIDAR NUMEROS DE TELEFONO
        String patron = "^[6789]\\d{8}$";

        // COMPILAR LA EXPRESION
        Pattern pattern = Pattern.compile(patron);

        // VERIFICAR SI EL NUMERO COINCIDE CON EL PATRON
        Matcher matcher = pattern.matcher(telefono);
        return matcher.matches();
    }

    // COMPROBAR QUE EL DNI ES CORRECTO
    public static boolean validarDni(String dni) {
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

    // COMPROBAR QUE LA DIRECCION SE PUEDE ENCONTRAR EN GOOGLE MAPS
    public static boolean validarDireccion(String direccion, String localidad, Activity actividad) {

        // COMPROBAR SI LA DIRECCIÓN CONTIENE AL MENOS UN NÚMERO Y UNA PALABRA
        if (!direccion.matches(".*\\d+.*") || !direccion.matches(".*\\b\\w+\\b.*")) {
            return false;
        }

        // OBTENER EL GEOCODER PARA BUSCAR LA UBICACION
        Geocoder geocoder = new Geocoder(actividad.getApplicationContext());
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

    // MOSTRAR TOAST PERSONALIZADOS
    public static void mostrarMensajes(Activity actividad, int tipo, String mensaje) {
        if(tipo == 2) {
            // MENSAJE INFORMATIVO
            LayoutInflater inflater = actividad.getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_personalizado, null);

            TextView text = (TextView) layout.findViewById(R.id.toast_text);
            text.setText(mensaje); // CONFIGURAR EL MENSAJE INFORMATIVO PERSONALIZADO

            Toast toast = new Toast(actividad.getApplicationContext());
            toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 300);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        } else if(tipo == 1){
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
        } else {
            // MENSAJE DE EXITO
            LayoutInflater inflater = actividad.getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast_personalizado_exito, null);

            TextView text = layout.findViewById(R.id.toast_text);
            text.setText(mensaje); // CONFIGURAR EL MENSAJE DE EXITO PERSONALIZADO

            Toast toast = new Toast(actividad.getApplicationContext());
            toast.setGravity(Gravity.CENTER | Gravity.BOTTOM, 0, 300);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }
    }

    // DEVOLVER EL TEXTO PASADO CON LA PRIMERA LETRA MAYUSCULA
    public static String primeraLetraMayuscula(String texto) {
        String textoConPrimeraLetraMayuscula = "";

        textoConPrimeraLetraMayuscula = String.valueOf(texto.charAt(0)).toUpperCase();
        textoConPrimeraLetraMayuscula += texto.substring(1,texto.length());

        return  textoConPrimeraLetraMayuscula;
    }

    // DIALOGO DE ESPERA EN CASO DE QUE TARDE MUCHO FIREBASE EN DAR RESPUESTA
    public static void esperar(Activity actividad) {
        ProgressDialog progressDialog = new ProgressDialog(actividad);
        progressDialog.setMessage("Por favor espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

}
