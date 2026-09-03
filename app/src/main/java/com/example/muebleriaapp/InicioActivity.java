package com.example.muebleriaapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.TextView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.button.MaterialButton;

public class InicioActivity extends AppCompatActivity {

    private MaterialButton btnVerInventario;
    private MaterialButton btnAgregarDesdeInicio;
    private TextView txtUsuarioSesion;
    private MaterialButton btnCerrarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!SesionUsuario.estaIniciada(this)) {
            abrirLogin();
            return;
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);

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

        btnVerInventario = findViewById(R.id.btnVerInventario);
        btnAgregarDesdeInicio =
                findViewById(R.id.btnAgregarDesdeInicio);

        btnVerInventario.setOnClickListener(v -> {
            Intent intent = new Intent(
                    InicioActivity.this,
                    MainActivity.class
            );
            startActivity(intent);
        });

        btnAgregarDesdeInicio.setOnClickListener(v -> {
            Intent intent = new Intent(
                    InicioActivity.this,
                    AgregarArticuloActivity.class
            );
            startActivity(intent);
        });

        txtUsuarioSesion =
                findViewById(R.id.txtUsuarioSesion);

        btnCerrarSesion =
                findViewById(R.id.btnCerrarSesion);

        txtUsuarioSesion.setText(
                "Sesión: " + SesionUsuario.obtenerUsuario(this)
        );

        btnCerrarSesion.setOnClickListener(v ->
                mostrarConfirmacionCerrarSesion()
        );
    }
    private void mostrarConfirmacionCerrarSesion() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Cerrar sesión")
                .setMessage(
                        "¿Deseas cerrar la sesión actual?"
                )
                .setNegativeButton("Cancelar", null)
                .setPositiveButton(
                        "Cerrar sesión",
                        (dialog, which) -> {
                            SesionUsuario.cerrar(this);
                            abrirLogin();
                        }
                )
                .show();
    }

    private void abrirLogin() {
        Intent intent = new Intent(
                this,
                LoginActivity.class
        );

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}