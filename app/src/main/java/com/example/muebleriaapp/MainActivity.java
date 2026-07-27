package com.example.muebleriaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button btnAgregarArticulo;
    ListView lvArticulos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    systemBars.bottom
            );
            return insets;
        });

        // Conectar elementos del XML
        btnAgregarArticulo = findViewById(R.id.btnAgregarArticulo);
        lvArticulos = findViewById(R.id.lvArticulos);

        // Botón para abrir pantalla agregar artículo
        btnAgregarArticulo.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AgregarArticuloActivity.class
            );
            startActivity(intent);
        });
        cargarListaArticulos();
    }
    private void cargarListaArticulos() {
        Toast.makeText(
                this,
                "Artículos en memoria: " + DatosApp.listaArticulos.size(),
                Toast.LENGTH_SHORT
        ).show();
        ArrayList<String> listaMostrar = new ArrayList<>();
        for (Articulo articulo : DatosApp.listaArticulos) {
            listaMostrar.add(
                    "Nombre: " + articulo.getNombre()
                            + "\nPrecio: Q" + articulo.getPrecio()
                            + "\nCategoría: " + articulo.getCategoria()
                            + "\nDescripción: " + articulo.getDescripcion()
            );
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                listaMostrar
        );
        lvArticulos.setAdapter(adapter);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Actualiza la lista cuando regresamos de agregar artículo
        cargarListaArticulos();
    }
}