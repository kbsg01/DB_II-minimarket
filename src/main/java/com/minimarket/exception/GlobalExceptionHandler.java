package com.minimarket.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Traduce las excepciones de negocio a los códigos HTTP que la documentación OpenAPI
 * declara (FR-012): sin este manejador, StockInsuficienteException/DatosIncompletosException
 * resultarían en 500 sin documentar, repitiendo el tipo de brecha entre documentación y
 * comportamiento real señalada en doc/grupo7.html.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<Map<String, String>> manejarStockInsuficiente(StockInsuficienteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", "Stock insuficiente", "message", ex.getMessage()));
    }

    @ExceptionHandler(DatosIncompletosException.class)
    public ResponseEntity<Map<String, String>> manejarDatosIncompletos(DatosIncompletosException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Datos incompletos", "message", ex.getMessage()));
    }
}
