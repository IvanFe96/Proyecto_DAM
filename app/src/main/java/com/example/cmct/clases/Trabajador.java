package com.example.cmct.clases;

import java.io.Serializable;

public class Trabajador extends Usuario implements Serializable {

    public Trabajador() {
        super();
    }

    public Trabajador(String imagen, String nombre, String apellido1, String apellido2, String telefono, String dni, String correo, String contrasenia, String rol) {
        super(imagen, nombre, apellido1, apellido2, telefono, dni, correo, contrasenia, rol);
    }
}
