package com.example.muebleriaapp;

import java.text.Normalizer;
import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ValidadorEntrada {

    public static final int MAX_NOMBRE = 80;
    public static final int MAX_CATEGORIA = 50;
    public static final int MAX_DESCRIPCION = 500;

    private static final BigDecimal PRECIO_MAXIMO =
            new BigDecimal("9999999.99");

    // Evita que alguien cree objetos de esta clase.
    private ValidadorEntrada() {
    }

    public static String limpiarTexto(
            String entrada,
            boolean permitirSaltos
    ) {
        if (entrada == null) {
            return "";
        }

        // Unifica caracteres como vocales con tilde.
        String normalizado = Normalizer.normalize(
                entrada,
                Normalizer.Form.NFC
        );

        StringBuilder textoLimpio = new StringBuilder();

        for (int i = 0; i < normalizado.length(); i++) {
            char caracter = normalizado.charAt(i);

            if (caracter == '\n' && permitirSaltos) {
                textoLimpio.append(caracter);

            } else if (!Character.isISOControl(caracter)) {
                textoLimpio.append(caracter);
            }
        }

        return textoLimpio.toString().trim();
    }

    public static boolean longitudValida(
            String texto,
            int longitudMaxima
    ) {
        return texto != null
                && texto.length() <= longitudMaxima;
    }
    public static String normalizarPrecio(String entrada) {

        if (entrada == null || entrada.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El precio está vacío"
            );
        }

        BigDecimal precio;

        try {
            precio = new BigDecimal(
                    entrada.trim().replace(',', '.')
            );

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "El precio no es válido"
            );
        }

        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El precio debe ser mayor que cero"
            );
        }

        if (precio.compareTo(PRECIO_MAXIMO) > 0) {
            throw new IllegalArgumentException(
                    "El precio supera el máximo permitido"
            );
        }

        precio = precio.setScale(
                2,
                RoundingMode.HALF_UP
        );

        return precio.stripTrailingZeros().toPlainString();
    }
}