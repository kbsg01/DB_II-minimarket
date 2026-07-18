package com.minimarket.service.impl;

import com.minimarket.entity.DetallePedido;
import com.minimarket.entity.Inventario;
import com.minimarket.entity.Pedido;
import com.minimarket.entity.Producto;
import com.minimarket.exception.DatosIncompletosException;
import com.minimarket.exception.StockInsuficienteException;
import com.minimarket.repository.PedidoRepository;
import com.minimarket.service.InventarioService;
import com.minimarket.service.PedidoService;
import com.minimarket.service.PromocionService;
import com.minimarket.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class PedidoServiceImpl implements PedidoService {

    private static final String ESTADO_CONFIRMADO = "CONFIRMADO";

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private PromocionService promocionService;

    @Autowired
    private VentaService ventaService;

    @Override
    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    @Override
    public Pedido findById(Long id) {
        return pedidoRepository.findById(id).orElse(null);
    }

    @Override
    public int consultarDisponibilidad(Long productoId, Long sucursalId) {
        return inventarioService.calcularStockVigente(productoId, sucursalId);
    }

    @Override
    @Transactional
    public Pedido confirmarPedido(Pedido pedido) {
        if (pedido == null || pedido.getUsuario() == null || pedido.getSucursal() == null) {
            throw new DatosIncompletosException("El pedido debe tener usuario y sucursal.");
        }
        if (pedido.getModoEntrega() == null || pedido.getModoEntrega().isBlank()) {
            throw new DatosIncompletosException("El pedido debe indicar el modo de entrega.");
        }
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new DatosIncompletosException("El pedido debe contener al menos un detalle.");
        }

        Date ahora = new Date();
        Long sucursalId = pedido.getSucursal().getId();

        // Revalidar disponibilidad de cada detalle al momento de confirmar (FR-010).
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = detalle.getProducto();
            int disponible = inventarioService.calcularStockVigente(producto.getId(), sucursalId);
            if (disponible < detalle.getCantidad()) {
                throw new StockInsuficienteException(
                        "Stock insuficiente para el producto '" + producto.getNombre() +
                        "'. Disponible: " + disponible + ", solicitado: " + detalle.getCantidad() + ".");
            }
        }

        // Aplicar precio promocional vigente y descontar stock por cada detalle (FR-011, FR-005/FR-006 vía InventarioService).
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = detalle.getProducto();
            detalle.setPedido(pedido);
            detalle.setPrecioAplicado(promocionService.calcularPrecioConPromocion(producto, ahora));

            Inventario salida = new Inventario();
            salida.setProducto(producto);
            salida.setSucursal(pedido.getSucursal());
            salida.setTipoMovimiento("Salida");
            salida.setCantidad(detalle.getCantidad());
            salida.setFechaMovimiento(ahora);
            inventarioService.registrarMovimiento(salida);
        }

        pedido.setEstado(ESTADO_CONFIRMADO);
        pedido.setFechaCreacion(ahora);
        Pedido confirmado = pedidoRepository.save(pedido);

        ventaService.registrarDesdePedido(confirmado);

        return confirmado;
    }
}
