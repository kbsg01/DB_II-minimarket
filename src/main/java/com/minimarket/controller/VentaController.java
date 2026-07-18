package com.minimarket.controller;

import com.minimarket.entity.Venta;
import com.minimarket.security.model.CustomUserDetails;
import com.minimarket.service.VentaService;
import com.minimarket.web.VentaModelAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Ventas", description = "Ventas registradas en tienda. Registrar ventas está reservado a CAJERO y roles de gestión (FR-002); un usuario sin rol de gestión solo puede ver sus propias ventas.")
@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private VentaModelAssembler ventaModelAssembler;

    @Operation(summary = "Listar ventas, opcionalmente filtradas por usuario",
               description = "Un usuario sin rol de gestión solo puede ver sus propias ventas, sin importar el usuarioId solicitado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de ventas con enlaces a su detalle")
    })
    @GetMapping
    public CollectionModel<EntityModel<Venta>> listarVentas(
            @Parameter(description = "Filtra las ventas por id de usuario (ignorado si el solicitante no es de gestión)") @RequestParam(required = false) Long usuarioId) {
        Long usuarioIdEfectivo = resolverUsuarioIdEfectivo(usuarioId);
        List<Venta> ventas = (usuarioIdEfectivo != null)
                ? ventaService.findByUsuarioId(usuarioIdEfectivo)
                : ventaService.findAll();
        List<EntityModel<Venta>> modelos = ventas.stream()
                .map(ventaModelAssembler::toModel)
                .toList();
        return CollectionModel.of(modelos,
                linkTo(methodOn(VentaController.class).listarVentas(usuarioId)).withSelfRel());
    }

    @Operation(summary = "Obtener una venta por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Venta encontrada"),
        @ApiResponse(responseCode = "403", description = "No es el dueño de la venta ni tiene rol de gestión"),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Venta>> obtenerVentaPorId(@PathVariable Long id) {
        Venta venta = ventaService.findById(id);
        if (venta == null) {
            return ResponseEntity.notFound().build();
        }
        verificarPropietarioODeGestion(venta);
        return ResponseEntity.ok(ventaModelAssembler.toModel(venta));
    }

    @Operation(summary = "Registrar una venta en tienda")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Venta registrada"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización")
    })
    @PreAuthorize("hasAnyRole('CAJERO','GERENTE_SUCURSAL','ADMINISTRADOR')")
    @PostMapping
    public Venta guardarVenta(@RequestBody Venta venta) {
        return ventaService.registrarVenta(venta);
    }

    /**
     * Solo el dueño de la venta o un rol de gestión puede consultarla (Constitution
     * Principio II / FR-004, hallazgo T056 de /speckit-converge, segunda pasada).
     */
    private void verificarPropietarioODeGestion(Venta venta) {
        if (esGestion()) {
            return;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof CustomUserDetails cud
                && venta.getUsuario() != null
                && cud.getUsuarioId().equals(venta.getUsuario().getId())) {
            return;
        }
        throw new AccessDeniedException("No autorizado para acceder a esta venta");
    }

    private Long resolverUsuarioIdEfectivo(Long usuarioIdSolicitado) {
        if (esGestion()) {
            return usuarioIdSolicitado;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof CustomUserDetails cud) {
            return cud.getUsuarioId();
        }
        throw new AccessDeniedException("No autorizado");
    }

    private boolean esGestion() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(rol -> rol.equals("ROLE_GERENTE_SUCURSAL") || rol.equals("ROLE_ADMINISTRADOR"));
    }
}
