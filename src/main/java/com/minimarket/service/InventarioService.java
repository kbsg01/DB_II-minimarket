package com.minimarket.service;

import com.minimarket.entity.Inventario;

import java.util.List;

public interface InventarioService {
    List<Inventario> findAll();
    Inventario findById(Long id);
    Inventario save(Inventario inventario);
    void deleteById(Long id);
    List<Inventario> findByProductoId(Long productoId);
    Inventario registrarMovimiento(Inventario inventario);

    /** Actualiza un movimiento existente aplicando las mismas validaciones que {@link #registrarMovimiento}. */
    Inventario actualizarMovimiento(Long id, Inventario inventario);

    /** Suma de movimientos ENTRADA menos SALIDA de un producto en una sucursal. */
    int calcularStockVigente(Long productoId, Long sucursalId);
}
