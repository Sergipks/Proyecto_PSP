package com.example.proyecto.model;

import java.util.Date;

public class Task {
    private int cod;
    private String categoria;
    private String descripcion;
    private Date fecIni;
    private Date fecFin;
    private int tiempo;
    private int prioridad;
    private Worker trabajador;

    public Task(int cod, String categoria, String descripcion, Date fecIni,
                Date fecFin, int tiempo, int prioridad) {
        this.cod = cod;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.fecIni = fecIni;
        this.fecFin = fecFin;
        this.tiempo = tiempo;
        this.prioridad = prioridad;
        this.trabajador = null;
    }

    public Task(int cod, String categoria, String descripcion, Date fecIni,
                Date fecFin, int tiempo, int prioridad, Worker trabajador) {
        this.cod = cod;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.fecIni = fecIni;
        this.fecFin = fecFin;
        this.tiempo = tiempo;
        this.prioridad = prioridad;
        this.trabajador = trabajador;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
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
