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
 * Inicializa los 7 roles del caso de negocio MiniMarket Plus y un usuario de
 * demostración por rol al arrancar la aplicación (FR-002 de
 * specs/001-minimarket-backend-spec). Útil para demostrar la autorización basada en
 * roles sin necesidad de un proceso de registro previo.
 *
 * Usuarios creados:
 *   admin       / admin123       → ROLE_ADMINISTRADOR
 *   gerente     / gerente123     → ROLE_GERENTE_SUCURSAL
 *   jefeturno   / jefeturno123   → ROLE_JEFE_TURNO
 *   cajero      / cajero123      → ROLE_CAJERO
 *   reponedor   / reponedor123   → ROLE_REPONEDOR
 *   asistente   / asistente123   → ROLE_ASISTENTE_SERVICIO_CLIENTE
 *   cliente     / cliente123     → ROLE_CLIENTE
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
        // Crear los 7 roles del caso de negocio si no existen
        Rol administrador = getOrCreateRol("ROLE_ADMINISTRADOR");
        Rol gerente       = getOrCreateRol("ROLE_GERENTE_SUCURSAL");
        Rol jefeTurno     = getOrCreateRol("ROLE_JEFE_TURNO");
        Rol cajero        = getOrCreateRol("ROLE_CAJERO");
        Rol reponedor     = getOrCreateRol("ROLE_REPONEDOR");
        Rol asistente     = getOrCreateRol("ROLE_ASISTENTE_SERVICIO_CLIENTE");
        Rol cliente       = getOrCreateRol("ROLE_CLIENTE");

        // Crear un usuario de demostración por rol si no existen
        crearUsuario("admin",     "admin123",     Set.of(administrador));
        crearUsuario("gerente",   "gerente123",   Set.of(gerente));
        crearUsuario("jefeturno", "jefeturno123", Set.of(jefeTurno));
        crearUsuario("cajero",    "cajero123",    Set.of(cajero));
        crearUsuario("reponedor", "reponedor123", Set.of(reponedor));
        crearUsuario("asistente", "asistente123", Set.of(asistente));
        crearUsuario("cliente",   "cliente123",   Set.of(cliente));
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
