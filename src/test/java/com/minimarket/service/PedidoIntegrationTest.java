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
 * Verificación manual del Escenario 3 de quickstart.md (specs/001-minimarket-backend-spec):
 * disponibilidad, confirmación de pedido con descuento de stock y precio promocional, y
 * rechazo por stock insuficiente — contra el contexto real de Spring Boot y H2, sin
 * mocks. Es la verificación de T035.
 */
@SpringBootTest
class PedidoIntegrationTest {

    @Autowired private SucursalRepository sucursalRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PromocionRepository promocionRepository;
    @Autowired private VentaRepository ventaRepository;
    @Autowired private PedidoService pedidoService;
    @Autowired private InventarioService inventarioService;

    private Date diasDesdeHoy(int dias) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, dias);
        return cal.getTime();
    }

    @Test
    @Transactional
    void pedidoConStockSuficienteSeConfirmaDescuentaStockYGeneraVenta() {
        Sucursal sucursal = new Sucursal();
        sucursal.setNombre("Sucursal Ñuñoa");
        sucursal.setDireccion("Av. Grecia 500");
        sucursal = sucursalRepository.save(sucursal);
        final Long sucursalId = sucursal.getId();

        Categoria categoria = new Categoria();
        categoria.setNombre("Abarrotes IT-" + System.nanoTime());
        categoria = categoriaRepository.save(categoria);

        Producto producto = new Producto();
        producto.setNombre("Arroz 1kg");
        producto.setPrecio(1500.0);
        producto.setStock(0);
        producto.setCategoria(categoria);
        producto = productoRepository.save(producto);
        final Long productoId = producto.getId();

        // Promoción vigente de 10% de descuento
        Promocion promocion = new Promocion();
        promocion.setProducto(producto);
        promocion.setDescuentoPorcentaje(10.0);
        promocion.setFechaInicio(diasDesdeHoy(-1));
        promocion.setFechaFin(diasDesdeHoy(1));
        promocionRepository.save(promocion);

        // Stock inicial: 20 unidades
        Inventario entrada = new Inventario();
        entrada.setProducto(producto);
        entrada.setSucursal(sucursal);
        entrada.setTipoMovimiento("Entrada");
        entrada.setCantidad(20);
        entrada.setFechaMovimiento(new Date());
        inventarioService.registrarMovimiento(entrada);

        // Disponibilidad (FR-008)
        int disponible = pedidoService.consultarDisponibilidad(productoId, sucursalId);
        assertEquals(20, disponible);

        Usuario cliente = usuarioRepository.findByUsername("cliente").orElseThrow();

        Pedido pedido = new Pedido();
        pedido.setUsuario(cliente);
        pedido.setSucursal(sucursal);
        pedido.setModoEntrega("RETIRO_TIENDA");
        DetallePedido detalle = new DetallePedido();
        detalle.setProducto(producto);
        detalle.setCantidad(6);
        pedido.setDetalles(List.of(detalle));

        Pedido confirmado = pedidoService.confirmarPedido(pedido);

        assertEquals("CONFIRMADO", confirmado.getEstado());
        // Precio con 10% de descuento: 1500 * 0.9 = 1350
        assertEquals(1350.0, confirmado.getDetalles().get(0).getPrecioAplicado(), 0.001);

        // Stock descontado: 20 - 6 = 14
        assertEquals(14, inventarioService.calcularStockVigente(productoId, sucursalId));

        // La venta correspondiente fue generada (FR-015)
        boolean ventaGenerada = ventaRepository.findAll().stream()
                .anyMatch(v -> v.getUsuario().getId().equals(cliente.getId())
                        && v.getDetalles().stream().anyMatch(d ->
                                d.getProducto().getId().equals(productoId) && d.getCantidad() == 6));
        assertTrue(ventaGenerada, "Debe generarse una Venta a partir del pedido confirmado");
    }

    @Test
    void pedidoConStockInsuficienteSeRechaza() {
        Sucursal sucursal = new Sucursal();
        sucursal.setNombre("Sucursal Maipú");
        sucursal.setDireccion("Av. Pajaritos 2000");
        sucursal = sucursalRepository.save(sucursal);

        Categoria categoria = new Categoria();
        categoria.setNombre("Limpieza IT-" + System.nanoTime());
        categoria = categoriaRepository.save(categoria);

        Producto producto = new Producto();
        producto.setNombre("Detergente 3kg");
        producto.setPrecio(5000.0);
        producto.setStock(0);
        producto.setCategoria(categoria);
        producto = productoRepository.save(producto);

        // Sin movimientos de inventario: stock vigente = 0
        Usuario cliente = usuarioRepository.findByUsername("cliente").orElseThrow();

        Pedido pedido = new Pedido();
        pedido.setUsuario(cliente);
        pedido.setSucursal(sucursal);
        pedido.setModoEntrega("DESPACHO_DOMICILIO");
        DetallePedido detalle = new DetallePedido();
        detalle.setProducto(producto);
        detalle.setCantidad(3);
        pedido.setDetalles(List.of(detalle));

        assertThrows(StockInsuficienteException.class, () -> pedidoService.confirmarPedido(pedido));
    }
}
