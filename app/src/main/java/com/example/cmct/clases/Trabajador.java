package com.example.cmct.clases;

import java.io.Serializable;

public class Trabajador extends Usuario implements Serializable {
    public Trabajador(String idUsuario, String nombre, String apellido1, String apellido2, String telefono, String dni, String correo) {
        super(idUsuario, nombre, apellido1, apellido2, telefono, dni, correo);
    }
}
