package com.minimarket.web;

import com.minimarket.entity.*;
import com.minimarket.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica FR-013 de specs/001-minimarket-backend-spec: las respuestas de recursos
 * individuales de productos, ventas, inventario, categorías, carritos, detalle-ventas y
 * pedidos incluyen un bloque `_links` real — la prueba específica que faltó en la
 * entrega de S8 según doc/grupo7.html (que declaraba HATEOAS sin código real).
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "ADMINISTRADOR")
class HateoasLinksTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private SucursalRepository sucursalRepository;
    @Autowired private InventarioRepository inventarioRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private CarritoRepository carritoRepository;
    @Autowired private VentaRepository ventaRepository;
    @Autowired private DetalleVentaRepository detalleVentaRepository;
    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private DetallePedidoRepository detallePedidoRepository;

    @Test
    @Transactional
    void productoIndividualIncluyeLinksReales() throws Exception {
        Categoria categoria = categoriaRepository.save(nuevaCategoria());
        Producto producto = productoRepository.save(nuevoProducto(categoria));

        mockMvc.perform(get("/api/productos/" + producto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.categoria.href").exists())
                .andExpect(jsonPath("$._links.inventario.href").exists());
    }

    @Test
    @Transactional
    void productosColeccionIncluyeLinkSelf() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @Transactional
    void categoriaIndividualIncluyeLinksReales() throws Exception {
        Categoria categoria = categoriaRepository.save(nuevaCategoria());

        mockMvc.perform(get("/api/categorias/" + categoria.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.categorias.href").exists());
    }

    @Test
    @Transactional
    void inventarioIndividualIncluyeLinksReales() throws Exception {
        Categoria categoria = categoriaRepository.save(nuevaCategoria());
        Producto producto = productoRepository.save(nuevoProducto(categoria));
        Sucursal sucursal = sucursalRepository.save(nuevaSucursal());

        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setSucursal(sucursal);
        inventario.setTipoMovimiento("Entrada");
        inventario.setCantidad(5);
        inventario.setFechaMovimiento(new Date());
        inventario = inventarioRepository.save(inventario);

        mockMvc.perform(get("/api/inventario/" + inventario.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.producto.href").exists());
    }

    @Test
    @Transactional
    void carritoIndividualIncluyeLinksReales() throws Exception {
        Categoria categoria = categoriaRepository.save(nuevaCategoria());
        Producto producto = productoRepository.save(nuevoProducto(categoria));
        Usuario usuario = usuarioRepository.findByUsername("cliente").orElseThrow();

        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setProducto(producto);
        carrito.setCantidad(2);
        carrito = carritoRepository.save(carrito);

        mockMvc.perform(get("/api/carrito/" + carrito.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.producto.href").exists());
    }

    @Test
    @Transactional
    void ventaIndividualIncluyeLinksReales() throws Exception {
        Categoria categoria = categoriaRepository.save(nuevaCategoria());
        Producto producto = productoRepository.save(nuevoProducto(categoria));
        Usuario usuario = usuarioRepository.findByUsername("cliente").orElseThrow();

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setFecha(new Date());
        DetalleVenta detalle = new DetalleVenta();
        detalle.setVenta(venta);
        detalle.setProducto(producto);
        detalle.setCantidad(1);
        detalle.setPrecio(producto.getPrecio());
        venta.setDetalles(List.of(detalle));
        venta = ventaRepository.save(venta);

        mockMvc.perform(get("/api/ventas/" + venta.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.detalle.href").exists());
    }

    @Test
    @Transactional
    void detalleVentaIndividualIncluyeLinksReales() throws Exception {
        Categoria categoria = categoriaRepository.save(nuevaCategoria());
        Producto producto = productoRepository.save(nuevoProducto(categoria));
        Usuario usuario = usuarioRepository.findByUsername("cliente").orElseThrow();

        Venta venta = new Venta();
        venta.setUsuario(usuario);
        venta.setFecha(new Date());
        venta = ventaRepository.save(venta);

        DetalleVenta detalle = new DetalleVenta();
        detalle.setVenta(venta);
        detalle.setProducto(producto);
        detalle.setCantidad(1);
        detalle.setPrecio(producto.getPrecio());
        detalle = detalleVentaRepository.save(detalle);

        mockMvc.perform(get("/api/detalle-ventas/" + detalle.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.venta.href").exists());
    }

    @Test
    @Transactional
    void pedidoIndividualIncluyeLinkSelf() throws Exception {
        Categoria categoria = categoriaRepository.save(nuevaCategoria());
        Producto producto = productoRepository.save(nuevoProducto(categoria));
        Sucursal sucursal = sucursalRepository.save(nuevaSucursal());
        Usuario usuario = usuarioRepository.findByUsername("cliente").orElseThrow();

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setSucursal(sucursal);
        pedido.setModoEntrega("RETIRO_TIENDA");
        pedido.setEstado("CONFIRMADO");
        pedido.setFechaCreacion(new Date());
        pedido = pedidoRepository.save(pedido);

        DetallePedido detalle = new DetallePedido();
        detalle.setPedido(pedido);
        detalle.setProducto(producto);
        detalle.setCantidad(1);
        detalle.setPrecioAplicado(producto.getPrecio());
        detallePedidoRepository.save(detalle);

        mockMvc.perform(get("/api/pedidos/" + pedido.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    private Categoria nuevaCategoria() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Categoria HATEOAS IT-" + System.nanoTime());
        return categoria;
    }

    private Producto nuevoProducto(Categoria categoria) {
        Producto producto = new Producto();
        producto.setNombre("Producto HATEOAS");
        producto.setPrecio(1000.0);
        producto.setStock(10);
        producto.setCategoria(categoria);
        return producto;
    }

    private Sucursal nuevaSucursal() {
        Sucursal sucursal = new Sucursal();
        sucursal.setNombre("Sucursal HATEOAS IT-" + System.nanoTime());
        sucursal.setDireccion("Dirección de prueba");
        return sucursal;
    }
}
