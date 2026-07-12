package com.minimarket.controller;

import com.minimarket.assembler.InventarioModelAssembler;
import com.minimarket.entity.Inventario;
import com.minimarket.service.InventarioService;
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

@Tag(name = "Inventario", description = "Registro de movimientos de inventario (entradas y salidas) por producto. " +
        "Las respuestas incluyen un enlace HATEOAS hacia el producto afectado.")
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private static final String EJEMPLO_MOVIMIENTO = """
            {
              "producto": { "id": 1 },
              "cantidad": 10,
              "tipoMovimiento": "Entrada",
              "fechaMovimiento": "2026-07-05T10:00:00.000+00:00"
            }""";

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private InventarioModelAssembler inventarioModelAssembler;

    @Operation(summary = "Listar movimientos de inventario",
            description = "Retorna el historial de entradas y salidas. Si se indica `productoId`, filtra solo los movimientos de ese producto.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping
    public CollectionModel<EntityModel<Inventario>> listarMovimientosDeInventario(
            @Parameter(description = "Filtra los movimientos de un producto específico", example = "1")
            @RequestParam(required = false) Long productoId) {
        List<Inventario> movimientos = (productoId != null)
                ? inventarioService.findByProductoId(productoId)
                : inventarioService.findAll();
        List<EntityModel<Inventario>> modelos = movimientos.stream()
                .map(inventarioModelAssembler::toModel)
                .toList();
        Link self = linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario(productoId)).withSelfRel();
        return CollectionModel.of(modelos, self);
    }

    @Operation(summary = "Obtener un movimiento por ID",
            description = "Busca un movimiento de inventario específico por su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimiento encontrado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No existe un movimiento con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Inventario>> obtenerMovimientoPorId(
            @Parameter(description = "Identificador único del movimiento", example = "1")
            @PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        return (inventario != null)
                ? ResponseEntity.ok(inventarioModelAssembler.toModel(inventario))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Registrar un movimiento de inventario",
            description = "Registra una entrada o salida de unidades para un producto existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Movimiento registrado correctamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Datos inválidos: producto, cantidad o tipo de movimiento faltantes o mal formados", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<Inventario>> registrarMovimiento(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Movimiento a registrar (el producto debe existir)", required = true,
                    content = @Content(schema = @Schema(implementation = Inventario.class),
                            examples = @ExampleObject(name = "nuevoMovimiento", value = EJEMPLO_MOVIMIENTO)))
            @RequestBody Inventario inventario) {
        Inventario guardado = inventarioService.save(inventario);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioModelAssembler.toModel(guardado));
    }

    @Operation(summary = "Actualizar un movimiento de inventario",
            description = "Modifica los datos de un movimiento existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimiento actualizado correctamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Datos inválidos: producto, cantidad o tipo de movimiento faltantes o mal formados", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un movimiento con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Inventario>> actualizarMovimiento(
            @Parameter(description = "Identificador único del movimiento a actualizar", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos del movimiento", required = true,
                    content = @Content(schema = @Schema(implementation = Inventario.class),
                            examples = @ExampleObject(name = "movimientoActualizado", value = EJEMPLO_MOVIMIENTO)))
            @RequestBody Inventario inventario) {
        Inventario existente = inventarioService.findById(id);
        if (existente != null) {
            inventario.setId(id);
            return ResponseEntity.ok(inventarioModelAssembler.toModel(inventarioService.save(inventario)));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un movimiento de inventario",
            description = "Elimina el movimiento identificado por el ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Movimiento eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un movimiento con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(
            @Parameter(description = "Identificador único del movimiento a eliminar", example = "1")
            @PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        if (inventario != null) {
            inventarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
