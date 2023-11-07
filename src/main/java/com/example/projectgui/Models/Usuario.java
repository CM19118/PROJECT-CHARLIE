package com.example.projectgui.Models;

public class Usuario {
    private int idUsuario;
    private int idEmpleado;
    private String usuario;
    private String password;
    private int rol;

    public Usuario(int idUsuario, int idEmpleado, String usuario, String password, int rol) {
        this.idUsuario = idUsuario;
        this.idEmpleado = idEmpleado;
        this.usuario = usuario;
        this.password = password;
        this.rol = rol;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRol() {
        return rol;
    }

    public void setRol(int rol) {
        this.rol = rol;
    }
}
