package com.minimarket.service;

import com.minimarket.entity.Pedido;

import java.util.List;

public interface PedidoService {
    List<Pedido> findAll();
    Pedido findById(Long id);

    /** Cantidad disponible (stock vigente) de un producto en una sucursal (FR-008). */
    int consultarDisponibilidad(Long productoId, Long sucursalId);

    /**
     * Confirma un pedido: revalida la disponibilidad de cada detalle al momento de
     * confirmar (no solo al consultar, FR-010), aplica el precio promocional vigente de
     * cada producto (FR-011), y descuenta el stock a través de
     * {@link InventarioService#registrarMovimiento} para que la reposición automática de
     * inventario se dispare igual que con una venta en tienda. Si el stock de algún
     * detalle no alcanza, lanza {@link com.minimarket.exception.StockInsuficienteException}
     * sin descontar inventario ni persistir el pedido.
     */
    Pedido confirmarPedido(Pedido pedido);
}
