package com.minimarket.controller;

import com.minimarket.entity.Sucursal;
import com.minimarket.service.SucursalService;
import com.minimarket.web.SucursalModelAssembler;
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
 * CRUD de Sucursal (FR-005: el stock, las órdenes de compra y los pedidos se dimensionan
 * por sucursal). Hallazgo de convergencia: `contracts/openapi.yaml` y `quickstart.md` ya
 * declaraban el tag/prerequisito "sucursales", pero no existía ningún controlador real —
 * sin este endpoint no había forma de crear una Sucursal a través de la API.
 */
@Tag(name = "Sucursales", description = "Sucursales de la cadena MiniMarket Plus. Mutaciones reservadas a roles de gestión.")
@RestController
@RequestMapping("/api/sucursales")
public class SucursalController {

    @Autowired
    private SucursalService sucursalService;

    @Autowired
    private SucursalModelAssembler sucursalModelAssembler;

    @Operation(summary = "Listar todas las sucursales")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de sucursales")
    })
    @GetMapping
    public CollectionModel<EntityModel<Sucursal>> listarSucursales() {
        List<EntityModel<Sucursal>> modelos = sucursalService.findAll().stream()
                .map(sucursalModelAssembler::toModel)
                .toList();
        return CollectionModel.of(modelos,
                linkTo(methodOn(SucursalController.class).listarSucursales()).withSelfRel());
    }

    @Operation(summary = "Obtener una sucursal por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sucursal encontrada"),
        @ApiResponse(responseCode = "404", description = "Sucursal no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Sucursal>> obtenerSucursalPorId(@PathVariable Long id) {
        Sucursal sucursal = sucursalService.findById(id);
        return (sucursal != null)
                ? ResponseEntity.ok(sucursalModelAssembler.toModel(sucursal))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Crear una sucursal")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sucursal creada"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización")
    })
    @PreAuthorize("hasAnyRole('GERENTE_SUCURSAL','ADMINISTRADOR')")
    @PostMapping
    public Sucursal guardarSucursal(@RequestBody Sucursal sucursal) {
        return sucursalService.save(sucursal);
    }

    @Operation(summary = "Actualizar una sucursal existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sucursal actualizada"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización"),
        @ApiResponse(responseCode = "404", description = "Sucursal no encontrada")
    })
    @PreAuthorize("hasAnyRole('GERENTE_SUCURSAL','ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Sucursal> actualizarSucursal(@PathVariable Long id, @RequestBody Sucursal sucursal) {
        Sucursal existente = sucursalService.findById(id);
        if (existente != null) {
            sucursal.setId(id);
            return ResponseEntity.ok(sucursalService.save(sucursal));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una sucursal")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Sucursal eliminada"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización"),
        @ApiResponse(responseCode = "404", description = "Sucursal no encontrada")
    })
    @PreAuthorize("hasAnyRole('GERENTE_SUCURSAL','ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarSucursal(@PathVariable Long id) {
        Sucursal sucursal = sucursalService.findById(id);
        if (sucursal != null) {
            sucursalService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
