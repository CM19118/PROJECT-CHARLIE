package com.example.projectgui.Models;

public class Factura {

    public int idFactura;
    public String fechaFactura;
    public double montoTotal;
    public int cantidadProducto;

    public String productos;
    public String nombreCliente;
    public String telefonoCliente;

    public Factura(int idFactura, String fechaFactura, double montoTotal, int cantidadProducto, String productos, String nombreCliente, String telefonoCliente) {
        this.idFactura = idFactura;
        this.fechaFactura = fechaFactura;
        this.montoTotal = montoTotal;
        this.cantidadProducto = cantidadProducto;
        this.productos = productos;
        this.nombreCliente = nombreCliente;
        this.telefonoCliente = telefonoCliente;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public String getFechaFactura() {
        return fechaFactura;
    }

    public void setFechaFactura(String fechaFactura) {
        this.fechaFactura = fechaFactura;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public int getCantidadProducto() {
        return cantidadProducto;
    }

    public void setCantidadProducto(int cantidadProducto) {
        this.cantidadProducto = cantidadProducto;
    }

    public String getProductos() {
        return productos;
    }

    public void setProductos(String productos) {
        this.productos = productos;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }
}
