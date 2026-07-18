package com.minimarket.controller;

import com.minimarket.entity.Carrito;
import com.minimarket.security.model.CustomUserDetails;
import com.minimarket.service.CarritoService;
import com.minimarket.web.CarritoModelAssembler;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Carrito", description = "Carrito de compras. Modificar o eliminar un ítem está reservado al usuario dueño del carrito o a roles de gestión.")
@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private CarritoModelAssembler carritoModelAssembler;

    @Operation(summary = "Listar ítems del carrito, opcionalmente filtrados por usuario",
               description = "Un usuario sin rol de gestión solo puede ver sus propios ítems, sin importar el usuarioId solicitado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de ítems del carrito")
    })
    @GetMapping
    public CollectionModel<EntityModel<Carrito>> listarCarrito(
            @Parameter(description = "Filtra los ítems por id de usuario (ignorado si el solicitante no es de gestión)") @RequestParam(required = false) Long usuarioId) {
        Long usuarioIdEfectivo = resolverUsuarioIdEfectivo(usuarioId);
        List<Carrito> carritos = (usuarioIdEfectivo != null)
                ? carritoService.findByUsuarioId(usuarioIdEfectivo)
                : carritoService.findAll();
        List<EntityModel<Carrito>> modelos = carritos.stream()
                .map(carritoModelAssembler::toModel)
                .toList();
        return CollectionModel.of(modelos,
                linkTo(methodOn(CarritoController.class).listarCarrito(usuarioId)).withSelfRel());
    }

    @Operation(summary = "Obtener un ítem del carrito por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ítem encontrado"),
        @ApiResponse(responseCode = "403", description = "No es el dueño del carrito ni tiene rol de gestión"),
        @ApiResponse(responseCode = "404", description = "Ítem no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Carrito>> obtenerCarritoPorId(@PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }
        verificarPropietarioODeGestion(carrito);
        return ResponseEntity.ok(carritoModelAssembler.toModel(carrito));
    }

    @Operation(summary = "Agregar un producto al carrito")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto agregado"),
        @ApiResponse(responseCode = "403", description = "No se puede agregar al carrito de otro usuario")
    })
    @PostMapping
    public Carrito agregarProductoAlCarrito(@RequestBody Carrito carrito) {
        verificarPropietarioODeGestion(carrito);
        return carritoService.save(carrito);
    }

    @Operation(summary = "Actualizar un ítem del carrito")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ítem actualizado"),
        @ApiResponse(responseCode = "403", description = "No es el dueño del carrito ni tiene rol de gestión"),
        @ApiResponse(responseCode = "404", description = "Ítem no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Carrito> actualizarCarrito(@PathVariable Long id, @RequestBody Carrito carrito) {
        Carrito existente = carritoService.findById(id);
        if (existente != null) {
            verificarPropietarioODeGestion(existente);
            carrito.setId(id);
            return ResponseEntity.ok(carritoService.save(carrito));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un ítem del carrito")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Ítem eliminado"),
        @ApiResponse(responseCode = "403", description = "No es el dueño del carrito ni tiene rol de gestión"),
        @ApiResponse(responseCode = "404", description = "Ítem no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProductoDelCarrito(@PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito != null) {
            verificarPropietarioODeGestion(carrito);
            carritoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Solo el dueño del carrito o un rol de gestión puede modificarlo/eliminarlo
     * (Constitution Principio II, hallazgo T049 de /speckit-converge). No se puede
     * expresar como @PreAuthorize simple porque requiere el carrito ya cargado.
     */
    private void verificarPropietarioODeGestion(Carrito carrito) {
        if (esGestion()) {
            return;
        }
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof CustomUserDetails cud
                && carrito.getUsuario() != null
                && cud.getUsuarioId().equals(carrito.getUsuario().getId())) {
            return;
        }
        throw new AccessDeniedException("No autorizado para acceder a este carrito");
    }

    /**
     * Un solicitante sin rol de gestión solo puede listar sus propios ítems: se ignora
     * cualquier usuarioId solicitado y se fuerza al propio (hallazgo T056 de
     * /speckit-converge, segunda pasada).
     */
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
