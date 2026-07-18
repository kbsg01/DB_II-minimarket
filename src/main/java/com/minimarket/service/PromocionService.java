package com.minimarket.service;

import com.minimarket.entity.Producto;
import com.minimarket.entity.Promocion;

import java.util.Date;
import java.util.List;

public interface PromocionService {
    List<Promocion> findAll();
    Promocion findById(Long id);
    Promocion save(Promocion promocion);
    void deleteById(Long id);

    /** Promociones de un producto vigentes en la fecha indicada (fechaInicio <= fecha <= fechaFin). */
    List<Promocion> findVigentesPorProducto(Long productoId, Date fecha);

    /**
     * Precio final de un producto en una fecha, aplicando el descuento de la promoción
     * vigente más reciente (mayor fechaInicio) si existe más de una activa; si no hay
     * ninguna vigente, retorna el precio base del producto.
     */
    double calcularPrecioConPromocion(Producto producto, Date fecha);
}
