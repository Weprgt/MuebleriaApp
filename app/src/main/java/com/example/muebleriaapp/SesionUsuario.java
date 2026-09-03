package com.example.muebleriaapp;

import android.content.Context;
import android.content.SharedPreferences;

public final class SesionUsuario {

    private static final String PREFERENCIAS =
            "sesion_usuario";

    private static final String CLAVE_INICIADA =
            "sesion_iniciada";

    private static final String CLAVE_USUARIO =
            "nombre_usuario";

    private SesionUsuario() {
        // Evita crear objetos de esta clase.
    }

    public static void iniciar(
            Context context,
            String usuario
    ) {
        SharedPreferences preferencias =
                context.getSharedPreferences(
                        PREFERENCIAS,
                        Context.MODE_PRIVATE
                );

        preferencias.edit()
                .putBoolean(CLAVE_INICIADA, true)
                .putString(CLAVE_USUARIO, usuario)
                .apply();
    }

    public static boolean estaIniciada(Context context) {
        return context.getSharedPreferences(
                PREFERENCIAS,
                Context.MODE_PRIVATE
        ).getBoolean(CLAVE_INICIADA, false);
    }

    public static String obtenerUsuario(Context context) {
        return context.getSharedPreferences(
                PREFERENCIAS,
                Context.MODE_PRIVATE
        ).getString(CLAVE_USUARIO, "");
    }

    public static void cerrar(Context context) {
        context.getSharedPreferences(
                PREFERENCIAS,
                Context.MODE_PRIVATE
        ).edit().clear().apply();
    }
}