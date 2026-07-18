package com.minimarket.security;

import com.minimarket.entity.Promocion;
import com.minimarket.exception.DatosIncompletosException;
import com.minimarket.service.PromocionService;
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
 * Verifica T050/FR-011 (specs/001-minimarket-backend-spec): PromocionController
 * restringido a roles de gestión (GERENTE_SUCURSAL/ADMINISTRADOR), y T052: el rango de
 * fechas inválido se traduce a 400 vía GlobalExceptionHandler.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PromocionAutorizacionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PromocionService promocionService;

    private static final String BODY = """
            {"producto":{"id":1},"descuentoPorcentaje":15.0,"fechaInicio":"2026-07-01T00:00:00.000+00:00","fechaFin":"2026-07-10T00:00:00.000+00:00"}
            """;

    @Test
    @WithMockUser(roles = "GERENTE_SUCURSAL")
    void gerenteSucursalPuedeListarYCrearPromociones() throws Exception {
        when(promocionService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/promociones")).andExpect(status().isOk());

        Promocion creada = new Promocion();
        creada.setId(1L);
        when(promocionService.save(any(Promocion.class))).thenReturn(creada);

        mockMvc.perform(post("/api/promociones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void administradorPuedeListarYCrearPromociones() throws Exception {
        when(promocionService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/promociones")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeVerPromociones() throws Exception {
        mockMvc.perform(get("/api/promociones")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CAJERO")
    void cajeroNoPuedeCrearPromociones() throws Exception {
        mockMvc.perform(post("/api/promociones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    void sinAutenticacionRechazaConNoAutorizado() throws Exception {
        mockMvc.perform(get("/api/promociones")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void rangoDeFechasInvalidoRetorna400() throws Exception {
        when(promocionService.save(any(Promocion.class)))
                .thenThrow(new DatosIncompletosException("fechaFin debe ser posterior a fechaInicio"));

        mockMvc.perform(post("/api/promociones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isBadRequest());
    }
}
