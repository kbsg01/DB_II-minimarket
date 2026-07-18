package com.minimarket.controller;

import com.minimarket.entity.Inventario;
import com.minimarket.service.InventarioService;
import com.minimarket.web.InventarioModelAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Tag(name = "Inventario", description = "Movimientos de inventario por producto y sucursal. Una salida que deja el stock bajo el mínimo genera una orden de compra automática (FR-006).")
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private InventarioModelAssembler inventarioModelAssembler;

    @Operation(summary = "Listar movimientos de inventario, opcionalmente filtrados por producto")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de movimientos de inventario")
    })
    @GetMapping
    public CollectionModel<EntityModel<Inventario>> listarMovimientosDeInventario(
            @Parameter(description = "Filtra los movimientos por id de producto") @RequestParam(required = false) Long productoId) {
        List<Inventario> movimientos = (productoId != null)
                ? inventarioService.findByProductoId(productoId)
                : inventarioService.findAll();
        List<EntityModel<Inventario>> modelos = movimientos.stream()
                .map(inventarioModelAssembler::toModel)
                .toList();
        return CollectionModel.of(modelos,
                linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario(productoId)).withSelfRel());
    }

    @Operation(summary = "Obtener un movimiento de inventario por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Movimiento encontrado"),
        @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Inventario>> obtenerMovimientoPorId(@PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        return (inventario != null)
                ? ResponseEntity.ok(inventarioModelAssembler.toModel(inventario))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Registrar un movimiento de inventario (Entrada/Salida)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Movimiento registrado"),
        @ApiResponse(responseCode = "400", description = "Datos incompletos"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización")
    })
    @PreAuthorize("hasAnyRole('REPONEDOR','GERENTE_SUCURSAL','ADMINISTRADOR')")
    @PostMapping
    public Inventario registrarMovimiento(@RequestBody Inventario inventario) {
        return inventarioService.registrarMovimiento(inventario);
    }

    @Operation(summary = "Actualizar un movimiento de inventario existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Movimiento actualizado"),
        @ApiResponse(responseCode = "400", description = "Datos incompletos"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización"),
        @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    @PreAuthorize("hasAnyRole('REPONEDOR','GERENTE_SUCURSAL','ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Inventario> actualizarMovimiento(@PathVariable Long id, @RequestBody Inventario inventario) {
        Inventario existente = inventarioService.findById(id);
        if (existente != null) {
            return ResponseEntity.ok(inventarioService.actualizarMovimiento(id, inventario));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un movimiento de inventario")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Movimiento eliminado"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización"),
        @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    @PreAuthorize("hasAnyRole('GERENTE_SUCURSAL','ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        if (inventario != null) {
            inventarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
