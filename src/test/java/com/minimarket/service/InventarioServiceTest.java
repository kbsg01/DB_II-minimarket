package com.minimarket.service;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.exception.DatosIncompletosException;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.service.impl.InventarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Arroz");
        producto.setStock(50);
    }

    @Test
    void registrarMovimientoEntradaValido() {
        Inventario inventario = new Inventario();
        inventario.setTipoMovimiento("Entrada");
        inventario.setCantidad(10);
        inventario.setProducto(producto);
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
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        Inventario resultado = inventarioService.registrarMovimiento(inventario);

        assertNotNull(resultado);
        assertEquals("Salida", resultado.getTipoMovimiento());
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
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        Inventario resultado = inventarioService.registrarMovimiento(inventario);

        assertEquals(producto, resultado.getProducto());
    }
}
