package com.example.muebleriaapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;

public class AgregarArticuloActivity extends AppCompatActivity {

    EditText edtNombre;
    EditText edtPrecio;
    EditText edtCategoria;
    EditText edtDescripcion;
    Button btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_articulo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edtNombre = findViewById(R.id.edtNombre);
        edtPrecio = findViewById(R.id.edtPrecio);
        edtDescripcion = findViewById(R.id.edtDescripcion);
        edtCategoria = findViewById(R.id.edtCategoria);

        btnGuardar = findViewById(R.id.btnGuardar);


        btnGuardar.setOnClickListener(v -> {

            String nombre = edtNombre.getText().toString();
            String precio = edtPrecio.getText().toString();
            String descripcion = edtDescripcion.getText().toString();
            String categoria = edtCategoria.getText().toString();


            Articulo articulo = new Articulo(
                    nombre,
                    precio,
                    descripcion,
                    categoria
            );


            DatosApp.listaArticulos.add(articulo);


            Toast.makeText(
                    AgregarArticuloActivity.this,
                    "Artículo guardado correctamente",
                    Toast.LENGTH_SHORT
            ).show();


            Intent intent = new Intent(
                    AgregarArticuloActivity.this,
                    MainActivity.class
            );

            startActivity(intent);

        });
    }
}