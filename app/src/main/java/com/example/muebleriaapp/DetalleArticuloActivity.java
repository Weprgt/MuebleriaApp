package com.example.muebleriaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import android.net.Uri;
import android.widget.ImageView;

public class DetalleArticuloActivity extends AppCompatActivity {

    private MaterialToolbar toolbarDetalle;
    private TextView txtDetalleNombre;
    private TextView txtDetallePrecio;
    private TextView txtDetalleCategoria;
    private TextView txtDetalleDescripcion;
    private MaterialButton btnEliminar;
    private MaterialButton btnEditar;
    private int posicionArticulo;
    private ImageView imgArticulo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detalle_articulo);

        // Conectar los controles del XML
        toolbarDetalle = findViewById(R.id.toolbarDetalle);
        txtDetalleNombre = findViewById(R.id.txtDetalleNombre);
        txtDetallePrecio = findViewById(R.id.txtDetallePrecio);
        txtDetalleCategoria = findViewById(R.id.txtDetalleCategoria);
        txtDetalleDescripcion = findViewById(R.id.txtDetalleDescripcion);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnEditar = findViewById(R.id.btnEditar);
        imgArticulo = findViewById(R.id.imgArticulo);

        // Acción del botón Editar
        btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(
                    DetalleArticuloActivity.this,
                    AgregarArticuloActivity.class
            );

            intent.putExtra("posicionArticulo", posicionArticulo);
            startActivity(intent);
        });

        // Acción del botón Eliminar
        btnEliminar.setOnClickListener(v ->
                mostrarConfirmacionEliminar()
        );

        // Regresar a la pantalla anterior
        toolbarDetalle.setNavigationOnClickListener(v -> finish());

        // Recibir la posición seleccionada
        posicionArticulo = getIntent().getIntExtra(
                "posicionArticulo",
                -1
        );

        // Comprobar que la posición sea válida
        if (posicionArticulo < 0
                || posicionArticulo >= DatosApp.listaArticulos.size()) {
            finish();
            return;
        }

        // Obtener el artículo seleccionado
        Articulo articulo =
                DatosApp.listaArticulos.get(posicionArticulo);

        // Mostrar sus datos
        txtDetalleNombre.setText(articulo.getNombre());
        txtDetallePrecio.setText("Q " + articulo.getPrecio());
        txtDetalleCategoria.setText(articulo.getCategoria());
        txtDetalleDescripcion.setText(articulo.getDescripcion());
        mostrarFotografia(articulo);


    }
    private void mostrarFotografia(Articulo articulo) {
        String fotoUri = articulo.getFotoUri();

        // Restablecer imagen predeterminada.
        imgArticulo.setImageURI(null);
        imgArticulo.setPadding(70, 70, 70, 70);
        imgArticulo.setScaleType(
                ImageView.ScaleType.CENTER_INSIDE
        );
        imgArticulo.setImageResource(
                R.drawable.ic_sofa
        );

        if (fotoUri == null || fotoUri.isEmpty()) {
            return;
        }

        try {
            imgArticulo.setImageTintList(null);
            imgArticulo.setPadding(0, 0, 0, 0);
            imgArticulo.setScaleType(
                    ImageView.ScaleType.CENTER_CROP
            );
            imgArticulo.setImageURI(
                    Uri.parse(fotoUri)
            );

        } catch (Exception e) {
            imgArticulo.setPadding(70, 70, 70, 70);
            imgArticulo.setScaleType(
                    ImageView.ScaleType.CENTER_INSIDE
            );
            imgArticulo.setImageResource(
                    R.drawable.ic_sofa
            );
        }
    }

    private void mostrarConfirmacionEliminar() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Eliminar artículo")
                .setMessage("¿Estás seguro de que deseas eliminar este artículo?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarArticulo())
                .show();
    }

    private void eliminarArticulo() {
        if (posicionArticulo < 0
                || posicionArticulo >= DatosApp.listaArticulos.size()) {
            return;
        }

        DatosApp.listaArticulos.remove(posicionArticulo);
        DatosApp.guardarArticulos(this);

        Snackbar.make(
                findViewById(R.id.main),
                "Artículo eliminado correctamente",
                Snackbar.LENGTH_SHORT
        ).addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(
                    Snackbar transientBottomBar,
                    int event
            ) {
                finish();
            }
        }
        ).show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (posicionArticulo < 0
                || posicionArticulo >= DatosApp.listaArticulos.size()) {
            return;
        }

        Articulo articulo =
                DatosApp.listaArticulos.get(posicionArticulo);

        txtDetalleNombre.setText(articulo.getNombre());
        txtDetallePrecio.setText("Q " + articulo.getPrecio());
        txtDetalleCategoria.setText(articulo.getCategoria());
        txtDetalleDescripcion.setText(articulo.getDescripcion());
        mostrarFotografia(articulo);
    }
}