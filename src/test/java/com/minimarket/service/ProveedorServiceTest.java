package com.minimarket.service;

import com.minimarket.entity.Proveedor;
import com.minimarket.repository.ProveedorRepository;
import com.minimarket.service.impl.ProveedorServiceImpl;
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
 * Pruebas unitarias de ProveedorService (hallazgo de convergencia: contracts/openapi.yaml
 * ya declaraba "proveedores" pero no existía ningún servicio/controlador real que lo
 * respaldara, y FR-006 depende de que un Proveedor exista para dirigir la reposición
 * automática).
 */
@ExtendWith(MockitoExtension.class)
class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProveedorServiceImpl proveedorService;

    private Proveedor proveedor(Long id, String nombre) {
        Proveedor p = new Proveedor();
        p.setId(id);
        p.setNombre(nombre);
        p.setContacto("contacto@" + nombre.toLowerCase().replace(" ", "") + ".cl");
        return p;
    }

    @Test
    void findAll_devuelveTodosLosProveedores() {
        when(proveedorRepository.findAll()).thenReturn(List.of(proveedor(1L, "Distribuidora Central"), proveedor(2L, "Nestlé")));

        List<Proveedor> resultado = proveedorService.findAll();

        assertEquals(2, resultado.size());
    }

    @Test
    void findById_existente_devuelveProveedor() {
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(proveedor(1L, "Distribuidora Central")));

        Proveedor resultado = proveedorService.findById(1L);

        assertNotNull(resultado);
        assertEquals("Distribuidora Central", resultado.getNombre());
    }

    @Test
    void findById_inexistente_devuelveNull() {
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(proveedorService.findById(99L));
    }

    @Test
    void save_delegaEnRepositorio() {
        Proveedor nuevo = proveedor(null, "Soprole");
        Proveedor guardado = proveedor(1L, "Soprole");
        when(proveedorRepository.save(nuevo)).thenReturn(guardado);

        Proveedor resultado = proveedorService.save(nuevo);

        assertEquals(1L, resultado.getId());
        verify(proveedorRepository).save(nuevo);
    }

    @Test
    void deleteById_delegaEnRepositorio() {
        proveedorService.deleteById(1L);

        verify(proveedorRepository).deleteById(1L);
    }
}
