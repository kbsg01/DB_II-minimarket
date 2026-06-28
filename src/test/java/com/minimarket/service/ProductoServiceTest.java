package com.minimarket.service;

import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.impl.ProductoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de Producto.
 *
 * Se simulan (mock) ProductoRepository para aislar la logica del servicio.
 * Cubre escenarios de exito y error para las operaciones CRUD y busqueda por categoria.
 *
 * Contexto de seguridad (S6): la restriccion de rol ADMIN sobre modificaciones se
 * aplica a nivel de SecurityConfig (@EnableMethodSecurity). Estos tests verifican
 * la correcta delegacion al repositorio; los tests de autorizacion se validan
 * mediante la configuracion de Spring Security.
 */
@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Lacteos");
    }

    private Producto producto(Long id, String nombre, double precio, int stock) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setStock(stock);
        p.setCategoria(categoria);
        return p;
    }

    // ---------- findAll ----------

    @Test
    void findAll_retornaListaCompleta() {
        Producto leche = producto(1L, "Leche", 1000.0, 50);
        Producto arroz = producto(2L, "Arroz", 990.0, 100);
        when(productoRepository.findAll()).thenReturn(List.of(leche, arroz));

        List<Producto> resultado = productoService.findAll();

        assertEquals(2, resultado.size());
        verify(productoRepository).findAll();
    }

    @Test
    void findAll_sinProductos_retornaListaVacia() {
        when(productoRepository.findAll()).thenReturn(List.of());

        List<Producto> resultado = productoService.findAll();

        assertTrue(resultado.isEmpty());
        verify(productoRepository).findAll();
    }

    // ---------- findById ----------

    @Test
    void findById_existente_retornaProducto() {
        Producto leche = producto(1L, "Leche", 1000.0, 50);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(leche));

        Producto resultado = productoService.findById(1L);

        assertNotNull(resultado);
        assertEquals("Leche", resultado.getNombre());
        assertEquals(1000.0, resultado.getPrecio(), 0.001);
    }

    @Test
    void findById_inexistente_retornaNull() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        Producto resultado = productoService.findById(99L);

        assertNull(resultado);
        verify(productoRepository).findById(99L);
    }

    // ---------- save (escenarios de exito - equivale a operacion de administrador) ----------

    @Test
    void save_nuevoProducto_persisteYRetornaConId() {
        Producto nuevo = producto(null, "Pan", 1200.0, 30);
        Producto guardado = producto(5L, "Pan", 1200.0, 30);
        when(productoRepository.save(nuevo)).thenReturn(guardado);

        Producto resultado = productoService.save(nuevo);

        assertNotNull(resultado);
        assertEquals(5L, resultado.getId());
        assertEquals("Pan", resultado.getNombre());
        verify(productoRepository, times(1)).save(nuevo);
    }

    @Test
    void save_actualizaProductoExistente_retornaDatosActualizados() {
        Producto actualizado = producto(1L, "Leche Descremada", 1100.0, 45);
        when(productoRepository.save(actualizado)).thenReturn(actualizado);

        Producto resultado = productoService.save(actualizado);

        assertEquals("Leche Descremada", resultado.getNombre());
        assertEquals(1100.0, resultado.getPrecio(), 0.001);
        verify(productoRepository).save(actualizado);
    }

    // ---------- deleteById ----------

    @Test
    void deleteById_existente_delegaEnRepositorio() {
        productoService.deleteById(1L);

        verify(productoRepository).deleteById(1L);
    }

    @Test
    void deleteById_idInexistente_noLanzaExcepcion() {
        doNothing().when(productoRepository).deleteById(99L);

        assertDoesNotThrow(() -> productoService.deleteById(99L));
        verify(productoRepository).deleteById(99L);
    }

    // ---------- findByCategoriaId ----------

    @Test
    void findByCategoriaId_conProductos_retornaListaDeLaCategoria() {
        Producto p1 = producto(1L, "Leche", 1000.0, 50);
        Producto p2 = producto(2L, "Yogurt", 800.0, 30);
        when(productoRepository.findByCategoriaId(1L)).thenReturn(List.of(p1, p2));

        List<Producto> resultado = productoService.findByCategoriaId(1L);

        assertEquals(2, resultado.size());
        verify(productoRepository).findByCategoriaId(1L);
    }

    @Test
    void findByCategoriaId_sinProductosEnCategoria_retornaListaVacia() {
        when(productoRepository.findByCategoriaId(99L)).thenReturn(List.of());

        List<Producto> resultado = productoService.findByCategoriaId(99L);

        assertTrue(resultado.isEmpty());
        verify(productoRepository).findByCategoriaId(99L);
    }

    // ---------- Relaciones del modelo ----------

    @Test
    void save_producto_mantieneRelacionConCategoria() {
        Producto p = producto(null, "Mantequilla", 1500.0, 20);
        Producto guardado = producto(3L, "Mantequilla", 1500.0, 20);
        when(productoRepository.save(p)).thenReturn(guardado);

        Producto resultado = productoService.save(p);

        assertNotNull(resultado.getCategoria());
        assertEquals("Lacteos", resultado.getCategoria().getNombre());
    }

    @Test
    void findById_productoConCategoria_retornaRelacionCompleta() {
        Producto p = producto(2L, "Queso", 2000.0, 15);
        when(productoRepository.findById(2L)).thenReturn(Optional.of(p));

        Producto resultado = productoService.findById(2L);

        assertNotNull(resultado);
        assertNotNull(resultado.getCategoria());
        assertEquals(1L, resultado.getCategoria().getId());
    }
}
