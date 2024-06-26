package com.example.proyecto.model;

public class Worker {
    private String idTrabajador;
    private String dni;
    private String nombre;
    private String apellidos;
    private String especialidad;
    private String contraseña;
    private String email;

    public Worker(String id, String dni, String nombre, String apellidos,
                  String especialidad, String contraseña, String email) {
        this.idTrabajador = id;
        this.dni = dni;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.especialidad = especialidad;
        this.contraseña = contraseña;
        this.email = email;
    }

    public String getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(String id) {
        this.idTrabajador = id;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contrasenya) {
        this.contraseña = contraseña;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return apellidos + ", " + nombre + " (" + especialidad + ")";
    }
}
