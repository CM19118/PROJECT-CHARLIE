package com.example.projectgui.Models;

public class Proveedor {
    public int idProveedor;
    public String nombreProveedor;
    public  String direccion;
    public String correo;
    public String tipo;
    public String telefono;
    public String observaciones;

    public Proveedor(int idProveedor, String nombreProveedor, String direccion, String correo, String tipo, String telefono, String observaciones) {
        this.idProveedor = idProveedor;
        this.nombreProveedor = nombreProveedor;
        this.direccion = direccion;
        this.correo = correo;
        this.tipo = tipo;
        this.telefono = telefono;
        this.observaciones = observaciones;
    }


    public int getIdProveedor() {
        return idProveedor;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTipo() {
        return tipo;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
