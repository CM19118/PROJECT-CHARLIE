package com.example.projectgui.Models;

public class Compras {
    private int id;
    private String nombreArticulo;
    private int cantidad;
    private double montoCompra;
    private String fechaCompra;

    public Compras(int id, String nombreArticulo, int cantidad, double montoCompra, String fechaCompra) {
        this.id = id;
        this.nombreArticulo = nombreArticulo;
        this.cantidad = cantidad;
        this.montoCompra = montoCompra;
        this.fechaCompra = fechaCompra;
    }

    public int getId() {
        return id;
    }

    public String getNombreArticulo() {
        return nombreArticulo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getMontoCompra() {
        return montoCompra;
    }

    public String getFechaCompra() {
        return fechaCompra;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombreArticulo(String nombreArticulo) {
        this.nombreArticulo = nombreArticulo;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setMontoCompra(double montoCompra) {
        this.montoCompra = montoCompra;
    }

    public void setFechaCompra(String fechaCompra) {
        this.fechaCompra = fechaCompra;
    }
}
