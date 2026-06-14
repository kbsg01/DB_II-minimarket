package com.minimarket.exception;

/**
 * Se lanza cuando se intenta registrar un usuario sin todos los datos
 * obligatorios (username, nombre, apellido, email, dirección).
 */
public class DatosIncompletosException extends RuntimeException {
    public DatosIncompletosException(String mensaje) {
        super(mensaje);
    }
}
