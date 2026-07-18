package com.minimarket.service;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.entity.Venta;
import com.minimarket.exception.StockInsuficienteException;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.impl.VentaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de Venta.
 *
 * Se simulan (mock) {@link VentaRepository} y {@link ProductoRepository} para aislar la
 * lógica de negocio: cálculo del total y validación de stock antes de registrar la venta.
 */
@ExtendWith(MockitoExtension.class)
public class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private PromocionService promocionService;

    @InjectMocks
    private VentaServiceImpl ventaService;

    private Producto producto(Long id, String nombre, double precio, int stock) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setPrecio(precio);
        p.setStock(stock);
        return p;
    }

    private DetalleVenta detalle(Producto producto, int cantidad, double precio) {
        DetalleVenta d = new DetalleVenta();
        d.setProducto(producto);
        d.setCantidad(cantidad);
        d.setPrecio(precio);
        return d;
    }

    private Venta ventaConUsuario(List<DetalleVenta> detalles) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("vendedor1");

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setDetalles(detalles);
        return venta;
    }

    // ---------- Cálculo de total (criterio 5: ≥2 verificaciones de cálculo) ----------

    @Test
    public void calcularTotal_variosProductos_sumaCorrecta() {
        // Arrange
        Producto leche = producto(1L, "Leche", 1000.0, 50);
        Producto pan = producto(2L, "Pan", 1500.0, 50);
        Venta venta = ventaConUsuario(List.of(
                detalle(leche, 2, 1000.0),   // 2000
                detalle(pan, 1, 1500.0)      // 1500
        ));

        // Act
        double total = ventaService.calcularTotal(venta);

        // Assert
        assertEquals(3500.0, total, 0.001);
    }

    @Test
    public void calcularTotal_cantidadesMultiples_sumaCorrecta() {
        // Arrange
        Producto arroz = producto(3L, "Arroz", 990.0, 100);
        Venta venta = ventaConUsuario(List.of(
                detalle(arroz, 5, 990.0)     // 4950
        ));

        // Act
        double total = ventaService.calcularTotal(venta);

        // Assert
        assertEquals(4950.0, total, 0.001);
    }

    // ---------- Validación de stock (criterio 3: distintos comportamientos) ----------

    @Test
    public void registrarVenta_stockSuficiente_persisteVenta() {
        // Arrange
        Producto leche = producto(1L, "Leche", 1000.0, 10);
        Venta venta = ventaConUsuario(List.of(detalle(leche, 3, 1000.0)));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(leche));
        when(ventaRepository.save(venta)).thenReturn(venta);

        // Act
        Venta resultado = ventaService.registrarVenta(venta);

        // Assert
        assertNotNull(resultado);
        verify(ventaRepository, times(1)).save(venta);
        // El stock se descontó: 10 - 3 = 7
        assertEquals(7, leche.getStock());
    }

    @Test
    public void registrarVenta_stockInsuficiente_lanzaExcepcion() {
        // Arrange
        Producto leche = producto(1L, "Leche", 1000.0, 2);
        Venta venta = ventaConUsuario(List.of(detalle(leche, 5, 1000.0)));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(leche));

        // Act + Assert
        assertThrows(StockInsuficienteException.class, () -> ventaService.registrarVenta(venta));
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    public void registrarVenta_dosDetallesMismoProducto_agregaCantidadYRechazaSiExcedeStock() {
        // Regresión de /speckit-analyze: dos DetalleVenta del mismo producto (3 + 3 = 6)
        // contra un stock de 5 no debían pasar la validación por separado. Antes del fix,
        // cada detalle se comparaba de forma independiente contra el mismo
        // Producto.stock (todavía no descontado), por lo que ambos pasaban y el segundo
        // descuento dejaba el stock en -1.
        Producto leche = producto(1L, "Leche", 1000.0, 5);
        Venta venta = ventaConUsuario(List.of(
                detalle(leche, 3, 1000.0),
                detalle(leche, 3, 1000.0)
        ));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(leche));

        assertThrows(StockInsuficienteException.class, () -> ventaService.registrarVenta(venta));

        verify(ventaRepository, never()).save(any(Venta.class));
        assertEquals(5, leche.getStock());
    }

    @Test
    public void registrarVenta_dosDetallesMismoProducto_confirmaSiStockAlcanzaParaElTotal() {
        Producto leche = producto(1L, "Leche", 1000.0, 5);
        Venta venta = ventaConUsuario(List.of(
                detalle(leche, 3, 1000.0),
                detalle(leche, 2, 1000.0)
        ));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(leche));
        when(ventaRepository.save(venta)).thenReturn(venta);

        Venta resultado = ventaService.registrarVenta(venta);

        assertNotNull(resultado);
        assertEquals(0, leche.getStock());
    }

    // ---------- Relaciones del modelo (criterio 4: ≥2 relaciones distintas) ----------

    @Test
    public void registrarVenta_relacionVentaUsuario_seConservaEnLaPersistencia() {
        // Arrange: relación Venta -> Usuario (@ManyToOne)
        Producto leche = producto(1L, "Leche", 1000.0, 10);
        Venta venta = ventaConUsuario(List.of(detalle(leche, 1, 1000.0)));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(leche));
        when(ventaRepository.save(any(Venta.class))).thenReturn(venta);

        // Act
        ventaService.registrarVenta(venta);

        // Assert: la venta enviada al repositorio mantiene el usuario asociado
        ArgumentCaptor<Venta> captor = ArgumentCaptor.forClass(Venta.class);
        verify(ventaRepository).save(captor.capture());
        assertNotNull(captor.getValue().getUsuario());
        assertEquals("vendedor1", captor.getValue().getUsuario().getUsername());
    }

    @Test
    public void registrarVenta_relacionDetalleProducto_consultaCadaProducto() {
        // Arrange: relación Venta -> DetalleVenta -> Producto
        Producto leche = producto(1L, "Leche", 1000.0, 10);
        Producto pan = producto(2L, "Pan", 1500.0, 10);
        Venta venta = ventaConUsuario(List.of(
                detalle(leche, 1, 1000.0),
                detalle(pan, 2, 1500.0)
        ));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(leche));
        when(productoRepository.findById(2L)).thenReturn(Optional.of(pan));
        when(ventaRepository.save(venta)).thenReturn(venta);

        // Act
        ventaService.registrarVenta(venta);

        // Assert: se resolvió el producto de cada detalle vía su relación
        verify(productoRepository, atLeastOnce()).findById(1L);
        verify(productoRepository, atLeastOnce()).findById(2L);
    }

    // ---------- Operaciones de delegación al repositorio (mock de BD) ----------

    @Test
    public void findAll_retornaListaDelRepositorio() {
        // Arrange
        Venta venta = ventaConUsuario(List.of());
        when(ventaRepository.findAll()).thenReturn(List.of(venta));

        // Act
        List<Venta> resultado = ventaService.findAll();

        // Assert
        assertEquals(1, resultado.size());
        verify(ventaRepository).findAll();
    }

    @Test
    public void findById_existente_retornaVenta() {
        // Arrange
        Venta venta = ventaConUsuario(List.of());
        venta.setId(7L);
        when(ventaRepository.findById(7L)).thenReturn(Optional.of(venta));

        // Act
        Venta resultado = ventaService.findById(7L);

        // Assert
        assertNotNull(resultado);
        assertEquals(7L, resultado.getId());
    }

    @Test
    public void findById_inexistente_retornaNull() {
        // Arrange
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Venta resultado = ventaService.findById(99L);

        // Assert
        assertNull(resultado);
    }

    @Test
    public void save_delegaEnRepositorio() {
        // Arrange
        Venta venta = ventaConUsuario(List.of());
        when(ventaRepository.save(venta)).thenReturn(venta);

        // Act
        Venta resultado = ventaService.save(venta);

        // Assert
        assertNotNull(resultado);
        verify(ventaRepository).save(venta);
    }

    @Test
    public void findByUsuarioId_delegaEnRepositorio() {
        // Arrange
        when(ventaRepository.findByUsuarioId(1L)).thenReturn(List.of(ventaConUsuario(List.of())));

        // Act
        List<Venta> resultado = ventaService.findByUsuarioId(1L);

        // Assert
        assertEquals(1, resultado.size());
        verify(ventaRepository).findByUsuarioId(1L);
    }
}
