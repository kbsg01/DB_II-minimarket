package com.minimarket.controller;

import com.minimarket.assembler.VentaModelAssembler;
import com.minimarket.entity.Venta;
import com.minimarket.service.VentaService;
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

@Tag(name = "Ventas", description = "Registro y consulta de ventas del minimarket. " +
        "Los detalles de cada venta se gestionan en /api/detalle-ventas. " +
        "Las respuestas incluyen enlaces HATEOAS hacia el usuario y los detalles de la venta.")
@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private static final String EJEMPLO_VENTA = """
            {
              "usuario": { "id": 2 },
              "fecha": "2026-07-05T14:30:00.000+00:00"
            }""";

    @Autowired
    private VentaService ventaService;

    @Autowired
    private VentaModelAssembler ventaModelAssembler;

    @Operation(summary = "Listar ventas",
            description = "Retorna las ventas registradas con su usuario, fecha y detalles. Si se indica `usuarioId`, filtra solo las ventas de ese usuario.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping
    public CollectionModel<EntityModel<Venta>> listarVentas(
            @Parameter(description = "Filtra las ventas de un usuario específico", example = "2")
            @RequestParam(required = false) Long usuarioId) {
        List<Venta> ventas = (usuarioId != null)
                ? ventaService.findByUsuarioId(usuarioId)
                : ventaService.findAll();
        List<EntityModel<Venta>> modelos = ventas.stream()
                .map(ventaModelAssembler::toModel)
                .toList();
        Link self = linkTo(methodOn(VentaController.class).listarVentas(usuarioId)).withSelfRel();
        return CollectionModel.of(modelos, self);
    }

    @Operation(summary = "Obtener una venta por ID",
            description = "Busca una venta específica por su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venta encontrada", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No existe una venta con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Venta>> obtenerVentaPorId(
            @Parameter(description = "Identificador único de la venta", example = "1")
            @PathVariable Long id) {
        Venta venta = ventaService.findById(id);
        return (venta != null)
                ? ResponseEntity.ok(ventaModelAssembler.toModel(venta))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Registrar una nueva venta",
            description = "Crea la cabecera de una venta (usuario y fecha). Las líneas se agregan luego en /api/detalle-ventas.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Venta registrada correctamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Datos inválidos: usuario o fecha faltantes o mal formados", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<Venta>> guardarVenta(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la venta a registrar (el usuario debe existir)", required = true,
                    content = @Content(schema = @Schema(implementation = Venta.class),
                            examples = @ExampleObject(name = "nuevaVenta", value = EJEMPLO_VENTA)))
            @RequestBody Venta venta) {
        Venta guardada = ventaService.save(venta);
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaModelAssembler.toModel(guardada));
    }
}
