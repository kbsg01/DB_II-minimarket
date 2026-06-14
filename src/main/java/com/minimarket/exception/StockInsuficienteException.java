package com.minimarket.exception;

/**
 * Se lanza cuando se intenta registrar una venta cuyo detalle solicita una
 * cantidad mayor al stock disponible del producto.
 */
public class StockInsuficienteException extends RuntimeException {
    public StockInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
