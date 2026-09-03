package com.example.muebleriaapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;

public class AgregarArticuloActivity extends AppCompatActivity {

    private EditText edtNombre;
    private EditText edtPrecio;
    private EditText edtCategoria;
    private EditText edtDescripcion;
    private Button btnGuardar;
    private ImageView imgArticulo;
    private Button btnSeleccionarFoto;
    private String fotoUri = "";
    private Button btnTomarFoto;
    private Uri uriFotoCamara;

    // Debe ser una variable de la clase
    private int posicionArticulo = -1;
    private ArticuloDbHelper dbHelper;


    private final ActivityResultLauncher<PickVisualMediaRequest>
            selectorFoto = registerForActivityResult(
            new ActivityResultContracts.PickVisualMedia(),
            uri -> {
                if (uri != null) {
                    fotoUri = uri.toString();
                    mostrarFotografia(uri);

                    try {
                        getContentResolver().takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );
                    } catch (SecurityException e) {
                        // No se muestra ni registra la URI de la imagen.
                    }
                }
            }
    );

    private final ActivityResultLauncher<String> permisoCamara =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    permisoConcedido -> {
                        if (permisoConcedido) {
                            abrirCamara();
                        } else {
                            Toast.makeText(
                                    this,
                                    "Se necesita permiso para usar la cámara",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );

    private final ActivityResultLauncher<Uri> tomarFotografia =
            registerForActivityResult(
                    new ActivityResultContracts.TakePicture(),
                    fotografiaGuardada -> {
                        if (fotografiaGuardada) {
                            fotoUri = uriFotoCamara.toString();
                            mostrarFotografia(uriFotoCamara);
                        } else {
                            uriFotoCamara = null;

                            Toast.makeText(
                                    this,
                                    "No se tomó ninguna fotografía",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_articulo);
        dbHelper = new ArticuloDbHelper(this);

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

        edtNombre = findViewById(R.id.edtNombre);
        edtPrecio = findViewById(R.id.edtPrecio);
        edtCategoria = findViewById(R.id.edtCategoria);
        edtDescripcion = findViewById(R.id.edtDescripcion);
        btnGuardar = findViewById(R.id.btnGuardar);
        imgArticulo = findViewById(R.id.imgArticulo);
        btnSeleccionarFoto = findViewById(R.id.btnSeleccionarFoto);
        btnTomarFoto = findViewById(R.id.btnTomarFoto);

        btnSeleccionarFoto.setOnClickListener(v -> {
            PickVisualMediaRequest solicitud =
                    new PickVisualMediaRequest.Builder()
                            .setMediaType(
                                    ActivityResultContracts
                                            .PickVisualMedia
                                            .ImageOnly
                                            .INSTANCE
                            )
                            .build();

            selectorFoto.launch(solicitud);
        });

        btnTomarFoto.setOnClickListener(v -> comprobarPermisoCamara());

        // Saber si estamos agregando o editando
        posicionArticulo = getIntent().getIntExtra(
                "posicionArticulo",
                -1
        );

        if (posicionArticulo >= 0
                && posicionArticulo < DatosApp.listaArticulos.size()) {

            Articulo articulo =
                    DatosApp.listaArticulos.get(posicionArticulo);

            fotoUri = articulo.getFotoUri();

            edtNombre.setText(articulo.getNombre());
            edtPrecio.setText(articulo.getPrecio());
            edtCategoria.setText(articulo.getCategoria());
            edtDescripcion.setText(articulo.getDescripcion());

            if (fotoUri != null && !fotoUri.isEmpty()) {
                mostrarFotografia(Uri.parse(fotoUri));
            }

            btnGuardar.setText("Actualizar artículo");
        }

        btnGuardar.setOnClickListener(v -> guardarArticulo());
    }

    private void comprobarPermisoCamara() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED) {

            abrirCamara();

        } else {
            permisoCamara.launch(Manifest.permission.CAMERA);
        }
    }

    private void abrirCamara() {
        try {
            File directorio = getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES
            );

            File archivoFoto = File.createTempFile(
                    "articulo_" + System.currentTimeMillis() + "_",
                    ".jpg",
                    directorio
            );

            uriFotoCamara = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    archivoFoto
            );

            tomarFotografia.launch(uriFotoCamara);

        } catch (IOException e) {
            Toast.makeText(
                    this,
                    "No se pudo crear el archivo de la fotografía",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void mostrarFotografia(Uri uri) {
        try {
            imgArticulo.setPadding(0, 0, 0, 0);
            imgArticulo.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imgArticulo.setImageURI(uri);
        } catch (Exception e) {
            imgArticulo.setPadding(58, 58, 58, 58);
            imgArticulo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imgArticulo.setImageResource(R.drawable.ic_sofa);
        }
    }

    private void guardarArticulo() {
        String nombre = ValidadorEntrada.limpiarTexto(
                edtNombre.getText().toString(),
                false
        );

        String precio;

        String categoria = ValidadorEntrada.limpiarTexto(
                edtCategoria.getText().toString(),
                false
        );

        String descripcion = ValidadorEntrada.limpiarTexto(
                edtDescripcion.getText().toString(),
                true
        );

        if (fotoUri != null && !fotoUri.isEmpty()) {
            imgArticulo.setImageURI(Uri.parse(fotoUri));
        }

        if (nombre.isEmpty()) {
            edtNombre.setError("Ingresa el nombre");
            edtNombre.requestFocus();
            return;
        }
        if (!ValidadorEntrada.longitudValida(
                nombre,
                ValidadorEntrada.MAX_NOMBRE
        )) {
            edtNombre.setError(
                    "El nombre admite hasta 80 caracteres"
            );
            edtNombre.requestFocus();
            return;
        }

        try {
            precio = ValidadorEntrada.normalizarPrecio(
                    edtPrecio.getText().toString()
            );

        } catch (IllegalArgumentException e) {
            edtPrecio.setError(
                    "Ingresa un precio válido entre Q0.01 y Q9,999,999.99"
            );
            edtPrecio.requestFocus();
            return;
        }

        if (categoria.isEmpty()) {
            edtCategoria.setError("Ingresa la categoría");
            edtCategoria.requestFocus();
            return;
        }
        if (!ValidadorEntrada.longitudValida(
                categoria,
                ValidadorEntrada.MAX_CATEGORIA
        )) {
            edtCategoria.setError(
                    "La categoría admite hasta 50 caracteres"
            );
            edtCategoria.requestFocus();
            return;
        }

        if (descripcion.isEmpty()) {
            edtDescripcion.setError("Ingresa una descripción");
            edtDescripcion.requestFocus();
            return;
        }
        if (!ValidadorEntrada.longitudValida(
                descripcion,
                ValidadorEntrada.MAX_DESCRIPCION
        )) {
            edtDescripcion.setError(
                    "La descripción admite hasta 500 caracteres"
            );
            edtDescripcion.requestFocus();
            return;
        }

        Articulo articulo;

        if (posicionArticulo >= 0
                && posicionArticulo < DatosApp.listaArticulos.size()) {

            Articulo articuloAnterior =
                    DatosApp.listaArticulos.get(posicionArticulo);

            articulo = new Articulo(
                    articuloAnterior.getId(),
                    nombre,
                    precio,
                    descripcion,
                    categoria,
                    fotoUri
            );

            boolean actualizadoCorrectamente;

            if (articuloAnterior.getId() <= 0) {
                actualizadoCorrectamente =
                        dbHelper.insertarArticulo(articulo) != -1;
            } else {
                actualizadoCorrectamente =
                        dbHelper.actualizarArticulo(articulo) > 0;
            }

            if (!actualizadoCorrectamente) {
                Toast.makeText(
                        this,
                        "No se pudo actualizar el artículo",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            DatosApp.listaArticulos.set(
                    posicionArticulo,
                    articulo
            );

            Toast.makeText(
                    this,
                    "Artículo actualizado correctamente",
                    Toast.LENGTH_SHORT
            ).show();

        } else {
            articulo = new Articulo(
                    nombre,
                    precio,
                    descripcion,
                    categoria,
                    fotoUri
            );

            long idGenerado =
                    dbHelper.insertarArticulo(articulo);

            if (idGenerado == -1) {
                Toast.makeText(
                        this,
                        "No se pudo guardar el artículo",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            DatosApp.listaArticulos.add(articulo);

            Toast.makeText(
                    this,
                    "Artículo guardado correctamente",
                    Toast.LENGTH_SHORT
            ).show();
        }

        // Se conserva temporalmente mientras migramos los datos anteriores.
        DatosApp.guardarArticulos(this);

        finish();
    }
}