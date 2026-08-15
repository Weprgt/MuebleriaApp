package com.example.muebleriaapp;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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

        try (
                BufferedReader lector = new BufferedReader(
                        new InputStreamReader(
                                context.openFileInput(NOMBRE_ARCHIVO)
                        )
                )
        ) {
            StringBuilder contenido = new StringBuilder();
            String linea;

            while ((linea = lector.readLine()) != null) {
                contenido.append(linea);
            }

            if (contenido.length() > 0) {
                JSONArray arregloJson =
                        new JSONArray(contenido.toString());

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

        } catch (FileNotFoundException e) {
            // Es normal la primera vez que se ejecuta la aplicación.
        } catch (Exception e) {
            e.printStackTrace();
        }

        datosCargados = true;
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

            try (
                    OutputStreamWriter escritor =
                            new OutputStreamWriter(
                                    context.openFileOutput(
                                            NOMBRE_ARCHIVO,
                                            Context.MODE_PRIVATE
                                    )
                            )
            ) {
                escritor.write(arregloJson.toString(2));
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}