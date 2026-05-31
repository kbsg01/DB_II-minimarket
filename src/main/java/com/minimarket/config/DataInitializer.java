package com.minimarket.config;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Inicializador de datos de prueba.
 * Se ejecuta al arrancar la aplicación (CommandLineRunner) e inserta en la BD:
 *  - Roles: GERENTE, EMPLEADO, CLIENTE
 *  - Usuarios de prueba uno por cada rol (contraseñas hasheadas con BCrypt)
 *
 * Útil para demostrar y probar la autorización basada en roles sin necesidad
 * de registrar usuarios manualmente.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepository rolRepository,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        // ── 1. Crear roles si no existen ──────────────────────────────────────
        Rol rolGerente  = crearRolSiNoExiste("GERENTE");
        Rol rolEmpleado = crearRolSiNoExiste("EMPLEADO");
        Rol rolCliente  = crearRolSiNoExiste("CLIENTE");

        // ── 2. Crear usuarios de prueba si no existen ─────────────────────────

        // Gerente: acceso total
        crearUsuarioSiNoExiste("gerente", "gerente123", Set.of(rolGerente));

        // Empleado: puede gestionar productos, inventario y ventas
        crearUsuarioSiNoExiste("empleado", "empleado123", Set.of(rolEmpleado));

        // Cliente: puede navegar catálogo y gestionar su carrito
        crearUsuarioSiNoExiste("cliente", "cliente123", Set.of(rolCliente));

        System.out.println("[DataInitializer] Roles y usuarios de prueba cargados.");
    }

    /** Crea un rol solo si no existe aún en la BD. */
    private Rol crearRolSiNoExiste(String nombre) {
        return rolRepository.findByNombre(nombre).orElseGet(() -> {
            Rol rol = new Rol();
            rol.setNombre(nombre);
            return rolRepository.save(rol);
        });
    }

    /** Crea un usuario solo si el username no existe aún en la BD. */
    private void crearUsuarioSiNoExiste(String username, String passwordPlano, Set<Rol> roles) {
        if (usuarioRepository.findByUsername(username).isEmpty()) {
            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setPassword(passwordEncoder.encode(passwordPlano)); // BCrypt
            usuario.setRoles(new HashSet<>(roles));
            usuarioRepository.save(usuario);
        }
    }
}
