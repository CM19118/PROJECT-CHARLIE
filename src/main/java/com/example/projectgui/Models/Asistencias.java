package com.example.projectgui.Models;

public class Asistencias {

    public int idAsistencia;
    public String nombresEmpleado;
    public String apellidosEmpleado;
    public String fechaAsistencia;
    public String horaEntrada;
    public String horaSalida;

    public Asistencias(int idAsistencia, String nombresEmpleado, String apellidosEmpleado, String fechaAsistencia, String horaEntrada, String horaSalida) {
        this.idAsistencia = idAsistencia;
        this.nombresEmpleado = nombresEmpleado;
        this.apellidosEmpleado = apellidosEmpleado;
        this.fechaAsistencia = fechaAsistencia;
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
    }

    public int getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(int idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public String getNombresEmpleado() {
        return nombresEmpleado;
    }

    public void setNombresEmpleado(String nombresEmpleado) {
        this.nombresEmpleado = nombresEmpleado;
    }

    public String getApellidosEmpleado() {
        return apellidosEmpleado;
    }

    public void setApellidosEmpleado(String apellidosEmpleado) {
        this.apellidosEmpleado = apellidosEmpleado;
    }

    public String getFechaAsistencia() {
        return fechaAsistencia;
    }

    public void setFechaAsistencia(String fechaAsistencia) {
        this.fechaAsistencia = fechaAsistencia;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(String horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }
}
