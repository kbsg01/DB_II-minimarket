package com.minimarket.service;

import com.minimarket.entity.Producto;
import com.minimarket.entity.Promocion;
import com.minimarket.exception.DatosIncompletosException;
import com.minimarket.repository.PromocionRepository;
import com.minimarket.service.impl.PromocionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Cubre FR-011 de specs/001-minimarket-backend-spec: vigencia de promociones por fecha
 * y cálculo del precio final aplicando el descuento vigente más reciente.
 */
@ExtendWith(MockitoExtension.class)
class PromocionServiceTest {

    @Mock
    private PromocionRepository promocionRepository;

    @InjectMocks
    private PromocionServiceImpl promocionService;

    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Leche");
        producto.setPrecio(1000.0);
    }

    private Date diasDesdeHoy(int dias) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, dias);
        return cal.getTime();
    }

    @Test
    void sinPromocionesRetornaPrecioBase() {
        when(promocionRepository.findByProductoId(1L)).thenReturn(List.of());

        double precio = promocionService.calcularPrecioConPromocion(producto, new Date());

        assertEquals(1000.0, precio, 0.001);
    }

    @Test
    void promocionVigenteAplicaDescuento() {
        Promocion promo = new Promocion();
        promo.setProducto(producto);
        promo.setDescuentoPorcentaje(20.0);
        promo.setFechaInicio(diasDesdeHoy(-1));
        promo.setFechaFin(diasDesdeHoy(1));
        when(promocionRepository.findByProductoId(1L)).thenReturn(List.of(promo));

        double precio = promocionService.calcularPrecioConPromocion(producto, new Date());

        assertEquals(800.0, precio, 0.001);
    }

    @Test
    void promocionVencidaNoAplicaDescuento() {
        Promocion promo = new Promocion();
        promo.setProducto(producto);
        promo.setDescuentoPorcentaje(50.0);
        promo.setFechaInicio(diasDesdeHoy(-10));
        promo.setFechaFin(diasDesdeHoy(-2));
        when(promocionRepository.findByProductoId(1L)).thenReturn(List.of(promo));

        double precio = promocionService.calcularPrecioConPromocion(producto, new Date());

        assertEquals(1000.0, precio, 0.001);
    }

    @Test
    void promocionFuturaNoAplicaDescuento() {
        Promocion promo = new Promocion();
        promo.setProducto(producto);
        promo.setDescuentoPorcentaje(50.0);
        promo.setFechaInicio(diasDesdeHoy(2));
        promo.setFechaFin(diasDesdeHoy(10));
        when(promocionRepository.findByProductoId(1L)).thenReturn(List.of(promo));

        double precio = promocionService.calcularPrecioConPromocion(producto, new Date());

        assertEquals(1000.0, precio, 0.001);
    }

    @Test
    void dosPromocionesVigentesUsaLaMasReciente() {
        Promocion antigua = new Promocion();
        antigua.setProducto(producto);
        antigua.setDescuentoPorcentaje(10.0);
        antigua.setFechaInicio(diasDesdeHoy(-5));
        antigua.setFechaFin(diasDesdeHoy(5));

        Promocion reciente = new Promocion();
        reciente.setProducto(producto);
        reciente.setDescuentoPorcentaje(30.0);
        reciente.setFechaInicio(diasDesdeHoy(-1));
        reciente.setFechaFin(diasDesdeHoy(5));

        when(promocionRepository.findByProductoId(1L)).thenReturn(List.of(antigua, reciente));

        double precio = promocionService.calcularPrecioConPromocion(producto, new Date());

        assertEquals(700.0, precio, 0.001);
    }

    @Test
    void saveRechazaFechaFinAnteriorAFechaInicio() {
        Promocion promo = new Promocion();
        promo.setProducto(producto);
        promo.setDescuentoPorcentaje(10.0);
        promo.setFechaInicio(diasDesdeHoy(5));
        promo.setFechaFin(diasDesdeHoy(1));

        assertThrows(DatosIncompletosException.class, () -> promocionService.save(promo));
    }

    @Test
    void saveRechazaFechaFinIgualAFechaInicio() {
        Date misma = new Date();
        Promocion promo = new Promocion();
        promo.setProducto(producto);
        promo.setDescuentoPorcentaje(10.0);
        promo.setFechaInicio(misma);
        promo.setFechaFin(misma);

        assertThrows(DatosIncompletosException.class, () -> promocionService.save(promo));
    }
}
