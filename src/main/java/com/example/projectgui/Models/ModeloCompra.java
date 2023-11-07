package com.example.projectgui.Models;

//Una clase con el objetivo de tener un modelo de la compra que se va a realizar
public class ModeloCompra {
    private String nombreProducto;
    private int cantidadAComprar;
    private double precioUnitario;
    private double total;

    public ModeloCompra(String nombreProducto, int cantidadAComprar, double precioUnitario, double total) {
        this.nombreProducto = nombreProducto;
        this.cantidadAComprar = cantidadAComprar;
        this.precioUnitario = precioUnitario;
        this.total = total;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public int getCantidadAComprar() {
        return cantidadAComprar;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public double getTotal() {
        return total;
    }
}
