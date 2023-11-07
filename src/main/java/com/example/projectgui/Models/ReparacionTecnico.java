package com.example.projectgui.Models;

public class ReparacionTecnico {

    public int idReparacion;

    public String servicio;
    public double precioUnitario;
    public String fechaInicio;
    public String cliente;
    public String numTelefono;
    public String correo;
    public String equipo;
    public String detalles;
    public String estado;
    public String detalleCostAdicional;
    public double montoCostAdicional;

    public ReparacionTecnico(int idReparacion, String servicio, double precioUnitario, String fechaInicio, String cliente, String numTelefono, String correo, String equipo, String detalles, String estado, String detalleCostAdicional, double montoCostAdicional) {
        this.idReparacion = idReparacion;
        this.servicio = servicio;
        this.precioUnitario = precioUnitario;
        this.fechaInicio = fechaInicio;
        this.cliente = cliente;
        this.numTelefono = numTelefono;
        this.correo = correo;
        this.equipo = equipo;
        this.detalles = detalles;
        this.estado = estado;
        this.detalleCostAdicional = detalleCostAdicional;
        this.montoCostAdicional = montoCostAdicional;
    }

    public int getIdReparacion() {
        return idReparacion;
    }

    public void setIdReparacion(int idReparacion) {
        this.idReparacion = idReparacion;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getNumTelefono() {
        return numTelefono;
    }

    public void setNumTelefono(String numTelefono) {
        this.numTelefono = numTelefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getDetalleCostAdicional() {
        return detalleCostAdicional;
    }

    public void setDetalleCostAdicional(String detalleCostAdicional) {
        this.detalleCostAdicional = detalleCostAdicional;
    }

    public double getMontoCostAdicional() {
        return montoCostAdicional;
    }

    public void setMontoCostAdicional(double montoCostAdicional) {
        this.montoCostAdicional = montoCostAdicional;
    }
}
