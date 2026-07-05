package com.minimarket.config;

import com.minimarket.entity.*;
import com.minimarket.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Set;

/**
 * Datos de demostración para validar la documentación OpenAPI.
 * Como H2 corre en memoria, sin este seed la BD parte vacía y no existirían
 * credenciales para autenticarse ni registros que consultar desde
 * Swagger UI o Postman (Criterios 3 y 4 de la pauta).
 *
 * Credenciales de demostración (usuario / contraseña):
 *   admin    / admin123    (ROLE_ADMIN)
 *   cajero1  / cajero123   (ROLE_CAJERO)
 *   cliente1 / cliente123  (ROLE_CLIENTE)
 */
@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner seedDatabase(RolRepository rolRepository,
                                          UsuarioRepository usuarioRepository,
                                          CategoriaRepository categoriaRepository,
                                          ProductoRepository productoRepository,
                                          InventarioRepository inventarioRepository,
                                          CarritoRepository carritoRepository,
                                          VentaRepository ventaRepository,
                                          DetalleVentaRepository detalleVentaRepository,
                                          PasswordEncoder passwordEncoder) {
        return args -> {
            // Evita duplicar datos si ya existe información (p. ej. reinicios de devtools)
            if (rolRepository.count() > 0) {
                return;
            }

            // --- Roles ---
            Rol rolAdmin = new Rol();
            rolAdmin.setNombre("ROLE_ADMIN");
            Rol rolCajero = new Rol();
            rolCajero.setNombre("ROLE_CAJERO");
            Rol rolCliente = new Rol();
            rolCliente.setNombre("ROLE_CLIENTE");
            rolRepository.save(rolAdmin);
            rolRepository.save(rolCajero);
            rolRepository.save(rolCliente);

            // --- Usuarios (contraseñas encriptadas con BCrypt) ---
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRoles(Set.of(rolAdmin));
            usuarioRepository.save(admin);

            Usuario cajero = new Usuario();
            cajero.setUsername("cajero1");
            cajero.setPassword(passwordEncoder.encode("cajero123"));
            cajero.setRoles(Set.of(rolCajero));
            usuarioRepository.save(cajero);

            Usuario cliente = new Usuario();
            cliente.setUsername("cliente1");
            cliente.setPassword(passwordEncoder.encode("cliente123"));
            cliente.setRoles(Set.of(rolCliente));
            usuarioRepository.save(cliente);

            // --- Categorías ---
            Categoria bebidas = new Categoria();
            bebidas.setNombre("Bebidas");
            Categoria snacks = new Categoria();
            snacks.setNombre("Snacks");
            Categoria lacteos = new Categoria();
            lacteos.setNombre("Lácteos");
            categoriaRepository.save(bebidas);
            categoriaRepository.save(snacks);
            categoriaRepository.save(lacteos);

            // --- Productos ---
            Producto cocaCola = nuevoProducto("Coca-Cola 1.5L", 1890.0, 24, bebidas);
            Producto aguaMineral = nuevoProducto("Agua Mineral 1.6L", 990.0, 60, bebidas);
            Producto papasFritas = nuevoProducto("Papas Fritas Corte Americano 250g", 1290.0, 50, snacks);
            Producto lecheEntera = nuevoProducto("Leche Entera 1L", 1190.0, 36, lacteos);
            productoRepository.save(cocaCola);
            productoRepository.save(aguaMineral);
            productoRepository.save(papasFritas);
            productoRepository.save(lecheEntera);

            // --- Movimientos de inventario (entradas iniciales) ---
            inventarioRepository.save(nuevoMovimiento(cocaCola, 24));
            inventarioRepository.save(nuevoMovimiento(aguaMineral, 60));
            inventarioRepository.save(nuevoMovimiento(papasFritas, 50));
            inventarioRepository.save(nuevoMovimiento(lecheEntera, 36));

            // --- Carrito del cliente ---
            Carrito item1 = new Carrito();
            item1.setUsuario(cliente);
            item1.setProducto(cocaCola);
            item1.setCantidad(3);
            carritoRepository.save(item1);

            Carrito item2 = new Carrito();
            item2.setUsuario(cliente);
            item2.setProducto(papasFritas);
            item2.setCantidad(2);
            carritoRepository.save(item2);

            // --- Venta de ejemplo con sus detalles ---
            Venta venta = new Venta();
            venta.setUsuario(cliente);
            venta.setFecha(new Date());
            ventaRepository.save(venta);

            DetalleVenta detalle1 = new DetalleVenta();
            detalle1.setVenta(venta);
            detalle1.setProducto(cocaCola);
            detalle1.setCantidad(2);
            detalle1.setPrecio(cocaCola.getPrecio());
            detalleVentaRepository.save(detalle1);

            DetalleVenta detalle2 = new DetalleVenta();
            detalle2.setVenta(venta);
            detalle2.setProducto(lecheEntera);
            detalle2.setCantidad(1);
            detalle2.setPrecio(lecheEntera.getPrecio());
            detalleVentaRepository.save(detalle2);
        };
    }

    private Producto nuevoProducto(String nombre, Double precio, Integer stock, Categoria categoria) {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setCategoria(categoria);
        return producto;
    }

    private Inventario nuevoMovimiento(Producto producto, Integer cantidad) {
        Inventario movimiento = new Inventario();
        movimiento.setProducto(producto);
        movimiento.setCantidad(cantidad);
        movimiento.setTipoMovimiento("Entrada");
        movimiento.setFechaMovimiento(new Date());
        return movimiento;
    }
}
