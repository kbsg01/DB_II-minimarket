package com.minimarket.controller;

import com.minimarket.entity.Pedido;
import com.minimarket.entity.Usuario;
import com.minimarket.security.model.CustomUserDetails;
import com.minimarket.service.PedidoService;
import com.minimarket.web.PedidoModelAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Pedidos", description = "Disponibilidad por sucursal y pedidos en línea (retiro o despacho), con promociones aplicadas (FR-008 a FR-011).")
@RestController
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PedidoModelAssembler pedidoModelAssembler;

    @Operation(summary = "Consultar disponibilidad de un producto en una sucursal")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cantidad disponible en la sucursal indicada")
    })
    @GetMapping("/api/sucursales/{sucursalId}/productos/{productoId}/disponibilidad")
    public Map<String, Object> consultarDisponibilidad(@PathVariable Long sucursalId,
                                                         @PathVariable Long productoId) {
        int disponible = pedidoService.consultarDisponibilidad(productoId, sucursalId);
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("productoId", productoId);
        respuesta.put("sucursalId", sucursalId);
        respuesta.put("cantidadDisponible", disponible);
        return respuesta;
    }

    @Operation(summary = "Generar un pedido en línea",
               description = "Revalida disponibilidad al confirmar, aplica el precio promocional vigente y descuenta stock. " +
                       "El pedido siempre se confirma a nombre del usuario autenticado: cualquier usuario.id recibido en el body se ignora.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido confirmado, stock descontado"),
        @ApiResponse(responseCode = "409", description = "Stock insuficiente al momento de confirmar")
    })
    @PostMapping("/api/pedidos")
    public Pedido crearPedido(@RequestBody Pedido pedido) {
        pedido.setUsuario(usuarioAutenticado());
        return pedidoService.confirmarPedido(pedido);
    }

    @Operation(summary = "Consultar el estado de un pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Detalle del pedido"),
        @ApiResponse(responseCode = "403", description = "No es el dueño del pedido ni tiene rol de gestión"),
        @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    @GetMapping("/api/pedidos/{id}")
    public ResponseEntity<EntityModel<Pedido>> obtenerPedidoPorId(@PathVariable Long id) {
        Pedido pedido = pedidoService.findById(id);
        if (pedido == null) {
            return ResponseEntity.notFound().build();
        }
        verificarPropietarioODeGestion(pedido);
        return ResponseEntity.ok(pedidoModelAssembler.toModel(pedido));
    }

    /**
     * El pedido siempre se confirma a nombre de quien está autenticado: se ignora
     * cualquier usuario.id recibido en el body (Constitution Principio II / FR-009 /
     * FR-010, hallazgo T060 de /speckit-converge, tercera pasada). Sin este método,
     * cualquier usuario autenticado podía generar un pedido —y su Venta asociada— a
     * nombre de otra persona con solo indicar un usuario.id ajeno en la solicitud.
     */
    private Usuario usuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof CustomUserDetails cud) {
            Usuario usuario = new Usuario();
            usuario.setId(cud.getUsuarioId());
            return usuario;
        }
        throw new AccessDeniedException("No autorizado");
    }

    /**
     * Solo el dueño del pedido o un rol de gestión puede consultarlo (Constitution
     * Principio II / FR-004, hallazgo T056 de /speckit-converge, segunda pasada).
     */
    private void verificarPropietarioODeGestion(Pedido pedido) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean esGestion = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(rol -> rol.equals("ROLE_GERENTE_SUCURSAL") || rol.equals("ROLE_ADMINISTRADOR"));
        if (esGestion) {
            return;
        }
        if (auth.getPrincipal() instanceof CustomUserDetails cud
                && pedido.getUsuario() != null
                && cud.getUsuarioId().equals(pedido.getUsuario().getId())) {
            return;
        }
        throw new AccessDeniedException("No autorizado para acceder a este pedido");
    }
}
