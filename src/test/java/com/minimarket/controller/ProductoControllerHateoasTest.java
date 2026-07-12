package com.minimarket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica que GET /api/productos/{id} incluya los enlaces HATEOAS
 * esperados (self, colección, categoría e inventario) según el
 * contrato definido en specs/001-hateoas-openapi-avanzado/contracts/hateoas-links.md.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductoControllerHateoasTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void obtenerProductoPorId_incluyeEnlacesHateoas() throws Exception {
        mockMvc.perform(get("/api/productos/1").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").value(org.hamcrest.Matchers.endsWith("/api/productos/1")))
                .andExpect(jsonPath("$._links.productos.href").value(org.hamcrest.Matchers.containsString("/api/productos")))
                .andExpect(jsonPath("$._links.categoria.href").exists())
                .andExpect(jsonPath("$._links.inventario.href").value(org.hamcrest.Matchers.containsString("productoId=1")));
    }

    @Test
    void listarProductosFiltradoPorCategoria_devuelveSoloEsaCategoria() throws Exception {
        mockMvc.perform(get("/api/productos").param("categoriaId", "1").with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href").value(org.hamcrest.Matchers.containsString("categoriaId=1")))
                .andExpect(jsonPath("$._embedded").exists());
    }
}
