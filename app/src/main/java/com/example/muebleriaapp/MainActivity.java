package com.example.muebleriaapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Locale;

import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton btnAgregarArticulo;
    private ListView lvArticulos;
    private TextInputEditText edtBuscar;

    private ArticuloAdapter adapter;
    private final ArrayList<Articulo> articulosMostrados =
            new ArrayList<>();

    private TextView txtCantidadArticulos;
    private View estadoVacio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        // Conectar controles.
        btnAgregarArticulo =
                findViewById(R.id.btnAgregarArticulo);

        lvArticulos =
                findViewById(R.id.lvArticulos);

        edtBuscar =
                findViewById(R.id.edtBuscar);

        txtCantidadArticulos =
                findViewById(R.id.txtCantidadArticulos);

        estadoVacio =
                findViewById(R.id.estadoVacio);

        // Cargar los artículos guardados.
        DatosApp.cargarArticulos(this);

        // Crear el adaptador una sola vez.
        adapter = new ArticuloAdapter(
                this,
                articulosMostrados
        );

        lvArticulos.setAdapter(adapter);

        // Mostrar inicialmente todos los artículos.
        filtrarArticulos("");

        // Abrir el formulario para agregar.
        btnAgregarArticulo.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AgregarArticuloActivity.class
            );

            startActivity(intent);
        });

        // Abrir el detalle del artículo seleccionado.
        lvArticulos.setOnItemClickListener(
                (parent, view, position, id) -> {

                    Articulo articuloSeleccionado =
                            articulosMostrados.get(position);

                    int posicionOriginal =
                            DatosApp.listaArticulos.indexOf(
                                    articuloSeleccionado
                            );

                    if (posicionOriginal == -1) {
                        return;
                    }

                    Intent intent = new Intent(
                            MainActivity.this,
                            DetalleArticuloActivity.class
                    );

                    intent.putExtra(
                            "posicionArticulo",
                            posicionOriginal
                    );

                    startActivity(intent);
                }
        );

        // Detectar cambios en el buscador.
        edtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence texto,
                    int inicio,
                    int cantidad,
                    int despues
            ) {
                // No se necesita código aquí.
            }

            @Override
            public void onTextChanged(
                    CharSequence texto,
                    int inicio,
                    int antes,
                    int cantidad
            ) {
                filtrarArticulos(texto.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No se necesita código aquí.
            }
        });
    }

    private void filtrarArticulos(String textoBusqueda) {
        articulosMostrados.clear();

        String busquedaNormalizada =
                normalizarTexto(textoBusqueda.trim());

        if (busquedaNormalizada.isEmpty()) {
            articulosMostrados.addAll(
                    DatosApp.listaArticulos
            );

        } else {
            for (Articulo articulo : DatosApp.listaArticulos) {

                String nombre =
                        normalizarTexto(articulo.getNombre());

                String categoria =
                        normalizarTexto(articulo.getCategoria());

                if (nombre.contains(busquedaNormalizada)
                        || categoria.contains(busquedaNormalizada)) {

                    articulosMostrados.add(articulo);
                }
            }
        }

        adapter.notifyDataSetChanged();
        int cantidad = articulosMostrados.size();

        if (cantidad == 1) {
            txtCantidadArticulos.setText("1 artículo");
        } else {
            txtCantidadArticulos.setText(
                    cantidad + " artículos"
            );
        }

        boolean listaVacia = cantidad == 0;

        estadoVacio.setVisibility(
                listaVacia ? View.VISIBLE : View.GONE
        );

        lvArticulos.setVisibility(
                listaVacia ? View.GONE : View.VISIBLE
        );
    }

    private String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }

        String textoMinusculas =
                texto.toLowerCase(Locale.ROOT);

        return Normalizer.normalize(
                textoMinusculas,
                Normalizer.Form.NFD
        ).replaceAll("\\p{M}", "");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adapter == null) {
            return;
        }

        String busquedaActual = "";

        if (edtBuscar.getText() != null) {
            busquedaActual =
                    edtBuscar.getText().toString();
        }

        filtrarArticulos(busquedaActual);
    }
}