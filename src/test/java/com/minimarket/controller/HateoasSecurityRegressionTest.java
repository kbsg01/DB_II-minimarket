package com.minimarket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Confirma que envolver las respuestas en EntityModel/CollectionModel
 * (HATEOAS) no debilita la autenticación HTTP Basic ya existente
 * (FR-007 / SC-005 de specs/001-hateoas-openapi-avanzado/spec.md).
 */
@SpringBootTest
@AutoConfigureMockMvc
class HateoasSecurityRegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void obtenerProductoSinCredenciales_devuelve401() throws Exception {
        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void obtenerProductoConCredencialesValidas_devuelve200() throws Exception {
        mockMvc.perform(get("/api/productos/1").with(httpBasic("cliente1", "cliente123")))
                .andExpect(status().isOk());
    }
}
