package com.minimarket.config;

import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Proveedor;
import com.minimarket.entity.Rol;
import com.minimarket.entity.Sucursal;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.CategoriaRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.ProveedorRepository;
import com.minimarket.repository.RolRepository;
import com.minimarket.repository.SucursalRepository;
import com.minimarket.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Inicializa los 7 roles del caso de negocio MiniMarket Plus, un usuario de demostración
 * por rol, y los datos semilla mínimos que `quickstart.md` (Prerrequisitos) exige para
 * poder ejecutar los 4 escenarios end-to-end sin depender del H2 console: al menos una
 * `Sucursal`, un `Proveedor`, y un `Producto` con `stockMinimo`/`proveedor` configurados
 * (FR-002, FR-005, FR-006 de specs/001-minimarket-backend-spec). Antes de este hallazgo de
 * convergencia, ni `SucursalController` ni `ProveedorController` existían, y esta clase no
 * sembraba ninguna Sucursal/Proveedor/Producto: el sistema, recién levantado, no podía
 * demostrar ninguno de los escenarios de inventario o pedidos de `quickstart.md` sin
 * manipular la base directamente.
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
    private final SucursalRepository sucursalRepository;
    private final ProveedorRepository proveedorRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public DataInitializer(RolRepository rolRepository,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           SucursalRepository sucursalRepository,
                           ProveedorRepository proveedorRepository,
                           CategoriaRepository categoriaRepository,
                           ProductoRepository productoRepository) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.sucursalRepository = sucursalRepository;
        this.proveedorRepository = proveedorRepository;
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
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

        crearDatosSemillaMinimos();
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

    /**
     * Datos semilla mínimos exigidos por quickstart.md (Prerrequisitos): al menos una
     * Sucursal, un Proveedor, y un Producto con stockMinimo y proveedor asociado, para
     * poder ejecutar los 4 escenarios end-to-end contra la API real sin pasos manuales.
     */
    private void crearDatosSemillaMinimos() {
        if (!sucursalRepository.findAll().isEmpty()) {
            return; // ya sembrado en un arranque anterior
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setNombre("Distribuidora Central");
        proveedor.setContacto("contacto@distribuidoracentral.cl");
        proveedor = proveedorRepository.save(proveedor);

        Sucursal sucursal = new Sucursal();
        sucursal.setNombre("Sucursal Centro");
        sucursal.setDireccion("Av. Providencia 1234");
        sucursal.setRegion("Metropolitana");
        sucursalRepository.save(sucursal);

        Categoria categoria = new Categoria();
        categoria.setNombre("Abarrotes");
        categoria = categoriaRepository.save(categoria);

        Producto producto = new Producto();
        producto.setNombre("Arroz 1kg");
        producto.setPrecio(1500.0);
        producto.setStock(0); // el stock real se calcula por sucursal vía Inventario
        producto.setCategoria(categoria);
        producto.setStockMinimo(5);
        producto.setProveedor(proveedor);
        productoRepository.save(producto);
    }
}
