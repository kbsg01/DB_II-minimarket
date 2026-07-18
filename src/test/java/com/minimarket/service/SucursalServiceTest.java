package com.minimarket.service;

import com.minimarket.entity.Sucursal;
import com.minimarket.repository.SucursalRepository;
import com.minimarket.service.impl.SucursalServiceImpl;
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
 * Pruebas unitarias de SucursalService (hallazgo de convergencia: contracts/openapi.yaml
 * y quickstart.md ya declaraban "sucursales" pero no existía ningún servicio/controlador
 * real que lo respaldara).
 */
@ExtendWith(MockitoExtension.class)
class SucursalServiceTest {

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private SucursalServiceImpl sucursalService;

    private Sucursal sucursal(Long id, String nombre) {
        Sucursal s = new Sucursal();
        s.setId(id);
        s.setNombre(nombre);
        s.setDireccion("Av. Siempre Viva 123");
        s.setRegion("Metropolitana");
        return s;
    }

    @Test
    void findAll_devuelveTodasLasSucursales() {
        when(sucursalRepository.findAll()).thenReturn(List.of(sucursal(1L, "Centro"), sucursal(2L, "Maipú")));

        List<Sucursal> resultado = sucursalService.findAll();

        assertEquals(2, resultado.size());
    }

    @Test
    void findById_existente_devuelveSucursal() {
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(sucursal(1L, "Centro")));

        Sucursal resultado = sucursalService.findById(1L);

        assertNotNull(resultado);
        assertEquals("Centro", resultado.getNombre());
    }

    @Test
    void findById_inexistente_devuelveNull() {
        when(sucursalRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(sucursalService.findById(99L));
    }

    @Test
    void save_delegaEnRepositorio() {
        Sucursal nueva = sucursal(null, "Ñuñoa");
        Sucursal guardada = sucursal(1L, "Ñuñoa");
        when(sucursalRepository.save(nueva)).thenReturn(guardada);

        Sucursal resultado = sucursalService.save(nueva);

        assertEquals(1L, resultado.getId());
        verify(sucursalRepository).save(nueva);
    }

    @Test
    void deleteById_delegaEnRepositorio() {
        sucursalService.deleteById(1L);

        verify(sucursalRepository).deleteById(1L);
    }
}
