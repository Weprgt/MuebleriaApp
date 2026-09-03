package com.example.muebleriaapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtUsuario;
    private TextInputEditText edtContrasena;
    private MaterialButton btnIniciarSesion;
    private MaterialButton btnIrRegistro;

    private ArticuloDbHelper dbHelper;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SesionUsuario.estaIniciada(this)) {
            abrirInicio();
            return;
        }

        setContentView(R.layout.activity_login);

        dbHelper = new ArticuloDbHelper(this);

        edtUsuario =
                findViewById(R.id.edtUsuario);

        edtContrasena =
                findViewById(R.id.edtContrasena);

        btnIniciarSesion =
                findViewById(R.id.btnIniciarSesion);

        btnIrRegistro =
                findViewById(R.id.btnIrRegistro);

        btnIniciarSesion.setOnClickListener(v ->
                iniciarSesion()
        );

        btnIrRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(
                    LoginActivity.this,
                    RegistroActivity.class
            );

            startActivity(intent);
        });
    }

    private void iniciarSesion() {
        String usuario =
                obtenerTexto(edtUsuario).trim();

        String contrasena =
                obtenerTexto(edtContrasena);

        if (usuario.isEmpty()) {
            edtUsuario.setError("Ingresa tu usuario");
            edtUsuario.requestFocus();
            return;
        }

        if (contrasena.isEmpty()) {
            edtContrasena.setError(
                    "Ingresa tu contraseña"
            );
            edtContrasena.requestFocus();
            return;
        }

        btnIniciarSesion.setEnabled(false);

        executor.execute(() -> {
            boolean credencialesValidas;

            try {
                credencialesValidas =
                        dbHelper.validarUsuario(
                                usuario,
                                contrasena
                        );

            } catch (Exception e) {
                credencialesValidas = false;
            }

            boolean resultado = credencialesValidas;

            runOnUiThread(() -> {
                if (isFinishing() || isDestroyed()) {
                    return;
                }

                btnIniciarSesion.setEnabled(true);

                if (!resultado) {
                    edtContrasena.setError(
                            "Usuario o contraseña incorrectos"
                    );
                    edtContrasena.requestFocus();
                    return;
                }

                SesionUsuario.iniciar(
                        this,
                        usuario
                );

                abrirInicio();
            });
        });
    }

    private void abrirInicio() {
        Intent intent = new Intent(
                this,
                InicioActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
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