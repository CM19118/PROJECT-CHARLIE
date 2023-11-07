package com.example.projectgui.Models;

import java.util.Date;

public class Venta {

    public int idVenta;
    public String fechaVenta;
    public String productos;
    public int cantidadProducto;
    public double total;


    public Venta(int idVenta, String fechaVenta, String productos, int cantidadProducto, double total) {
        this.idVenta = idVenta;
        this.fechaVenta = fechaVenta;
        this.productos = productos;
        this.cantidadProducto = cantidadProducto;
        this.total = total;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public String getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(String fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getProductos() {
        return productos;
    }

    public void setProductos(String productos) {
        this.productos = productos;
    }

    public int getCantidadProducto() {
        return cantidadProducto;
    }

    public void setCantidadProducto(int cantidadProducto) {
        this.cantidadProducto = cantidadProducto;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }




}
