package com.example.cmct.clases;

public class Cliente extends Usuario{
    private String direccion;
    private String horaEntradaTrabajador;
    private String horaSalidaTrabajador;
    private String[] necesidades;

    public Cliente(String idUsuario, String nombre, String apellido1, String apellido2, String telefono, String dni, String correo, String direccion
                        ,String horaEntradaTrabajador, String horaSalidaTrabajador, String[] necesidades) {
        super(idUsuario, nombre, apellido1, apellido2, telefono, dni, correo);

        this.direccion = direccion;
        this.horaEntradaTrabajador = horaEntradaTrabajador;
        this.horaSalidaTrabajador = horaSalidaTrabajador;
        this.necesidades = necesidades;
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

    public String[] getNecesidades() {
        return necesidades;
    }

    public void setNecesidades(String[] necesidades) {
        this.necesidades = necesidades;
    }
}
