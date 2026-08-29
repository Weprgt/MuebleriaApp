package com.example.muebleriaapp;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DatosApp {

    public static ArrayList<Articulo> listaArticulos =
            new ArrayList<>();

    private static final String NOMBRE_ARCHIVO = "articulos.json";
    private static boolean datosCargados = false;

    public static void cargarArticulos(Context context) {

        if (datosCargados) {
            return;
        }

        listaArticulos.clear();

        try {
            if (AlmacenamientoSeguro.existe(context)) {

                String contenidoCifrado =
                        AlmacenamientoSeguro.leer(context);

                cargarDesdeJson(contenidoCifrado);

                // Llegamos aquí solamente si se descifró correctamente.
                context.deleteFile(NOMBRE_ARCHIVO);

            } else {

                String contenidoAntiguo =
                        leerJsonAntiguo(context);

                cargarDesdeJson(contenidoAntiguo);

                // Cifrar los artículos del archivo anterior.
                boolean guardadoCorrectamente =
                        guardarArticulos(context);

                // El JSON se elimina solamente si el cifrado funcionó.
                if (guardadoCorrectamente) {
                    context.deleteFile(NOMBRE_ARCHIVO);
                }
            }

        } catch (FileNotFoundException e) {
            // Es normal si la aplicación todavía no tiene artículos.

        } catch (Exception e) {
            // No se registra el contenido ni la excepción.
            listaArticulos.clear();
        }

        datosCargados = true;
    }

    private static String leerJsonAntiguo(
            Context context
    ) throws Exception {

        try (
                BufferedReader lector =
                        new BufferedReader(
                                new InputStreamReader(
                                        context.openFileInput(
                                                NOMBRE_ARCHIVO
                                        )
                                )
                        )
        ) {
            StringBuilder contenido =
                    new StringBuilder();

            String linea;

            while ((linea = lector.readLine()) != null) {
                contenido.append(linea);
            }

            return contenido.toString();
        }
    }

    private static void cargarDesdeJson(
            String contenido
    ) throws Exception {

        if (contenido == null
                || contenido.trim().isEmpty()) {
            return;
        }

        JSONArray arregloJson =
                new JSONArray(contenido);

        for (int i = 0; i < arregloJson.length(); i++) {

            JSONObject objetoJson =
                    arregloJson.getJSONObject(i);

            Articulo articulo = new Articulo(
                    objetoJson.optString("nombre"),
                    objetoJson.optString("precio"),
                    objetoJson.optString("descripcion"),
                    objetoJson.optString("categoria"),
                    objetoJson.optString("fotoUri", "")
            );

            listaArticulos.add(articulo);
        }
    }

    public static boolean guardarArticulos(Context context) {
        JSONArray arregloJson = new JSONArray();

        try {
            for (Articulo articulo : listaArticulos) {
                JSONObject objetoJson = new JSONObject();

                objetoJson.put("nombre", articulo.getNombre());
                objetoJson.put("precio", articulo.getPrecio());
                objetoJson.put(
                        "descripcion",
                        articulo.getDescripcion()
                );
                objetoJson.put(
                        "categoria",
                        articulo.getCategoria()
                );
                objetoJson.put(
                        "fotoUri",
                        articulo.getFotoUri()
                );

                arregloJson.put(objetoJson);
            }

            AlmacenamientoSeguro.guardar(
                    context,
                    arregloJson.toString(2)
            );

            return true;

        } catch (Exception e) {
            // No se registra el contenido del inventario.
            return false;
        }
    }
}