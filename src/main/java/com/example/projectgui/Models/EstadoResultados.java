package com.example.projectgui.Models;

public class EstadoResultados {
    private String concepto;
    private String monto;

    public EstadoResultados(String concepto, String monto) {
        this.concepto = concepto;
        this.monto = monto;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }
}
