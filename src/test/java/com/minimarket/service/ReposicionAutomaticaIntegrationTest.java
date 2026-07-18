package com.minimarket.service;

import com.minimarket.entity.*;
import com.minimarket.repository.*;
import com.minimarket.service.dto.RotacionProductoDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verificación manual del Escenario 2 de quickstart.md (specs/001-minimarket-backend-spec):
 * inventario por sucursal, reposición automática al cruzar el stock mínimo, y reporte de
 * rotación — contra el contexto real de Spring Boot y la base H2, sin mocks. Es la
 * verificación de T024.
 */
@SpringBootTest
class ReposicionAutomaticaIntegrationTest {

    @Autowired private SucursalRepository sucursalRepository;
    @Autowired private ProveedorRepository proveedorRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private VentaRepository ventaRepository;
    @Autowired private InventarioService inventarioService;
    @Autowired private OrdenDeCompraRepository ordenDeCompraRepository;
    @Autowired private ReporteService reporteService;

    @Test
    void movimientoDeSalidaBajoElMinimoGeneraOrdenDeCompraAutomatica() {
        Sucursal sucursal = new Sucursal();
        sucursal.setNombre("Sucursal Providencia");
        sucursal.setDireccion("Av. Providencia 1234");
        sucursal = sucursalRepository.save(sucursal);

        Proveedor proveedor = new Proveedor();
        proveedor.setNombre("Distribuidora Lácteos Ltda.");
        proveedor = proveedorRepository.save(proveedor);

        Categoria categoria = new Categoria();
        categoria.setNombre("Lácteos IT-" + System.nanoTime());
        categoria = categoriaRepository.save(categoria);

        Producto producto = new Producto();
        producto.setNombre("Leche Entera 1L");
        producto.setPrecio(1200.0);
        producto.setStock(0);
        producto.setStockMinimo(5);
        producto.setCategoria(categoria);
        producto.setProveedor(proveedor);
        producto = productoRepository.save(producto);

        final Long productoId = producto.getId();
        final Long sucursalId = sucursal.getId();
        final Long proveedorId = proveedor.getId();

        // Entrada inicial: 10 unidades
        Inventario entrada = new Inventario();
        entrada.setProducto(producto);
        entrada.setSucursal(sucursal);
        entrada.setTipoMovimiento("Entrada");
        entrada.setCantidad(10);
        entrada.setFechaMovimiento(new Date());
        inventarioService.registrarMovimiento(entrada);

        assertEquals(10, inventarioService.calcularStockVigente(producto.getId(), sucursal.getId()));

        // Salida que cruza el stock mínimo (10 - 7 = 3, bajo el mínimo de 5)
        Inventario salida = new Inventario();
        salida.setProducto(producto);
        salida.setSucursal(sucursal);
        salida.setTipoMovimiento("Salida");
        salida.setCantidad(7);
        salida.setFechaMovimiento(new Date());
        inventarioService.registrarMovimiento(salida);

        int stockVigente = inventarioService.calcularStockVigente(producto.getId(), sucursal.getId());
        assertEquals(3, stockVigente);

        List<OrdenDeCompra> ordenes = ordenDeCompraRepository.findAll().stream()
                .filter(o -> o.getProducto().getId().equals(productoId)
                        && o.getSucursal().getId().equals(sucursalId))
                .toList();
        assertEquals(1, ordenes.size(), "Debe generarse exactamente una orden de compra");
        assertEquals("PENDIENTE", ordenes.get(0).getEstado());
        assertEquals(proveedorId, ordenes.get(0).getProveedor().getId());

        // Repetir otro movimiento de salida pequeño: no debe duplicar la orden pendiente
        Inventario salida2 = new Inventario();
        salida2.setProducto(producto);
        salida2.setSucursal(sucursal);
        salida2.setTipoMovimiento("Salida");
        salida2.setCantidad(1);
        salida2.setFechaMovimiento(new Date());
        inventarioService.registrarMovimiento(salida2);

        long totalOrdenes = ordenDeCompraRepository.findAll().stream()
                .filter(o -> o.getProducto().getId().equals(productoId)
                        && o.getSucursal().getId().equals(sucursalId))
                .count();
        assertEquals(1, totalOrdenes, "No debe generarse una segunda orden mientras la primera siga PENDIENTE");
    }

    @Test
    void reporteDeRotacionReflejaVentasRealesEnElRango() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Bebidas IT-" + System.nanoTime());
        categoria = categoriaRepository.save(categoria);

        Producto producto = new Producto();
        producto.setNombre("Bebida Cola 500ml");
        producto.setPrecio(900.0);
        producto.setStock(100);
        producto.setCategoria(categoria);
        producto = productoRepository.save(producto);
        final Long productoId = producto.getId();

        Usuario usuario = usuarioRepository.findByUsername("cajero").orElseThrow();

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setFecha(new Date());
        DetalleVenta detalle = new DetalleVenta();
        detalle.setVenta(venta);
        detalle.setProducto(producto);
        detalle.setCantidad(4);
        detalle.setPrecio(900.0);
        venta.setDetalles(List.of(detalle));
        ventaRepository.save(venta);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date desde = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 2);
        Date hasta = cal.getTime();

        List<RotacionProductoDTO> rotacion = reporteService.obtenerRotacion(desde, hasta);

        assertTrue(rotacion.stream().anyMatch(r ->
                r.getProductoId().equals(productoId) && r.getCantidadVendida() == 4));
    }

    @Test
    void reporteDeRotacionSinVentasEnElRangoRetornaListaVacia() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -5);
        Date desde = cal.getTime();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        Date hasta = cal.getTime();

        List<RotacionProductoDTO> rotacion = reporteService.obtenerRotacion(desde, hasta);

        assertNotNull(rotacion);
        assertTrue(rotacion.isEmpty());
    }
}
