package com.minimarket.controller;

import com.minimarket.entity.OrdenDeCompra;
import com.minimarket.service.OrdenDeCompraService;
import com.minimarket.web.OrdenDeCompraModelAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Órdenes de Compra", description = "Órdenes generadas automáticamente cuando el stock de un producto cae bajo su nivel mínimo (FR-006). Reservado a roles de gestión.")
@RestController
@RequestMapping("/api/ordenes-compra")
@PreAuthorize("hasAnyRole('GERENTE_SUCURSAL','ADMINISTRADOR')")
public class OrdenDeCompraController {

    @Autowired
    private OrdenDeCompraService ordenDeCompraService;

    @Autowired
    private OrdenDeCompraModelAssembler ordenDeCompraModelAssembler;

    @Operation(summary = "Listar órdenes de compra generadas")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de órdenes de compra"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización")
    })
    @GetMapping
    public CollectionModel<EntityModel<OrdenDeCompra>> listarOrdenesDeCompra() {
        var modelos = ordenDeCompraService.findAll().stream()
                .map(ordenDeCompraModelAssembler::toModel)
                .toList();
        return CollectionModel.of(modelos,
                linkTo(methodOn(OrdenDeCompraController.class).listarOrdenesDeCompra()).withSelfRel());
    }

    @Operation(summary = "Obtener una orden de compra por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Orden encontrada"),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<OrdenDeCompra>> obtenerOrdenPorId(@PathVariable Long id) {
        OrdenDeCompra orden = ordenDeCompraService.findById(id);
        return (orden != null)
                ? ResponseEntity.ok(ordenDeCompraModelAssembler.toModel(orden))
                : ResponseEntity.notFound().build();
    }
}
