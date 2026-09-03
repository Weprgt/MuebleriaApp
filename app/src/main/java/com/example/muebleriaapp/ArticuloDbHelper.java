package com.example.muebleriaapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

public class ArticuloDbHelper extends SQLiteOpenHelper {
    public static final String TABLA_ARTICULOS = "articulos";
    public static final String COLUMNA_ID = "_id";
    public static final String COLUMNA_NOMBRE = "nombre";
    public static final String COLUMNA_PRECIO = "precio";
    public static final String COLUMNA_DESCRIPCION = "descripcion";
    public static final String COLUMNA_CATEGORIA = "categoria";
    public static final String COLUMNA_FOTO_URI = "foto_uri";


    public static final String TABLA_USUARIOS = "usuarios";
    public static final String COLUMNA_USUARIO_ID = "_id";
    public static final String COLUMNA_USUARIO = "usuario";
    public static final String COLUMNA_PASSWORD_HASH = "password_hash";
    public static final String COLUMNA_PASSWORD_SALT = "password_salt";

    private static final String SQL_CREAR_TABLA_ARTICULOS =
            "CREATE TABLE " + TABLA_ARTICULOS + " (" +
                    COLUMNA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMNA_NOMBRE + " TEXT NOT NULL, " +
                    COLUMNA_PRECIO + " TEXT NOT NULL, " +
                    COLUMNA_DESCRIPCION + " TEXT NOT NULL, " +
                    COLUMNA_CATEGORIA + " TEXT NOT NULL, " +
                    COLUMNA_FOTO_URI + " TEXT NOT NULL DEFAULT ''" +
                    ")";

    private static final String SQL_CREAR_TABLA_USUARIOS =
            "CREATE TABLE " + TABLA_USUARIOS + " (" +
                    COLUMNA_USUARIO_ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMNA_USUARIO +
                    " TEXT NOT NULL UNIQUE COLLATE NOCASE, " +
                    COLUMNA_PASSWORD_HASH +
                    " TEXT NOT NULL, " +
                    COLUMNA_PASSWORD_SALT +
                    " TEXT NOT NULL" +
                    ")";

    private static final String NOMBRE_BASE_DATOS = "muebleria.db";
    private static final int VERSION_BASE_DATOS = 2;

    public ArticuloDbHelper(Context context) {
        super(
                context,
                NOMBRE_BASE_DATOS,
                null,
                VERSION_BASE_DATOS
        );
    }

    public long insertarArticulo(Articulo articulo) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(COLUMNA_NOMBRE, articulo.getNombre());
        valores.put(COLUMNA_PRECIO, articulo.getPrecio());
        valores.put(COLUMNA_DESCRIPCION, articulo.getDescripcion());
        valores.put(COLUMNA_CATEGORIA, articulo.getCategoria());
        valores.put(COLUMNA_FOTO_URI, articulo.getFotoUri());

        long idGenerado = db.insert(
                TABLA_ARTICULOS,
                null,
                valores
        );

        if (idGenerado != -1) {
            articulo.setId(idGenerado);
        }

