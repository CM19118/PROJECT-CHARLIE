package com.example.projectgui.Models;

public class Garantia {

    public int idGarantia;
    public String fechaGarantia;
    public String cliente;
    public String equipo;
    public String detalles;
    public double monto;
    public String fechaVencimiento;
    public String estadoGrantia;

    public Garantia(int idGarantia, String fechaGarantia, String cliente, String equipo, String detalles, double monto, String fechaVencimiento, String estadoGrantia) {
        this.idGarantia = idGarantia;
        this.fechaGarantia = fechaGarantia;
        this.cliente = cliente;
        this.equipo = equipo;
        this.detalles = detalles;
        this.monto = monto;
        this.fechaVencimiento = fechaVencimiento;
        this.estadoGrantia = estadoGrantia;
    }

    public int getIdGarantia() {
        return idGarantia;
    }

    public void setIdGarantia(int idGarantia) {
        this.idGarantia = idGarantia;
    }

    public String getFechaGarantia() {
        return fechaGarantia;
    }

    public void setFechaGarantia(String fechaGarantia) {
        this.fechaGarantia = fechaGarantia;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
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

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(String fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getEstadoGrantia() {
        return estadoGrantia;
    }

    public void setEstadoGrantia(String estadoGrantia) {
        this.estadoGrantia = estadoGrantia;
    }
}
