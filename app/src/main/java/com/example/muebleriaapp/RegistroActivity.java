package com.example.muebleriaapp;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegistroActivity extends AppCompatActivity {

    private TextInputEditText edtUsuario;
    private TextInputEditText edtContrasena;
    private TextInputEditText edtConfirmarContrasena;
    private MaterialButton btnRegistrar;
    private MaterialButton btnVolverLogin;

    private ArticuloDbHelper dbHelper;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        dbHelper = new ArticuloDbHelper(this);

        edtUsuario =
                findViewById(R.id.edtUsuarioRegistro);

        edtContrasena =
                findViewById(R.id.edtContrasenaRegistro);

        edtConfirmarContrasena =
                findViewById(R.id.edtConfirmarContrasena);

        btnRegistrar =
                findViewById(R.id.btnRegistrar);

        btnVolverLogin =
                findViewById(R.id.btnVolverLogin);

        btnRegistrar.setOnClickListener(v ->
                registrarUsuario()
        );

        btnVolverLogin.setOnClickListener(v ->
                finish()
        );
    }

    private void registrarUsuario() {
        String usuario =
                obtenerTexto(edtUsuario).trim();

        String contrasena =
                obtenerTexto(edtContrasena);

        String confirmacion =
                obtenerTexto(edtConfirmarContrasena);

        if (usuario.isEmpty()) {
            edtUsuario.setError("Ingresa un usuario");
            edtUsuario.requestFocus();
            return;
        }

        if (usuario.length() < 3
                || usuario.length() > 30) {
            edtUsuario.setError(
                    "El usuario debe tener entre 3 y 30 caracteres"
            );
            edtUsuario.requestFocus();
            return;
        }

        if (!usuario.matches("[A-Za-z0-9._-]+")) {
            edtUsuario.setError(
                    "Usa solamente letras, números, punto, guion o guion bajo"
            );
            edtUsuario.requestFocus();
            return;
        }

        if (contrasena.length() < 8) {
            edtContrasena.setError(
                    "La contraseña debe tener al menos 8 caracteres"
            );
            edtContrasena.requestFocus();
            return;
        }

        if (!contrasena.equals(confirmacion)) {
            edtConfirmarContrasena.setError(
                    "Las contraseñas no coinciden"
            );
            edtConfirmarContrasena.requestFocus();
            return;
        }

        btnRegistrar.setEnabled(false);

        executor.execute(() -> {
            boolean usuarioExistente;
            boolean registrado = false;

            try {
                usuarioExistente =
                        dbHelper.existeUsuario(usuario);

                if (!usuarioExistente) {
                    registrado =
                            dbHelper.registrarUsuario(
                                    usuario,
                                    contrasena
                            );
                }

            } catch (Exception e) {
                usuarioExistente = false;
            }

            boolean resultadoRegistro = registrado;
            boolean resultadoExistente = usuarioExistente;

            runOnUiThread(() -> {
                btnRegistrar.setEnabled(true);

                if (resultadoExistente) {
                    edtUsuario.setError(
                            "Este usuario ya está registrado"
                    );
                    edtUsuario.requestFocus();
                    return;
                }

                if (!resultadoRegistro) {
                    Toast.makeText(
                            this,
                            "No se pudo registrar la cuenta",
                            Toast.LENGTH_SHORT
                    ).show();
                    return;
                }

                Toast.makeText(
                        this,
                        "Cuenta registrada correctamente",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
            });
        });
    }

    private String obtenerTexto(
            TextInputEditText campo
    ) {
        if (campo.getText() == null) {
            return "";
        }

        return campo.getText().toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}