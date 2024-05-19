package com.example.cmct.clases;

import java.io.Serializable;
import java.util.HashMap;

public class Cliente extends Usuario implements Serializable {

    private String localidad;
    private String direccion;
    private String horaEntradaTrabajador;
    private String horaSalidaTrabajador;
    private HashMap<String, String> necesidades;
    private String trabajadorAsignado;

    public Cliente() {
        super();
    }

    public Cliente(String imagen, String nombre, String apellido1, String apellido2, String telefono, String dni, String correo, String contrasenia, String rol,
                   String localidad, String direccion, String horaEntradaTrabajador, String horaSalidaTrabajador, HashMap<String, String> necesidades, String trabajadorAsignado) {
        super(imagen, nombre, apellido1, apellido2, telefono, dni, correo, contrasenia, rol);

        this.localidad = localidad;
        this.direccion = direccion;
        this.horaEntradaTrabajador = horaEntradaTrabajador;
        this.horaSalidaTrabajador = horaSalidaTrabajador;
        this.necesidades = necesidades;
        this.trabajadorAsignado = trabajadorAsignado;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getHoraEntradaTrabajador() {
        return horaEntradaTrabajador;
    }

    public void setHoraEntradaTrabajador(String horaEntradaTrabajador) {
        this.horaEntradaTrabajador = horaEntradaTrabajador;
    }

    public String getHoraSalidaTrabajador() {
        return horaSalidaTrabajador;
    }

    public void setHoraSalidaTrabajador(String horaSalidaTrabajador) {
        this.horaSalidaTrabajador = horaSalidaTrabajador;
    }

    public HashMap<String, String> getNecesidades() {
        return necesidades;
    }

    public void setNecesidades(HashMap<String, String> necesidades) {
        this.necesidades = necesidades;
    }

    public String getTrabajadorAsignado() {
        return trabajadorAsignado;
    }

    public void setTrabajadorAsignado(String trabajadorAsignado) {
        this.trabajadorAsignado = trabajadorAsignado;
    }

    // SE USA PARA SABER SI HAY CLIENTES IGUALES
    // ? : (es una forma mas corta de hacer una condicion else if)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return this.getDni() != null ? this.getDni().equals(cliente.getDni()) : cliente.getDni() == null;
    }

    @Override
    public int hashCode() {
        return this.getDni() != null ? this.getDni().hashCode() : 0;
    }

    // SE USA PARA MOSTRAR EL NOMBRE EN LOS DESPLEGABLES DE ASIGNAR TRABAJO
    @Override
    public String toString() {
        return this.getNombre();
    }
}
