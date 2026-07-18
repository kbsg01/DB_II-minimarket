package com.minimarket.service;

import com.minimarket.entity.OrdenDeCompra;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Sucursal;

import java.util.List;

public interface OrdenDeCompraService {
    List<OrdenDeCompra> findAll();
    OrdenDeCompra findById(Long id);

    /**
     * Genera una orden de compra PENDIENTE para el producto/sucursal si el stock
     * vigente cayó a su nivel mínimo o por debajo, y no existe ya una orden PENDIENTE
     * equivalente (mismo producto + sucursal + proveedor). Retorna null si no
     * corresponde generar una orden (stock suficiente, sin proveedor/mínimo
     * configurado, o ya existe una orden pendiente).
     */
    OrdenDeCompra generarSiNecesario(Producto producto, Sucursal sucursal, int stockVigente);
}
