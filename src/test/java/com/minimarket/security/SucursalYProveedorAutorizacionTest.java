package com.minimarket.security;

import com.minimarket.entity.Proveedor;
import com.minimarket.entity.Sucursal;
import com.minimarket.service.ProveedorService;
import com.minimarket.service.SucursalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica el hallazgo de convergencia: SucursalController/ProveedorController no
 * existían (contracts/openapi.yaml y quickstart.md ya los declaraban, pero sin código
 * real). Cubre que la lectura está abierta a cualquier usuario autenticado y que las
 * mutaciones quedan restringidas a roles de gestión, per FR-002/FR-014.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SucursalYProveedorAutorizacionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SucursalService sucursalService;

    @MockitoBean
    private ProveedorService proveedorService;

    private static final String BODY_SUCURSAL = """
            {"nombre":"Sucursal Test","direccion":"Calle 123","region":"Metropolitana"}
            """;

    private static final String BODY_PROVEEDOR = """
            {"nombre":"Proveedor Test","contacto":"contacto@test.cl"}
            """;

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteAutenticadoPuedeListarSucursalesYProveedores() throws Exception {
        when(sucursalService.findAll()).thenReturn(List.of());
        when(proveedorService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/sucursales")).andExpect(status().isOk());
        mockMvc.perform(get("/api/proveedores")).andExpect(status().isOk());
    }

    @Test
    void sinAutenticacionRechazaConNoAutorizado() throws Exception {
        mockMvc.perform(get("/api/sucursales")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/proveedores")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "GERENTE_SUCURSAL")
    void gerenteSucursalPuedeCrearSucursalesYProveedores() throws Exception {
        Sucursal sucursalCreada = new Sucursal();
        sucursalCreada.setId(1L);
        when(sucursalService.save(any(Sucursal.class))).thenReturn(sucursalCreada);

        Proveedor proveedorCreado = new Proveedor();
        proveedorCreado.setId(1L);
        when(proveedorService.save(any(Proveedor.class))).thenReturn(proveedorCreado);

        mockMvc.perform(post("/api/sucursales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_SUCURSAL))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/proveedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_PROVEEDOR))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeCrearSucursalesNiProveedores() throws Exception {
        mockMvc.perform(post("/api/sucursales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_SUCURSAL))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/proveedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_PROVEEDOR))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CAJERO")
    void cajeroNoPuedeCrearSucursalesNiProveedores() throws Exception {
        mockMvc.perform(post("/api/sucursales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_SUCURSAL))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/proveedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY_PROVEEDOR))
                .andExpect(status().isForbidden());
    }
}