        return idGenerado;
    }

    public ArrayList<Articulo> obtenerTodosLosArticulos() {
        ArrayList<Articulo> articulos = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] columnas = {
                COLUMNA_ID,
                COLUMNA_NOMBRE,
                COLUMNA_PRECIO,
                COLUMNA_DESCRIPCION,
                COLUMNA_CATEGORIA,
                COLUMNA_FOTO_URI
        };

        try (Cursor cursor = db.query(
                TABLA_ARTICULOS,
                columnas,
                null,
                null,
                null,
                null,
                COLUMNA_ID + " ASC"
        )) {
            int indiceId = cursor.getColumnIndexOrThrow(COLUMNA_ID);
            int indiceNombre = cursor.getColumnIndexOrThrow(COLUMNA_NOMBRE);
            int indicePrecio = cursor.getColumnIndexOrThrow(COLUMNA_PRECIO);
            int indiceDescripcion =
                    cursor.getColumnIndexOrThrow(COLUMNA_DESCRIPCION);
            int indiceCategoria =
                    cursor.getColumnIndexOrThrow(COLUMNA_CATEGORIA);
            int indiceFotoUri =
                    cursor.getColumnIndexOrThrow(COLUMNA_FOTO_URI);

            while (cursor.moveToNext()) {
                Articulo articulo = new Articulo(
                        cursor.getLong(indiceId),
                        cursor.getString(indiceNombre),
                        cursor.getString(indicePrecio),
                        cursor.getString(indiceDescripcion),
                        cursor.getString(indiceCategoria),
                        cursor.getString(indiceFotoUri)
                );

                articulos.add(articulo);
            }
        }

        return articulos;
    }

    public int actualizarArticulo(Articulo articulo) {
        if (articulo.getId() <= 0) {
            return 0;
        }

        SQLiteDatabase db = getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put(COLUMNA_NOMBRE, articulo.getNombre());
        valores.put(COLUMNA_PRECIO, articulo.getPrecio());
        valores.put(COLUMNA_DESCRIPCION, articulo.getDescripcion());
        valores.put(COLUMNA_CATEGORIA, articulo.getCategoria());
        valores.put(COLUMNA_FOTO_URI, articulo.getFotoUri());

        return db.update(
                TABLA_ARTICULOS,
                valores,
                COLUMNA_ID + " = ?",
                new String[]{String.valueOf(articulo.getId())}
        );
    }

    public int eliminarArticulo(Articulo articulo) {
        if (articulo.getId() <= 0) {
            return 0;
        }

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLA_ARTICULOS,
                COLUMNA_ID + " = ?",
                new String[]{String.valueOf(articulo.getId())}
        );
    }

    public boolean existeUsuario(String usuario) {
        SQLiteDatabase db = getReadableDatabase();

        String seleccion =
                COLUMNA_USUARIO + " = ?";

        String[] argumentos = {
                usuario.trim()
        };

        try (Cursor cursor = db.query(
                TABLA_USUARIOS,
                new String[]{COLUMNA_USUARIO_ID},
                seleccion,
                argumentos,
                null,
                null,
                null,
                "1"
        )) {
            return cursor.moveToFirst();
        }
    }

    public boolean registrarUsuario(
            String usuario,
            String contrasena
    ) {
        String salt =
                SeguridadContrasena.generarSalt();

        String hash =
                SeguridadContrasena.crearHash(
                        contrasena,
                        salt
                );

        ContentValues valores = new ContentValues();
        valores.put(COLUMNA_USUARIO, usuario.trim());
        valores.put(COLUMNA_PASSWORD_HASH, hash);
        valores.put(COLUMNA_PASSWORD_SALT, salt);

        SQLiteDatabase db = getWritableDatabase();

        long idGenerado = db.insertWithOnConflict(
                TABLA_USUARIOS,
                null,
                valores,
                SQLiteDatabase.CONFLICT_IGNORE
        );

        return idGenerado != -1;
    }

    public boolean validarUsuario(
            String usuario,
            String contrasena
    ) {
        SQLiteDatabase db = getReadableDatabase();

        String[] columnas = {
                COLUMNA_PASSWORD_HASH,
                COLUMNA_PASSWORD_SALT
        };

        String seleccion =
                COLUMNA_USUARIO + " = ?";

        String[] argumentos = {
                usuario.trim()
        };

        try (Cursor cursor = db.query(
                TABLA_USUARIOS,
                columnas,
                seleccion,
                argumentos,
                null,
                null,
                null,
                "1"
        )) {
            if (!cursor.moveToFirst()) {
                return false;
            }

            String hashGuardado = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            COLUMNA_PASSWORD_HASH
                    )
            );

            String saltGuardado = cursor.getString(
                    cursor.getColumnIndexOrThrow(
                            COLUMNA_PASSWORD_SALT
                    )
            );

            return SeguridadContrasena.verificar(
                    contrasena,
                    saltGuardado,
                    hashGuardado
            );
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREAR_TABLA_ARTICULOS);
        db.execSQL(SQL_CREAR_TABLA_USUARIOS);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int versionAnterior,
            int versionNueva
    ) {
        if (versionAnterior < 2) {
            db.execSQL(SQL_CREAR_TABLA_USUARIOS);
        }
    }
}