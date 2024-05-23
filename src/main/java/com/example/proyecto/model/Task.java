package com.example.proyecto.model;

import java.util.Date;

public class Task {
    private String codTrabajo;
    private String categoria;
    private String descripcion;
    private Date fecIni;
    private Date fecFin;
    private int tiempo;
    private int prioridad;
    private Worker trabajador;

    public Task(String cod, String categoria, String descripcion, Date fecIni, int prioridad) {
        this.codTrabajo = cod;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.fecIni = fecIni;
        this.fecFin = null;
        this.tiempo = -1;
        this.prioridad = prioridad;
        this.trabajador = null;
    }

    public Task(String cod, String categoria, String descripcion, Date fecIni, int prioridad, Worker trabajador) {
        this.codTrabajo = cod;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.fecIni = fecIni;
        this.fecFin = null;
        this.tiempo = -1;
        this.prioridad = prioridad;
        this.trabajador = trabajador;
    }

    public String getCodTrabajo() {
        return codTrabajo;
    }

    public void setCodTrabajo(String codTrabajo) {
        this.codTrabajo = codTrabajo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFecIni() {
        return fecIni;
    }

    public void setFecIni(Date fecIni) {
        this.fecIni = fecIni;
    }

    public Date getFecFin() {
        return fecFin;
    }

    public void setFecFin(Date fecFin) {
        this.fecFin = fecFin;
    }

    public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public Worker getTrabajador() {
        return trabajador;
    }

    public void setTrabajador(Worker trabajador) {
        this.trabajador = trabajador;
    }
}
