package com.example.projectgui.Models;

public class Empleado {
    private int idEmpleado;
    private String nombres;
    private String apellidos;
    private int edad;
    private String dui;
    private String direccion;
    private String telefono;
    private String correo;
    private String cargo;
    private int estadoEmpleado;
    private int estadoUsuario;
    private String nombreCompleto;


    public Empleado(int idEmpleado, String nombres, String apellidos, int edad, String dui, String direccion, String telefono, String correo, String cargo, int estadoEmpleado, int estadoUsuario, String nombreCompleto) {
        this.idEmpleado = idEmpleado;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.edad = edad;
        this.dui = dui;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correo = correo;
        this.cargo = cargo;
        this.estadoEmpleado = estadoEmpleado;
        this.estadoUsuario = estadoUsuario;
        this.nombreCompleto = nombreCompleto;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getDui() {
        return dui;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public int getEstadoEmpleado() {
        return estadoEmpleado;
    }

    public void setEstadoEmpleado(int estadoEmpleado) {
        this.estadoEmpleado = estadoEmpleado;
    }

    public int getEstadoUsuario() {
        return estadoUsuario;
    }

    public void setEstadoUsuario(int estadoUsuario) {
        this.estadoUsuario = estadoUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

}
