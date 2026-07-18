package com.minimarket.controller;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.service.DetalleVentaService;
import com.minimarket.web.DetalleVentaModelAssembler;
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

@Tag(name = "Detalle de Ventas", description = "Líneas de detalle de una venta. Mutaciones reservadas a CAJERO y roles de gestión; eliminar solo a roles de gestión.")
@RestController
@RequestMapping("/api/detalle-ventas")
public class DetalleVentaController {

    @Autowired
    private DetalleVentaService detalleVentaService;

    @Autowired
    private DetalleVentaModelAssembler detalleVentaModelAssembler;

    @Operation(summary = "Listar detalles de venta, opcionalmente filtrados por venta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de detalles de venta"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización")
    })
    @PreAuthorize("hasAnyRole('CAJERO','GERENTE_SUCURSAL','ADMINISTRADOR')")
    @GetMapping
    public CollectionModel<EntityModel<DetalleVenta>> listarDetalleVentas(
            @Parameter(description = "Filtra los detalles por id de venta") @RequestParam(required = false) Long ventaId) {
        List<DetalleVenta> detalles = (ventaId != null)
                ? detalleVentaService.findByVentaId(ventaId)
                : detalleVentaService.findAll();
        List<EntityModel<DetalleVenta>> modelos = detalles.stream()
                .map(detalleVentaModelAssembler::toModel)
                .toList();
        return CollectionModel.of(modelos,
                linkTo(methodOn(DetalleVentaController.class).listarDetalleVentas(ventaId)).withSelfRel());
    }

    @Operation(summary = "Obtener un detalle de venta por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Detalle encontrado"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización"),
        @ApiResponse(responseCode = "404", description = "Detalle no encontrado")
    })
    @PreAuthorize("hasAnyRole('CAJERO','GERENTE_SUCURSAL','ADMINISTRADOR')")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<DetalleVenta>> obtenerDetalleVentaPorId(@PathVariable Long id) {
        DetalleVenta detalleVenta = detalleVentaService.findById(id);
        return (detalleVenta != null)
                ? ResponseEntity.ok(detalleVentaModelAssembler.toModel(detalleVenta))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Crear un detalle de venta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Detalle creado"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización")
    })
    @PreAuthorize("hasAnyRole('CAJERO','GERENTE_SUCURSAL','ADMINISTRADOR')")
    @PostMapping
    public DetalleVenta guardarDetalleVenta(@RequestBody DetalleVenta detalleVenta) {
        return detalleVentaService.save(detalleVenta);
    }

    @Operation(summary = "Actualizar un detalle de venta existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Detalle actualizado"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización"),
        @ApiResponse(responseCode = "404", description = "Detalle no encontrado")
    })
    @PreAuthorize("hasAnyRole('CAJERO','GERENTE_SUCURSAL','ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<DetalleVenta> actualizarDetalleVenta(@PathVariable Long id, @RequestBody DetalleVenta detalleVenta) {
        DetalleVenta existente = detalleVentaService.findById(id);
        if (existente != null) {
            detalleVenta.setId(id);
            return ResponseEntity.ok(detalleVentaService.save(detalleVenta));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un detalle de venta")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Detalle eliminado"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización"),
        @ApiResponse(responseCode = "404", description = "Detalle no encontrado")
    })
    @PreAuthorize("hasAnyRole('GERENTE_SUCURSAL','ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalleVenta(@PathVariable Long id) {
        DetalleVenta detalleVenta = detalleVentaService.findById(id);
        if (detalleVenta != null) {
            detalleVentaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
