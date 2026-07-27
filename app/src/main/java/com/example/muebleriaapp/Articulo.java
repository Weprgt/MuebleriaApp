package com.example.muebleriaapp;

public class Articulo {
    String nombre;
    String precio;
    String descripcion;
    String categoria;

    public Articulo(String nombre, String precio, String descripcion, String categoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.categoria = categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public String getPrecio() {
        return precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCategoria() {
        return categoria;
    }
}
