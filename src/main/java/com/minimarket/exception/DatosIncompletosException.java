package com.minimarket.exception;

public class DatosIncompletosException extends RuntimeException {
    public DatosIncompletosException(String mensaje) {
        super(mensaje);
    }
}
