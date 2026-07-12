package com.minimarket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica que GET /api/ventas/{id} enlace a sus detalles (HATEOAS) y que
 * el filtro ?ventaId= en /api/detalle-ventas siga ese enlace correctamente.
 */
@SpringBootTest
@AutoConfigureMockMvc
class VentaControllerHateoasTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void obtenerVentaPorId_incluyeEnlaceHaciaSusDetalles() throws Exception {
        mockMvc.perform(get("/api/ventas/1").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").value(endsWith("/api/ventas/1")))
                .andExpect(jsonPath("$._links.usuario.href").exists())
                .andExpect(jsonPath("$._links.detalles.href").value(containsString("ventaId=1")));
    }

    @Test
    void listarDetalleVentasFiltradoPorVenta_devuelveSoloEsaVenta() throws Exception {
        mockMvc.perform(get("/api/detalle-ventas").param("ventaId", "1").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").value(containsString("ventaId=1")))
                .andExpect(jsonPath("$._embedded").exists());
    }
}
