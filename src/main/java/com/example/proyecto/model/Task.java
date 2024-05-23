package com.example.proyecto.model;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.util.Date;

public class Task {

    @SerializedName("codTrabajo")
    private String codTrabajo;

    @SerializedName("categoria")
    private String categoria;

    @SerializedName("descripcion")
    private String descripcion;

    @SerializedName("fechaInicio")
    private Date fecIni;

    @SerializedName("fechaFin")
    private Date fecFin;

    @SerializedName("tiempo")
    private BigDecimal tiempo;

    @SerializedName("prioridad")
    private int prioridad;

    @SerializedName("idTrabajador")
    private String idTrabajador;

    public Task() {
    }

    // Constructor con parámetros
    public Task(String cod, String categoria, String descripcion, int prioridad) {
        this.codTrabajo = cod;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.fecIni = null;
        this.fecFin = null;
        this.tiempo = null;
        this.prioridad = prioridad;
        this.idTrabajador = null;
    }

    public Task(String cod, String categoria, String descripcion, int prioridad, String idTrabajador) {
        this.codTrabajo = cod;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.fecIni = null;
        this.fecFin = null;
        this.tiempo = null;
        this.prioridad = prioridad;
        this.idTrabajador = idTrabajador;
    }

    // Getters y Setters
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

    public BigDecimal getTiempo() {
        return tiempo;
    }

    public void setTiempo(BigDecimal tiempo) {
        this.tiempo = tiempo;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public String getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(String idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    @Override
    public String toString() {
        return  codTrabajo + " | " + descripcion + " | " +
                categoria + " (prioridad: " + prioridad +')';
    }
}
