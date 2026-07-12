package com.minimarket.controller;

import com.minimarket.assembler.DetalleVentaModelAssembler;
import com.minimarket.entity.DetalleVenta;
import com.minimarket.service.DetalleVentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Detalles de venta", description = "Gestión de las líneas de detalle de cada venta (producto, cantidad y precio). " +
        "Las respuestas incluyen enlaces HATEOAS hacia la venta y el producto relacionados.")
@RestController
@RequestMapping("/api/detalle-ventas")
public class DetalleVentaController {

    private static final String EJEMPLO_DETALLE = """
            {
              "venta": { "id": 1 },
              "producto": { "id": 1 },
              "cantidad": 2,
              "precio": 1890
            }""";

    @Autowired
    private DetalleVentaService detalleVentaService;

    @Autowired
    private DetalleVentaModelAssembler detalleVentaModelAssembler;

    @Operation(summary = "Listar detalles de venta",
            description = "Retorna las líneas de detalle registradas. Si se indica `ventaId`, filtra solo los detalles de esa venta.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping
    public CollectionModel<EntityModel<DetalleVenta>> listarDetalleVentas(
            @Parameter(description = "Filtra los detalles de una venta específica", example = "1")
            @RequestParam(required = false) Long ventaId) {
        List<DetalleVenta> detalles = (ventaId != null)
                ? detalleVentaService.findByVentaId(ventaId)
                : detalleVentaService.findAll();
        List<EntityModel<DetalleVenta>> modelos = detalles.stream()
                .map(detalleVentaModelAssembler::toModel)
                .toList();
        Link self = linkTo(methodOn(DetalleVentaController.class).listarDetalleVentas(ventaId)).withSelfRel();
        return CollectionModel.of(modelos, self);
    }

    @Operation(summary = "Obtener un detalle de venta por ID",
            description = "Busca una línea de detalle específica por su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalle encontrado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No existe un detalle con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<DetalleVenta>> obtenerDetalleVentaPorId(
            @Parameter(description = "Identificador único del detalle de venta", example = "1")
            @PathVariable Long id) {
        DetalleVenta detalleVenta = detalleVentaService.findById(id);
        return (detalleVenta != null)
                ? ResponseEntity.ok(detalleVentaModelAssembler.toModel(detalleVenta))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Crear un detalle de venta",
            description = "Agrega una línea de detalle a una venta existente, indicando producto, cantidad y precio aplicado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Detalle creado correctamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Datos inválidos: venta, producto, cantidad o precio faltantes o mal formados", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<DetalleVenta>> guardarDetalleVenta(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Línea de detalle a crear (venta y producto deben existir)", required = true,
                    content = @Content(schema = @Schema(implementation = DetalleVenta.class),
                            examples = @ExampleObject(name = "nuevoDetalle", value = EJEMPLO_DETALLE)))
            @RequestBody DetalleVenta detalleVenta) {
        DetalleVenta guardado = detalleVentaService.save(detalleVenta);
        return ResponseEntity.status(HttpStatus.CREATED).body(detalleVentaModelAssembler.toModel(guardado));
    }

    @Operation(summary = "Actualizar un detalle de venta",
            description = "Modifica producto, cantidad o precio de una línea de detalle existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Detalle actualizado correctamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Datos inválidos: venta, producto, cantidad o precio faltantes o mal formados", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un detalle con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<DetalleVenta>> actualizarDetalleVenta(
            @Parameter(description = "Identificador único del detalle a actualizar", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos de la línea de detalle", required = true,
                    content = @Content(schema = @Schema(implementation = DetalleVenta.class),
                            examples = @ExampleObject(name = "detalleActualizado", value = EJEMPLO_DETALLE)))
            @RequestBody DetalleVenta detalleVenta) {
        DetalleVenta existente = detalleVentaService.findById(id);
        if (existente != null) {
            detalleVenta.setId(id);
            return ResponseEntity.ok(detalleVentaModelAssembler.toModel(detalleVentaService.save(detalleVenta)));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un detalle de venta",
            description = "Elimina la línea de detalle identificada por el ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Detalle eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un detalle con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalleVenta(
            @Parameter(description = "Identificador único del detalle a eliminar", example = "1")
            @PathVariable Long id) {
        DetalleVenta detalleVenta = detalleVentaService.findById(id);
        if (detalleVenta != null) {
            detalleVentaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
