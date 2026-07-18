package com.minimarket.security;

import com.minimarket.entity.Categoria;
import com.minimarket.entity.Producto;
import com.minimarket.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica FR-002/FR-014 (specs/001-minimarket-backend-spec): solo GERENTE_SUCURSAL y
 * ADMINISTRADOR pueden actualizar el precio de un producto; el resto de los roles del
 * negocio recibe 403. Sin token de autenticación, 401 (ya cubierto por
 * JwtAuthenticationEntryPoint, consolidado en specs/002).
 */
@SpringBootTest
@AutoConfigureMockMvc
class AutorizacionRolesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoService productoService;

    private static final String BODY = """
            {"nombre":"Leche","precio":1200.0,"stock":10}
            """;

    private Producto productoExistente() {
        Producto p = new Producto();
        p.setId(1L);
        p.setNombre("Leche");
        p.setPrecio(1000.0);
        p.setStock(10);
        p.setCategoria(new Categoria());
        return p;
    }

    @Test
    @WithMockUser(roles = "GERENTE_SUCURSAL")
    void gerenteSucursalPuedeActualizarPrecio() throws Exception {
        when(productoService.findById(anyLong())).thenReturn(productoExistente());
        when(productoService.save(any(Producto.class))).thenReturn(productoExistente());

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void administradorPuedeActualizarPrecio() throws Exception {
        when(productoService.findById(anyLong())).thenReturn(productoExistente());
        when(productoService.save(any(Producto.class))).thenReturn(productoExistente());

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeActualizarPrecio() throws Exception {
        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CAJERO")
    void cajeroNoPuedeActualizarPrecio() throws Exception {
        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "REPONEDOR")
    void reponedorNoPuedeActualizarPrecio() throws Exception {
        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    void sinAutenticacionRechazaConNoAutorizado() throws Exception {
        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isUnauthorized());
    }
}
