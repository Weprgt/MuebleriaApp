package com.example.muebleriaapp;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class SeguridadContrasena {

    private static final int TAMANO_SALT = 16;
    private static final int ITERACIONES = 120000;
    private static final int TAMANO_CLAVE_BITS = 256;

    private SeguridadContrasena() {
        // Evita crear objetos de esta clase.
    }

    public static String generarSalt() {
        byte[] salt = new byte[TAMANO_SALT];
        new SecureRandom().nextBytes(salt);

        return Base64.encodeToString(
                salt,
                Base64.NO_WRAP
        );
    }

    public static String crearHash(
            String contrasena,
            String saltBase64
    ) {
        PBEKeySpec especificacion = null;

        try {
            byte[] salt = Base64.decode(
                    saltBase64,
                    Base64.NO_WRAP
            );

            especificacion = new PBEKeySpec(
                    contrasena.toCharArray(),
                    salt,
                    ITERACIONES,
                    TAMANO_CLAVE_BITS
            );

            SecretKeyFactory fabrica =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            byte[] hash =
                    fabrica.generateSecret(especificacion)
                            .getEncoded();

            return Base64.encodeToString(
                    hash,
                    Base64.NO_WRAP
            );

        } catch (Exception e) {
            throw new IllegalStateException(
                    "No se pudo proteger la contraseña",
                    e
            );

        } finally {
            if (especificacion != null) {
                especificacion.clearPassword();
            }
        }
    }

    public static boolean verificar(
            String contrasena,
            String saltBase64,
            String hashGuardado
    ) {
        try {
            String hashCalculado =
                    crearHash(contrasena, saltBase64);

            byte[] calculado = Base64.decode(
                    hashCalculado,
                    Base64.NO_WRAP
            );

            byte[] guardado = Base64.decode(
                    hashGuardado,
                    Base64.NO_WRAP
            );

            return MessageDigest.isEqual(
                    calculado,
                    guardado
            );

        } catch (Exception e) {
            return false;
        }
    }
}