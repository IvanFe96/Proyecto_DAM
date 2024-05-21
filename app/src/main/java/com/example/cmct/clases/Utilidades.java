package com.example.cmct.clases;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cmct.R;

public class Utilidades {

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
}
