package com.example.muebleriaapp;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.security.KeyStore;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.crypto.spec.GCMParameterSpec;

import javax.crypto.Cipher;

public final class AlmacenamientoSeguro {

    private static final String ANDROID_KEYSTORE =
            "AndroidKeyStore";

    private static final String ALIAS_CLAVE =
            "clave_inventario_muebleria_v1";

    private static final String NOMBRE_ARCHIVO =
            "articulos_seguro.dat";

    private static final String TRANSFORMACION =
            "AES/GCM/NoPadding";

    private static final int VERSION_ARCHIVO = 1;

    private static final int MAX_ARCHIVO_BYTES =
            5 * 1024 * 1024;

    private AlmacenamientoSeguro() {
    }
    public static boolean existe(
            Context context
    ) {
        return context
                .getFileStreamPath(NOMBRE_ARCHIVO)
                .isFile();
    }

    private static SecretKey obtenerOCrearClave()
            throws Exception {

        KeyStore keyStore = KeyStore.getInstance(
                ANDROID_KEYSTORE
        );

        keyStore.load(null);

        KeyStore.Entry entrada = keyStore.getEntry(
                ALIAS_CLAVE,
                null
        );

        if (entrada instanceof KeyStore.SecretKeyEntry) {

            KeyStore.SecretKeyEntry entradaSecreta =
                    (KeyStore.SecretKeyEntry) entrada;

            return entradaSecreta.getSecretKey();
        }

        KeyGenerator generador =
                KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES,
                        ANDROID_KEYSTORE
                );

        KeyGenParameterSpec configuracion =
                new KeyGenParameterSpec.Builder(
                        ALIAS_CLAVE,
                        KeyProperties.PURPOSE_ENCRYPT
                                | KeyProperties.PURPOSE_DECRYPT
                )
                        .setBlockModes(
                                KeyProperties.BLOCK_MODE_GCM
                        )
                        .setEncryptionPaddings(
                                KeyProperties.ENCRYPTION_PADDING_NONE
                        )
                        .setKeySize(256)
                        .build();

        generador.init(configuracion);

        return generador.generateKey();
    }
    public static void guardar(
            Context context,
            String contenido
    ) throws Exception {

        SecretKey clave = obtenerOCrearClave();

        Cipher cipher = Cipher.getInstance(
                TRANSFORMACION
        );

        cipher.init(
                Cipher.ENCRYPT_MODE,
                clave
        );

        byte[] contenidoOriginal =
                contenido.getBytes(StandardCharsets.UTF_8);

        byte[] contenidoCifrado =
                cipher.doFinal(contenidoOriginal);

        byte[] iv = cipher.getIV();

        try (
                FileOutputStream archivo =
                        context.openFileOutput(
                                NOMBRE_ARCHIVO,
                                Context.MODE_PRIVATE
                        );

                DataOutputStream salida =
                        new DataOutputStream(
                                new BufferedOutputStream(archivo)
                        )
        ) {
            salida.writeInt(VERSION_ARCHIVO);

            salida.writeInt(iv.length);
            salida.write(iv);

            salida.writeInt(contenidoCifrado.length);
            salida.write(contenidoCifrado);
        }
    }
    public static String leer(
            Context context
    ) throws Exception {

        FileInputStream archivo =
                context.openFileInput(
                        NOMBRE_ARCHIVO
                );

        try (
                DataInputStream entrada =
                        new DataInputStream(
                                new BufferedInputStream(archivo)
                        )
        ) {
            int version = entrada.readInt();

            if (version != VERSION_ARCHIVO) {
                throw new IOException(
                        "Versión de archivo no compatible"
                );
            }

            int longitudIv = entrada.readInt();

            if (longitudIv < 12 || longitudIv > 16) {
                throw new IOException(
                        "IV inválido"
                );
            }

            byte[] iv = new byte[longitudIv];
            entrada.readFully(iv);

            int longitudContenido = entrada.readInt();

            if (longitudContenido < 16
                    || longitudContenido > MAX_ARCHIVO_BYTES) {

                throw new IOException(
                        "Contenido cifrado inválido"
                );
            }

            byte[] contenidoCifrado =
                    new byte[longitudContenido];

            entrada.readFully(contenidoCifrado);

            SecretKey clave = obtenerOCrearClave();

            Cipher cipher = Cipher.getInstance(
                    TRANSFORMACION
            );

            GCMParameterSpec parametrosGcm =
                    new GCMParameterSpec(
                            128,
                            iv
                    );

            cipher.init(
                    Cipher.DECRYPT_MODE,
                    clave,
                    parametrosGcm
            );

            byte[] contenidoOriginal =
                    cipher.doFinal(contenidoCifrado);

            return new String(
                    contenidoOriginal,
                    StandardCharsets.UTF_8
            );
        }
    }
}