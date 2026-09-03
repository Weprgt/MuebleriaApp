package com.example.muebleriaapp;

public class Articulo {
    private long id = -1;
    private String nombre;
    private String precio;
    private String descripcion;
    private String categoria;
    private String fotoUri;

    // Constructor utilizado cuando todavía no hay fotografía
    // Constructor para un artículo que todavía no está guardado en SQLite
    public Articulo(
            String nombre,
            String precio,
            String descripcion,
            String categoria,
            String fotoUri
    ) {
        this(
                -1,
                nombre,
                precio,
                descripcion,
                categoria,
                fotoUri
        );
    }

    // Constructor para recuperar un artículo almacenado en SQLite
    public Articulo(
            long id,
            String nombre,
            String precio,
            String descripcion,
            String categoria,
            String fotoUri
    ) {
        this.id = id;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}