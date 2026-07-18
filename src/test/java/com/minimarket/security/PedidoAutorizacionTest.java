package com.minimarket.security;

import com.minimarket.entity.Pedido;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.PedidoService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Verifica T060 (specs/001-minimarket-backend-spec, Fase 10): POST /api/pedidos ignora
 * cualquier usuario.id recibido en el body y confirma siempre el pedido a nombre del
 * usuario autenticado. Antes de esta corrección, un usuario podía suplantar a otro
 * indicando su id en el body, generando un pedido (y su Venta asociada) a su nombre.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PedidoAutorizacionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private PedidoService pedidoService;

    @Test
    @WithUserDetails("cliente")
    void crearPedidoIgnoraUsuarioSuplantadoYUsaElAutenticado() throws Exception {
        Usuario cliente = usuarioRepository.findByUsername("cliente").orElseThrow();
        Usuario admin = usuarioRepository.findByUsername("admin").orElseThrow();

        Pedido confirmado = new Pedido();
        confirmado.setId(1L);
        when(pedidoService.confirmarPedido(any(Pedido.class))).thenReturn(confirmado);

        String body = """
                {"usuario":{"id":%d},"sucursal":{"id":1},"modoEntrega":"RETIRO_TIENDA",
                 "detalles":[{"producto":{"id":1},"cantidad":1}]}
                """.formatted(admin.getId());

        mockMvc.perform(post("/api/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());

        ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);
        verify(pedidoService).confirmarPedido(captor.capture());
        assertEquals(cliente.getId(), captor.getValue().getUsuario().getId());
        assertNotEquals(admin.getId(), captor.getValue().getUsuario().getId());
    }
}
