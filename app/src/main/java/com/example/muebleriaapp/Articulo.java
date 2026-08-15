package com.example.muebleriaapp;

public class Articulo {

    private String nombre;
    private String precio;
    private String descripcion;
    private String categoria;
    private String fotoUri;

    // Constructor utilizado cuando todavía no hay fotografía
    public Articulo(
            String nombre,
            String precio,
            String descripcion,
            String categoria
    ) {
        this(
                nombre,
                precio,
                descripcion,
                categoria,
                ""
        );
    }

    // Constructor utilizado cuando sí existe una fotografía
    public Articulo(
            String nombre,
            String precio,
            String descripcion,
            String categoria,
            String fotoUri
    ) {
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.fotoUri = fotoUri;
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

    public String getFotoUri() {
        return fotoUri;
    }

    public void setFotoUri(String fotoUri) {
        this.fotoUri = fotoUri;
    }
}