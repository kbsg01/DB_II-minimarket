package com.minimarket.service;

import com.minimarket.entity.OrdenDeCompra;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Proveedor;
import com.minimarket.entity.Sucursal;
import com.minimarket.repository.OrdenDeCompraRepository;
import com.minimarket.service.impl.OrdenDeCompraServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Cubre FR-006 y SC-004 de specs/001-minimarket-backend-spec: generación automática de
 * una orden de compra al cruzar el stock mínimo, sin duplicar si ya existe una
 * PENDIENTE equivalente.
 */
@ExtendWith(MockitoExtension.class)
class OrdenDeCompraServiceTest {

    @Mock
    private OrdenDeCompraRepository ordenDeCompraRepository;

    @InjectMocks
    private OrdenDeCompraServiceImpl ordenDeCompraService;

    private Producto producto;
    private Sucursal sucursal;
    private Proveedor proveedor;

    @BeforeEach
    void setUp() {
        proveedor = new Proveedor();
        proveedor.setId(1L);
        proveedor.setNombre("Distribuidora Central");

        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Leche");
        producto.setStockMinimo(5);
        producto.setProveedor(proveedor);

        sucursal = new Sucursal();
        sucursal.setId(1L);
        sucursal.setNombre("Sucursal Centro");
    }

    @Test
    void stockPorDebajoDelMinimoGeneraOrden() {
        when(ordenDeCompraRepository.findByProducto_IdAndSucursal_IdAndProveedor_IdAndEstado(
                1L, 1L, 1L, "PENDIENTE")).thenReturn(Optional.empty());
        when(ordenDeCompraRepository.save(any(OrdenDeCompra.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrdenDeCompra resultado = ordenDeCompraService.generarSiNecesario(producto, sucursal, 3);

        assertNotNull(resultado);
        assertEquals("PENDIENTE", resultado.getEstado());
        assertEquals(producto, resultado.getProducto());
        assertEquals(sucursal, resultado.getSucursal());
        assertEquals(proveedor, resultado.getProveedor());
        verify(ordenDeCompraRepository).save(any(OrdenDeCompra.class));
    }

    @Test
    void stockIgualAlMinimoGeneraOrden() {
        when(ordenDeCompraRepository.findByProducto_IdAndSucursal_IdAndProveedor_IdAndEstado(
                1L, 1L, 1L, "PENDIENTE")).thenReturn(Optional.empty());
        when(ordenDeCompraRepository.save(any(OrdenDeCompra.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrdenDeCompra resultado = ordenDeCompraService.generarSiNecesario(producto, sucursal, 5);

        assertNotNull(resultado);
    }

    @Test
    void stockPorSobreElMinimoNoGeneraOrden() {
        OrdenDeCompra resultado = ordenDeCompraService.generarSiNecesario(producto, sucursal, 20);

        assertNull(resultado);
        verify(ordenDeCompraRepository, never()).save(any(OrdenDeCompra.class));
    }

    @Test
    void ordenPendienteExistenteNoGeneraDuplicado() {
        OrdenDeCompra existente = new OrdenDeCompra();
        existente.setEstado("PENDIENTE");
        when(ordenDeCompraRepository.findByProducto_IdAndSucursal_IdAndProveedor_IdAndEstado(
                1L, 1L, 1L, "PENDIENTE")).thenReturn(Optional.of(existente));

        OrdenDeCompra resultado = ordenDeCompraService.generarSiNecesario(producto, sucursal, 2);

        assertNull(resultado);
        verify(ordenDeCompraRepository, never()).save(any(OrdenDeCompra.class));
    }

    @Test
    void sinProveedorNoGeneraOrden() {
        producto.setProveedor(null);

        OrdenDeCompra resultado = ordenDeCompraService.generarSiNecesario(producto, sucursal, 1);

        assertNull(resultado);
        verify(ordenDeCompraRepository, never()).save(any(OrdenDeCompra.class));
    }

    @Test
    void sinStockMinimoConfiguradoNoGeneraOrden() {
        producto.setStockMinimo(null);

        OrdenDeCompra resultado = ordenDeCompraService.generarSiNecesario(producto, sucursal, 1);

        assertNull(resultado);
        verify(ordenDeCompraRepository, never()).save(any(OrdenDeCompra.class));
    }
}
