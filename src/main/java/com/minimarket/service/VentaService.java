package com.minimarket.service;

import com.minimarket.entity.Pedido;
import com.minimarket.entity.Venta;

import java.util.List;

public interface VentaService {
    List<Venta> findAll();
    Venta findById(Long id);
    Venta save(Venta venta);
    List<Venta> findByUsuarioId(Long usuarioId);

    /** Suma cantidad * precio de cada detalle de la venta. */
    double calcularTotal(Venta venta);

    /** Valida el stock de cada producto y, si todo alcanza, persiste la venta;
     *  en caso contrario lanza StockInsuficienteException. */
    Venta registrarVenta(Venta venta);

    /**
     * Registra la Venta correspondiente a un Pedido ya confirmado (FR-015), reutilizando
     * los precios ya aplicados (con promoción) en cada DetallePedido. No revalida ni
     * descuenta stock: eso ya lo hizo {@link PedidoService#confirmarPedido} a través de
     * InventarioService antes de invocar este método.
     */
    Venta registrarDesdePedido(Pedido pedido);
}
