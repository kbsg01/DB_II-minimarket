package com.minimarket.service;

import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.exception.DatosIncompletosException;
import com.minimarket.exception.StockInsuficienteException;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.impl.CarritoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CarritoServiceTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CarritoServiceImpl carritoService;

    private Producto producto;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Leche");
        producto.setStock(10);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("testuser");
    }

    @Test
    void agregarProductoConStockSuficiente() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(carritoRepository.findByUsuarioIdAndProductoId(1L, 1L)).thenReturn(Optional.empty());
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Carrito resultado = carritoService.agregarProducto(1L, 1L, 3);

        assertNotNull(resultado);
        assertEquals(usuario, resultado.getUsuario());
        assertEquals(producto, resultado.getProducto());
        assertEquals(3, resultado.getCantidad());
    }

    @Test
    void agregarProductoConStockExacto() {
        producto.setStock(5);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(carritoRepository.findByUsuarioIdAndProductoId(1L, 1L)).thenReturn(Optional.empty());
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Carrito resultado = carritoService.agregarProducto(1L, 1L, 5);

        assertNotNull(resultado);
        assertEquals(5, resultado.getCantidad());
    }

    @Test
    void agregarProductoSinStockLanzaExcepcion() {
        producto.setStock(2);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertThrows(StockInsuficienteException.class,
                () -> carritoService.agregarProducto(1L, 1L, 5));
    }

    @Test
    void agregarProductoStockCeroLanzaExcepcion() {
        producto.setStock(0);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        assertThrows(StockInsuficienteException.class,
                () -> carritoService.agregarProducto(1L, 1L, 1));
    }

    @Test
    void agregarProductoConCantidadCeroLanzaExcepcion() {
        assertThrows(DatosIncompletosException.class,
                () -> carritoService.agregarProducto(1L, 1L, 0));
    }

    @Test
    void agregarProductoConCantidadNegativaLanzaExcepcion() {
        assertThrows(DatosIncompletosException.class,
                () -> carritoService.agregarProducto(1L, 1L, -1));
    }

    @Test
    void carritoTieneUsuarioCorrecto() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(carritoRepository.findByUsuarioIdAndProductoId(1L, 1L)).thenReturn(Optional.empty());
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Carrito resultado = carritoService.agregarProducto(1L, 1L, 2);

        assertEquals(usuario, resultado.getUsuario());
    }

    @Test
    void carritoTieneProductoCorrecto() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(carritoRepository.findByUsuarioIdAndProductoId(1L, 1L)).thenReturn(Optional.empty());
        when(carritoRepository.save(any(Carrito.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Carrito resultado = carritoService.agregarProducto(1L, 1L, 2);

        assertEquals(producto, resultado.getProducto());
    }
}
