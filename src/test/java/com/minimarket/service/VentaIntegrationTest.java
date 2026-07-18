package com.minimarket.service;

import com.minimarket.entity.*;
import com.minimarket.exception.StockInsuficienteException;
import com.minimarket.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verificación contra el contexto real de Spring Boot y H2, sin mocks, de T058
 * (/speckit-converge, segunda pasada): antes de esta corrección,
 * {@code VentaController.guardarVenta} llamaba a {@code VentaService.save()} en vez de
 * {@code VentaService.registrarVenta()}, por lo que una venta directa en tienda no
 * validaba ni descontaba stock, no aplicaba el precio promocional vigente, y de hecho
 * fallaba con {@code HttpMessageNotWritableException}/constraint violation al no vincular
 * cada {@link DetalleVenta} a su {@link Venta} antes de persistir (verificado en vivo:
 * {@code POST /api/ventas} respondía {@code HTTP 500}). Estas pruebas ejercitan
 * {@code VentaService.registrarVenta} — el método real ahora conectado al endpoint — con
 * persistencia real para confirmar que ninguno de esos defectos se reproduce.
 */
@SpringBootTest
class VentaIntegrationTest {

    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PromocionRepository promocionRepository;
    @Autowired private VentaService ventaService;

    private Date diasDesdeHoy(int dias) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, dias);
        return cal.getTime();
    }

    @Test
    @Transactional
    void ventaConStockSuficienteDescuentaStockYAplicaPromocionVigente() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Abarrotes IT-" + System.nanoTime());
        categoria = categoriaRepository.save(categoria);

        Producto producto = new Producto();
        producto.setNombre("Leche 1L");
        producto.setPrecio(1000.0);
        producto.setStock(10);
        producto.setCategoria(categoria);
        producto = productoRepository.save(producto);

        // Promoción vigente de 20% de descuento
        Promocion promocion = new Promocion();
        promocion.setProducto(producto);
        promocion.setDescuentoPorcentaje(20.0);
        promocion.setFechaInicio(diasDesdeHoy(-1));
        promocion.setFechaFin(diasDesdeHoy(1));
        promocionRepository.save(promocion);

        Usuario cajero = usuarioRepository.findByUsername("cajero").orElseThrow();

        Venta venta = new Venta();
        venta.setUsuario(cajero);
        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(producto);
        detalle.setCantidad(3);
        detalle.setPrecio(1.0); // precio arbitrario enviado por el cliente: debe ser ignorado
        venta.setDetalles(List.of(detalle));

        Venta registrada = ventaService.registrarVenta(venta);

        assertNotNull(registrada.getId());
        // Precio con 20% de descuento: 1000 * 0.8 = 800 (no el 1.0 enviado por el cliente)
        assertEquals(800.0, registrada.getDetalles().get(0).getPrecio(), 0.001);
        // Stock descontado: 10 - 3 = 7
        Producto productoActualizado = productoRepository.findById(producto.getId()).orElseThrow();
        assertEquals(7, productoActualizado.getStock());
    }

    @Test
    void ventaConStockInsuficienteSeRechazaYNoDescuentaStock() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Bebidas IT-" + System.nanoTime());
        categoria = categoriaRepository.save(categoria);

        Producto producto = new Producto();
        producto.setNombre("Bebida 1.5L");
        producto.setPrecio(1200.0);
        producto.setStock(2);
        producto.setCategoria(categoria);
        producto = productoRepository.save(producto);
        final Long productoId = producto.getId();

        Usuario cajero = usuarioRepository.findByUsername("cajero").orElseThrow();

        Venta venta = new Venta();
        venta.setUsuario(cajero);
        DetalleVenta detalle = new DetalleVenta();
        detalle.setProducto(producto);
        detalle.setCantidad(500);
        detalle.setPrecio(1.0);
        venta.setDetalles(List.of(detalle));

        assertThrows(StockInsuficienteException.class, () -> ventaService.registrarVenta(venta));

        Producto productoSinCambios = productoRepository.findById(productoId).orElseThrow();
        assertEquals(2, productoSinCambios.getStock());
    }
}
