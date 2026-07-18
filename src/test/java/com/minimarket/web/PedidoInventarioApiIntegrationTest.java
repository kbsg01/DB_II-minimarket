package com.minimarket.web;

import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Proveedor;
import com.minimarket.entity.Sucursal;
import com.minimarket.repository.CategoriaRepository;
import com.minimarket.repository.OrdenDeCompraRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.ProveedorRepository;
import com.minimarket.repository.SucursalRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Regresión de un hallazgo de convergencia (2026-07-18, cuarta pasada): tanto
 * `POST /api/pedidos` como `POST /api/inventario` fallaban en vivo con el mismo patrón de
 * referencia mínima (`{"producto":{"id":X}}`) que ya usa el resto de la API (Carrito,
 * DetalleVenta, etc.), porque los servicios leían campos del objeto `Producto` recibido
 * en el body (precio, stockMinimo, proveedor) sin recargarlo desde la base:
 * - `POST /api/pedidos` respondía `HTTP 500` (NullPointerException en
 *   `Producto.getPrecio()`, vía `PromocionServiceImpl.calcularPrecioConPromocion`).
 * - `POST /api/inventario` respondía `200` pero la reposición automática (FR-006) no se
 *   disparaba nunca, en silencio, porque `producto.getStockMinimo()` era `null`.
 * Ninguna prueba existente detectó esto porque todas invocaban los servicios directamente
 * con entidades `Producto` ya gestionadas por JPA (con todos sus campos poblados), nunca a
 * través del `MockMvc`/JSON real que ejercita esta clase.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PedidoInventarioApiIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private ProductoRepository productoRepository;
    @Autowired private SucursalRepository sucursalRepository;
    @Autowired private ProveedorRepository proveedorRepository;
    @Autowired private OrdenDeCompraRepository ordenDeCompraRepository;

    private Categoria nuevaCategoria() {
        Categoria categoria = new Categoria();
        categoria.setNombre("Categoria API IT-" + System.nanoTime());
        return categoria;
    }

    private Sucursal nuevaSucursal() {
        Sucursal sucursal = new Sucursal();
        sucursal.setNombre("Sucursal API IT-" + System.nanoTime());
        sucursal.setDireccion("Dirección de prueba");
        return sucursal;
    }

    private Proveedor nuevoProveedor() {
        Proveedor proveedor = new Proveedor();
        proveedor.setNombre("Proveedor API IT-" + System.nanoTime());
        proveedor.setContacto("contacto@proveedor.cl");
        return proveedor;
    }

    @Test
    @Transactional
    @WithUserDetails("gerente")
    void postInventarioConReferenciaMinimaDisparaReposicionAutomatica() throws Exception {
        Categoria categoria = categoriaRepository.save(nuevaCategoria());
        Sucursal sucursal = sucursalRepository.save(nuevaSucursal());
        Proveedor proveedor = proveedorRepository.save(nuevoProveedor());

        Producto producto = new Producto();
        producto.setNombre("Producto API IT");
        producto.setPrecio(1000.0);
        producto.setStock(0);
        producto.setCategoria(categoria);
        producto.setStockMinimo(5);
        producto.setProveedor(proveedor);
        producto = productoRepository.save(producto);

        long ordenesAntes = ordenDeCompraRepository.count();

        String bodyEntrada = """
                {"producto":{"id":%d},"sucursal":{"id":%d},"tipoMovimiento":"Entrada","cantidad":10,"fechaMovimiento":"2026-07-18T12:00:00.000+00:00"}
                """.formatted(producto.getId(), sucursal.getId());
        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyEntrada))
                .andExpect(status().isOk());

        // Salida que deja el stock (10 - 7 = 3) bajo el mínimo (5): con solo {"id": X} en
        // el body, como cualquier cliente real de esta API. Antes de la corrección, esto
        // no generaba ninguna orden de compra, sin error visible.
        String bodySalida = """
                {"producto":{"id":%d},"sucursal":{"id":%d},"tipoMovimiento":"Salida","cantidad":7,"fechaMovimiento":"2026-07-18T13:00:00.000+00:00"}
                """.formatted(producto.getId(), sucursal.getId());
        mockMvc.perform(post("/api/inventario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodySalida))
                .andExpect(status().isOk());

        long ordenesDespues = ordenDeCompraRepository.count();
        assertEquals(ordenesAntes + 1, ordenesDespues,
                "Debe generarse exactamente una orden de compra nueva al cruzar el stock mínimo");
    }

    @Test
    @Transactional
    @WithUserDetails("cliente")
    void postPedidoConReferenciaMinimaNoFallaYAplicaPrecioReal() throws Exception {
        Categoria categoria = categoriaRepository.save(nuevaCategoria());
        Sucursal sucursal = sucursalRepository.save(nuevaSucursal());

        Producto producto = new Producto();
        producto.setNombre("Producto Pedido API IT");
        producto.setPrecio(2000.0);
        producto.setStock(0);
        producto.setCategoria(categoria);
        producto = productoRepository.save(producto);

        String bodyEntrada = """
                {"producto":{"id":%d},"sucursal":{"id":%d},"tipoMovimiento":"Entrada","cantidad":5,"fechaMovimiento":"2026-07-18T12:00:00.000+00:00"}
                """.formatted(producto.getId(), sucursal.getId());
        mockMvc.perform(post("/api/inventario")
                        .with(user("gerente").roles("GERENTE_SUCURSAL"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyEntrada))
                .andExpect(status().isOk());

        String bodyPedido = """
                {"sucursal":{"id":%d},"modoEntrega":"RETIRO_TIENDA","detalles":[{"producto":{"id":%d},"cantidad":2}]}
                """.formatted(sucursal.getId(), producto.getId());

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyPedido))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"))
                .andExpect(jsonPath("$.detalles[0].precioAplicado").value(2000.0));

        mockMvc.perform(get("/api/sucursales/" + sucursal.getId() + "/productos/" + producto.getId() + "/disponibilidad")
                        .with(user("gerente").roles("GERENTE_SUCURSAL")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadDisponible").value(3));
    }
}
