package com.example.muebleriaapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

public class InicioActivity extends AppCompatActivity {

    private MaterialButton btnVerInventario;
    private MaterialButton btnAgregarDesdeInicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }
}