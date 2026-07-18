package com.minimarket.service.impl;

import com.minimarket.entity.DetallePedido;
import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Pedido;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Venta;
import com.minimarket.exception.StockInsuficienteException;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.PromocionService;
import com.minimarket.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PromocionService promocionService;

    @Override
    public List<Venta> findAll() {
        return ventaRepository.findAll();
    }

    @Override
    public Venta findById(Long id) {
        return ventaRepository.findById(id).orElse(null);
    }

    @Override
    public Venta save(Venta venta) {
        return ventaRepository.save(venta);
    }

    @Override
    public List<Venta> findByUsuarioId(Long usuarioId) {
        return ventaRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public double calcularTotal(Venta venta) {
        if (venta == null || venta.getDetalles() == null) {
            return 0.0;
        }
        double total = 0.0;
        for (DetalleVenta detalle : venta.getDetalles()) {
            total += detalle.getCantidad() * detalle.getPrecio();
        }
        return total;
    }

    @Override
    @Transactional
    public Venta registrarVenta(Venta venta) {
        if (venta == null || venta.getUsuario() == null) {
            throw new IllegalArgumentException("La venta debe estar asociada a un usuario válido.");
        }
        if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe contener al menos un detalle.");
        }

        Date fecha = (venta.getFecha() != null) ? venta.getFecha() : new Date();
        venta.setFecha(fecha);

        // Validar stock de cada producto antes de persistir.
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = productoRepository.findById(detalle.getProducto().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "El producto del detalle no existe."));

            if (producto.getStock() < detalle.getCantidad()) {
                throw new StockInsuficienteException(
                        "Stock insuficiente para el producto '" + producto.getNombre() +
                        "'. Disponible: " + producto.getStock() +
                        ", solicitado: " + detalle.getCantidad() + ".");
            }
        }

        // Vincular cada detalle a la venta, aplicar el precio promocional vigente (FR-011)
        // y descontar stock una vez validada toda la venta.
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = productoRepository.findById(detalle.getProducto().getId()).get();
            detalle.setVenta(venta);
            detalle.setPrecio(promocionService.calcularPrecioConPromocion(producto, fecha));
            producto.setStock(producto.getStock() - detalle.getCantidad());
            productoRepository.save(producto);
        }

        return ventaRepository.save(venta);
    }

    @Override
    public Venta registrarDesdePedido(Pedido pedido) {
        Venta venta = new Venta();
        venta.setUsuario(pedido.getUsuario());
        venta.setFecha(new Date());

        List<DetalleVenta> detallesVenta = new ArrayList<>();
        for (DetallePedido detallePedido : pedido.getDetalles()) {
            DetalleVenta detalleVenta = new DetalleVenta();
            detalleVenta.setVenta(venta);
            detalleVenta.setProducto(detallePedido.getProducto());
            detalleVenta.setCantidad(detallePedido.getCantidad());
            detalleVenta.setPrecio(detallePedido.getPrecioAplicado());
            detallesVenta.add(detalleVenta);
        }
        venta.setDetalles(detallesVenta);

        return ventaRepository.save(venta);
    }
}
