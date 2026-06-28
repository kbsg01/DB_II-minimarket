package com.minimarket.config;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Inicializa roles y usuarios de prueba al arrancar la aplicación.
 * Útil para demostrar la autorización basada en roles sin necesidad de un
 * proceso de registro previo.
 *
 * Usuarios creados:
 *   admin   / admin123   → ROLE_ADMIN
 *   cajero  / cajero123  → ROLE_CAJERO
 *   cliente / cliente123 → ROLE_CLIENTE
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
    public void run(String... args) {
        // Crear roles si no existen
        Rol admin    = getOrCreateRol("ROLE_ADMIN");
        Rol cajero   = getOrCreateRol("ROLE_CAJERO");
        Rol cliente  = getOrCreateRol("ROLE_CLIENTE");

        // Crear usuarios de prueba si no existen
        crearUsuario("admin",    "admin123",    Set.of(admin));
        crearUsuario("cajero",   "cajero123",   Set.of(cajero));
        crearUsuario("cliente",  "cliente123",  Set.of(cliente));
    }

    private Rol getOrCreateRol(String nombre) {
        return rolRepository.findByNombre(nombre)
                .orElseGet(() -> {
                    Rol r = new Rol();
                    r.setNombre(nombre);
                    return rolRepository.save(r);
                });
    }

    private void crearUsuario(String username, String rawPassword, Set<Rol> roles) {
        if (usuarioRepository.findByUsername(username).isEmpty()) {
            Usuario u = new Usuario();
            u.setUsername(username);
            u.setPassword(passwordEncoder.encode(rawPassword)); // BCrypt hash
            u.setRoles(roles);
            usuarioRepository.save(u);
        }
    }
}
