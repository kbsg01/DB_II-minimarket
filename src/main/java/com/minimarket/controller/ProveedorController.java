package com.minimarket.controller;

import com.minimarket.entity.Proveedor;
import com.minimarket.service.ProveedorService;
import com.minimarket.web.ProveedorModelAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * CRUD de Proveedor (FR-006: la orden de compra automática se dirige al proveedor
 * asociado a un producto). Hallazgo de convergencia: `contracts/openapi.yaml` ya
 * declaraba el tag "proveedores", pero no existía ningún controlador real — sin este
 * endpoint no había forma de crear un Proveedor a través de la API.
 */
@Tag(name = "Proveedores", description = "Proveedores externos a los que se dirige la reposición automática de stock. Mutaciones reservadas a roles de gestión.")
@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private ProveedorModelAssembler proveedorModelAssembler;

    @Operation(summary = "Listar todos los proveedores")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de proveedores")
    })
    @GetMapping
    public CollectionModel<EntityModel<Proveedor>> listarProveedores() {
        List<EntityModel<Proveedor>> modelos = proveedorService.findAll().stream()
                .map(proveedorModelAssembler::toModel)
                .toList();
        return CollectionModel.of(modelos,
                linkTo(methodOn(ProveedorController.class).listarProveedores()).withSelfRel());
    }

    @Operation(summary = "Obtener un proveedor por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proveedor encontrado"),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Proveedor>> obtenerProveedorPorId(@PathVariable Long id) {
        Proveedor proveedor = proveedorService.findById(id);
        return (proveedor != null)
                ? ResponseEntity.ok(proveedorModelAssembler.toModel(proveedor))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Crear un proveedor")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proveedor creado"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización")
    })
    @PreAuthorize("hasAnyRole('GERENTE_SUCURSAL','ADMINISTRADOR')")
    @PostMapping
    public Proveedor guardarProveedor(@RequestBody Proveedor proveedor) {
        return proveedorService.save(proveedor);
    }

    @Operation(summary = "Actualizar un proveedor existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Proveedor actualizado"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización"),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    })
    @PreAuthorize("hasAnyRole('GERENTE_SUCURSAL','ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Proveedor> actualizarProveedor(@PathVariable Long id, @RequestBody Proveedor proveedor) {
        Proveedor existente = proveedorService.findById(id);
        if (existente != null) {
            proveedor.setId(id);
            return ResponseEntity.ok(proveedorService.save(proveedor));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un proveedor")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Proveedor eliminado"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización"),
        @ApiResponse(responseCode = "404", description = "Proveedor no encontrado")
    })
    @PreAuthorize("hasAnyRole('GERENTE_SUCURSAL','ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable Long id) {
        Proveedor proveedor = proveedorService.findById(id);
        if (proveedor != null) {
            proveedorService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
