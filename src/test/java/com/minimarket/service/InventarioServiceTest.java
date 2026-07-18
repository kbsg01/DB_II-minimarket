package com.minimarket.service;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Sucursal;
import com.minimarket.exception.DatosIncompletosException;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.service.OrdenDeCompraService;
import com.minimarket.service.impl.InventarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static java.util.Collections.singletonList;

@ExtendWith(MockitoExtension.class)
public class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private OrdenDeCompraService ordenDeCompraService;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    private Producto producto;
    private Sucursal sucursal;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Arroz");
        producto.setStock(50);

        sucursal = new Sucursal();
        sucursal.setId(1L);
        sucursal.setNombre("Sucursal Centro");
    }

    @Test
    void registrarMovimientoEntradaValido() {
        Inventario inventario = new Inventario();
        inventario.setTipoMovimiento("Entrada");
        inventario.setCantidad(10);
        inventario.setProducto(producto);
        inventario.setSucursal(sucursal);
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        Inventario resultado = inventarioService.registrarMovimiento(inventario);

        assertNotNull(resultado);
        assertEquals("Entrada", resultado.getTipoMovimiento());
    }

    @Test
    void registrarMovimientoSalidaValido() {
        Inventario inventario = new Inventario();
        inventario.setTipoMovimiento("Salida");
        inventario.setCantidad(5);
        inventario.setProducto(producto);
        inventario.setSucursal(sucursal);
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);
        when(inventarioRepository.findByProductoIdAndSucursalId(1L, 1L)).thenReturn(List.of(inventario));

        Inventario resultado = inventarioService.registrarMovimiento(inventario);

        assertNotNull(resultado);
        assertEquals("Salida", resultado.getTipoMovimiento());
        verify(ordenDeCompraService).generarSiNecesario(producto, sucursal, -5);
    }

    @Test
    void sucursalNulaLanzaExcepcion() {
        Inventario inventario = new Inventario();
        inventario.setTipoMovimiento("Entrada");
        inventario.setCantidad(10);
        inventario.setProducto(producto);
        inventario.setSucursal(null);

        assertThrows(DatosIncompletosException.class,
                () -> inventarioService.registrarMovimiento(inventario));
    }

    @Test
    void tipoMovimientoNuloLanzaExcepcion() {
        Inventario inventario = new Inventario();
        inventario.setTipoMovimiento(null);
        inventario.setCantidad(10);
        inventario.setProducto(producto);

        assertThrows(DatosIncompletosException.class,
                () -> inventarioService.registrarMovimiento(inventario));
    }

    @Test
    void tipoMovimientoVacioLanzaExcepcion() {
        Inventario inventario = new Inventario();
        inventario.setTipoMovimiento("");
        inventario.setCantidad(10);
        inventario.setProducto(producto);

        assertThrows(DatosIncompletosException.class,
                () -> inventarioService.registrarMovimiento(inventario));
    }

    @Test
    void cantidadNulaLanzaExcepcion() {
        Inventario inventario = new Inventario();
        inventario.setTipoMovimiento("Entrada");
        inventario.setCantidad(null);
        inventario.setProducto(producto);

        assertThrows(DatosIncompletosException.class,
                () -> inventarioService.registrarMovimiento(inventario));
    }

    @Test
    void cantidadCeroLanzaExcepcion() {
        Inventario inventario = new Inventario();
        inventario.setTipoMovimiento("Entrada");
        inventario.setCantidad(0);
        inventario.setProducto(producto);

        assertThrows(DatosIncompletosException.class,
                () -> inventarioService.registrarMovimiento(inventario));
    }

    @Test
    void productoNuloLanzaExcepcion() {
        Inventario inventario = new Inventario();
        inventario.setTipoMovimiento("Entrada");
        inventario.setCantidad(10);
        inventario.setProducto(null);

        assertThrows(DatosIncompletosException.class,
                () -> inventarioService.registrarMovimiento(inventario));
    }

    @Test
    void productoAsociadoEsCorrecto() {
        Inventario inventario = new Inventario();
        inventario.setTipoMovimiento("Entrada");
        inventario.setCantidad(15);
        inventario.setProducto(producto);
        inventario.setSucursal(sucursal);
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        Inventario resultado = inventarioService.registrarMovimiento(inventario);

        assertEquals(producto, resultado.getProducto());
    }

    @Test
    void findAllRetornaLista() {
        Inventario inv = new Inventario();
        when(inventarioRepository.findAll()).thenReturn(singletonList(inv));

        List<Inventario> resultado = inventarioService.findAll();

        assertEquals(1, resultado.size());
    }

    @Test
    void findByIdRetornaInventario() {
        Inventario inv = new Inventario();
        when(inventarioRepository.findById(1L)).thenReturn(Optional.of(inv));

        Inventario resultado = inventarioService.findById(1L);

        assertEquals(inv, resultado);
    }

    @Test
    void saveGuardaInventario() {
        Inventario inv = new Inventario();
        when(inventarioRepository.save(inv)).thenReturn(inv);

        Inventario resultado = inventarioService.save(inv);

        assertEquals(inv, resultado);
    }

    @Test
    void deleteByIdEliminaInventario() {
        inventarioService.deleteById(1L);
        verify(inventarioRepository).deleteById(1L);
    }

    @Test
    void findByProductoIdRetornaLista() {
        Inventario inv = new Inventario();
        when(inventarioRepository.findByProductoId(1L)).thenReturn(singletonList(inv));

        List<Inventario> resultado = inventarioService.findByProductoId(1L);

        assertEquals(1, resultado.size());
    }

    @Test
    void actualizarMovimientoValidoAplicaMismasValidacionesQueRegistrar() {
        Inventario inventario = new Inventario();
        inventario.setTipoMovimiento("Entrada");
        inventario.setCantidad(10);
        inventario.setProducto(producto);
        inventario.setSucursal(sucursal);
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        Inventario resultado = inventarioService.actualizarMovimiento(1L, inventario);

        assertNotNull(resultado);
        assertEquals(1L, inventario.getId());
        verify(ordenDeCompraService, never()).generarSiNecesario(any(), any(), anyInt());
    }

    @Test
    void actualizarMovimientoConSucursalNulaLanzaExcepcion() {
        Inventario inventario = new Inventario();
        inventario.setTipoMovimiento("Entrada");
        inventario.setCantidad(10);
        inventario.setProducto(producto);
        inventario.setSucursal(null);

        assertThrows(DatosIncompletosException.class,
                () -> inventarioService.actualizarMovimiento(1L, inventario));
    }
}
