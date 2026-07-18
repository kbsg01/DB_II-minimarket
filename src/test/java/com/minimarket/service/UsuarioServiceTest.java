package com.minimarket.service;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.exception.DatosIncompletosException;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de Usuario.
 *
 * Se simula (mock) el {@link UsuarioRepository} para aislar la lógica del servicio
 * de la base de datos. Valida los datos obligatorios del usuario y el acceso por rol.
 */
@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    /** Construye un usuario con todos los datos obligatorios completos. */
    private Usuario usuarioCompleto() {
        Usuario usuario = new Usuario();
        usuario.setUsername("jperez");
        usuario.setPassword("secret123");
        usuario.setNombre("Juan");
        usuario.setApellido("Pérez");
        usuario.setEmail("jperez@correo.cl");
        usuario.setDireccion("Av. Siempre Viva 742");
        return usuario;
    }

    // ---------- Validación de datos completos (criterio 2: ≥4 pruebas) ----------

    @Test
    public void datosCompletos_todosLosCampos_retornaTrue() {
        // Arrange
        Usuario usuario = usuarioCompleto();

        // Act
        boolean resultado = usuarioService.datosCompletos(usuario);

        // Assert
        assertTrue(resultado);
    }

    @Test
    public void registrar_sinNombre_lanzaDatosIncompletosException() {
        // Arrange
        Usuario usuario = usuarioCompleto();
        usuario.setNombre(null);

        // Act + Assert
        assertThrows(DatosIncompletosException.class, () -> usuarioService.registrar(usuario));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    public void registrar_sinApellido_lanzaDatosIncompletosException() {
        // Arrange
        Usuario usuario = usuarioCompleto();
        usuario.setApellido("   "); // vacío/espacios también es incompleto

        // Act + Assert
        assertThrows(DatosIncompletosException.class, () -> usuarioService.registrar(usuario));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    public void registrar_sinEmail_lanzaDatosIncompletosException() {
        // Arrange
        Usuario usuario = usuarioCompleto();
        usuario.setEmail(null);

        // Act + Assert
        assertThrows(DatosIncompletosException.class, () -> usuarioService.registrar(usuario));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    public void registrar_sinDireccion_lanzaDatosIncompletosException() {
        // Arrange
        Usuario usuario = usuarioCompleto();
        usuario.setDireccion(null);

        // Act + Assert
        assertThrows(DatosIncompletosException.class, () -> usuarioService.registrar(usuario));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // ---------- Registro válido y consulta simulada (criterio 6: mock de BD) ----------

    @Test
    public void registrar_datosValidos_invocaSaveYRetornaUsuario() {
        // Arrange
        Usuario usuario = usuarioCompleto();
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioService.registrar(usuario);

        // Assert
        assertNotNull(resultado);
        assertEquals("jperez", resultado.getUsername());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    public void findByUsername_existente_retornaUsuario() {
        // Arrange: se simula la consulta a la base de datos
        Usuario usuario = usuarioCompleto();
        when(usuarioRepository.findByUsername("jperez")).thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> resultado = usuarioService.findByUsername("jperez");

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("Juan", resultado.get().getNombre());
        verify(usuarioRepository).findByUsername("jperez");
    }

    // ---------- Acceso por rol (criterio 3: distintos comportamientos) ----------

    @Test
    public void puedeRegistrarVenta_rolValido_retornaTrue() {
        // Arrange
        Usuario usuario = usuarioCompleto();
        usuario.setRoles(Set.of(new Rol("ROLE_CAJERO")));

        // Act
        boolean resultado = usuarioService.puedeRegistrarVenta(usuario);

        // Assert
        assertTrue(resultado);
    }

    @Test
    public void puedeRegistrarVenta_rolInvalido_retornaFalse() {
        // Arrange
        Usuario usuario = usuarioCompleto();
        usuario.setRoles(Set.of(new Rol("ROLE_CLIENTE")));

        // Act
        boolean resultado = usuarioService.puedeRegistrarVenta(usuario);

        // Assert
        assertFalse(resultado);
    }

    // ---------- Operaciones de delegación al repositorio (mock de BD) ----------

    @Test
    public void findAll_retornaListaDelRepositorio() {
        // Arrange
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioCompleto()));

        // Act
        List<Usuario> resultado = usuarioService.findAll();

        // Assert
        assertEquals(1, resultado.size());
        verify(usuarioRepository).findAll();
    }

    @Test
    public void findById_existente_retornaUsuario() {
        // Arrange
        Usuario usuario = usuarioCompleto();
        usuario.setId(10L);
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(usuario));

        // Act
        Optional<Usuario> resultado = usuarioService.findById(10L);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(10L, resultado.get().getId());
    }

    @Test
    public void save_delegaEnRepositorio() {
        // Arrange
        Usuario usuario = usuarioCompleto();
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // Act
        Usuario resultado = usuarioService.save(usuario);

        // Assert
        assertNotNull(resultado);
        verify(usuarioRepository).save(usuario);
    }

    @Test
    public void deleteById_delegaEnRepositorio() {
        // Act
        usuarioService.deleteById(5L);

        // Assert
        verify(usuarioRepository).deleteById(5L);
    }
}
