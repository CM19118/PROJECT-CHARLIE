package com.example.projectgui;

public class UserSession {
    private String username;
    private String rol;
    private int idUsuario;
    private int idEmpleado;

    public UserSession(String username, String rol, int idUsuario, int idEmpleado) {
        this.username = username;
        this.rol = rol;
        this.idUsuario = idUsuario;
        this.idEmpleado = idEmpleado;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getUsername() {
        return username;
    }

    public String getRol() {
        return rol;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }
}
