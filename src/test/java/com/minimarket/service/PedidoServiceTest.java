package com.minimarket.service;

import com.minimarket.entity.*;
import com.minimarket.exception.DatosIncompletosException;
import com.minimarket.exception.StockInsuficienteException;
import com.minimarket.repository.PedidoRepository;
import com.minimarket.service.impl.PedidoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Cubre US3 de specs/001-minimarket-backend-spec: disponibilidad (FR-008), revalidación
 * de stock al confirmar (FR-010, SC-006), precio con promoción (FR-011), y generación de
 * la Venta correspondiente al confirmar (FR-015, T032).
 */
@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private InventarioService inventarioService;

    @Mock
    private PromocionService promocionService;

    @Mock
    private VentaService ventaService;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private Usuario usuario;
    private Sucursal sucursal;
    private Producto producto;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("cliente");

        sucursal = new Sucursal();
        sucursal.setId(1L);
        sucursal.setNombre("Sucursal Centro");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Leche");
        producto.setPrecio(1000.0);
    }

    private Pedido pedidoConDetalle(int cantidad) {
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setSucursal(sucursal);
        pedido.setModoEntrega("RETIRO_TIENDA");

        DetallePedido detalle = new DetallePedido();
        detalle.setProducto(producto);
        detalle.setCantidad(cantidad);

        pedido.setDetalles(List.of(detalle));
        return pedido;
    }

    @Test
    void consultarDisponibilidad_delegaEnInventarioService() {
        when(inventarioService.calcularStockVigente(1L, 1L)).thenReturn(7);

        int resultado = pedidoService.consultarDisponibilidad(1L, 1L);

        assertEquals(7, resultado);
    }

    @Test
    void confirmarPedido_stockSuficiente_confirmaDescuentaYRegistraVenta() {
        Pedido pedido = pedidoConDetalle(3);
        when(inventarioService.calcularStockVigente(1L, 1L)).thenReturn(10);
        when(promocionService.calcularPrecioConPromocion(eq(producto), any())).thenReturn(1000.0);
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido resultado = pedidoService.confirmarPedido(pedido);

        assertNotNull(resultado);
        assertEquals("CONFIRMADO", resultado.getEstado());
        assertEquals(1000.0, resultado.getDetalles().get(0).getPrecioAplicado(), 0.001);
        verify(inventarioService).registrarMovimiento(any(Inventario.class));
        verify(ventaService).registrarDesdePedido(resultado);
    }

    @Test
    void confirmarPedido_stockInsuficiente_lanzaExcepcionYNoDescuenta() {
        Pedido pedido = pedidoConDetalle(5);
        when(inventarioService.calcularStockVigente(1L, 1L)).thenReturn(2);

        assertThrows(StockInsuficienteException.class, () -> pedidoService.confirmarPedido(pedido));

        verify(inventarioService, never()).registrarMovimiento(any(Inventario.class));
        verify(pedidoRepository, never()).save(any(Pedido.class));
        verify(ventaService, never()).registrarDesdePedido(any(Pedido.class));
    }

    @Test
    void confirmarPedido_sinDetalles_lanzaDatosIncompletos() {
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setSucursal(sucursal);
        pedido.setModoEntrega("RETIRO_TIENDA");
        pedido.setDetalles(List.of());

        assertThrows(DatosIncompletosException.class, () -> pedidoService.confirmarPedido(pedido));
    }

    @Test
    void confirmarPedido_sinSucursal_lanzaDatosIncompletos() {
        Pedido pedido = pedidoConDetalle(1);
        pedido.setSucursal(null);

        assertThrows(DatosIncompletosException.class, () -> pedidoService.confirmarPedido(pedido));
    }

    @Test
    void confirmarPedido_aplicaPrecioPromocional() {
        Pedido pedido = pedidoConDetalle(2);
        when(inventarioService.calcularStockVigente(1L, 1L)).thenReturn(10);
        when(promocionService.calcularPrecioConPromocion(eq(producto), any())).thenReturn(800.0);
        ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);
        when(pedidoRepository.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));

        pedidoService.confirmarPedido(pedido);

        assertEquals(800.0, captor.getValue().getDetalles().get(0).getPrecioAplicado(), 0.001);
    }
}
