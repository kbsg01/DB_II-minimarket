package com.minimarket.controller;

import com.minimarket.entity.Inventario;
import com.minimarket.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Inventario", description = "Registro de movimientos de inventario (entradas y salidas) por producto")
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

    @Operation(summary = "Listar todos los movimientos de inventario",
            description = "Retorna el historial completo de entradas y salidas de productos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Inventario.class)))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping
    public List<Inventario> listarMovimientosDeInventario() {
        return inventarioService.findAll();
    }

    @Operation(summary = "Obtener un movimiento por ID",
            description = "Busca un movimiento de inventario específico por su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimiento encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Inventario.class))),
            @ApiResponse(responseCode = "404", description = "No existe un movimiento con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Inventario> obtenerMovimientoPorId(
            @Parameter(description = "Identificador único del movimiento", example = "1")
            @PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        return (inventario != null) ? ResponseEntity.ok(inventario) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Registrar un movimiento de inventario",
            description = "Registra una entrada o salida de unidades para un producto existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Movimiento registrado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Inventario.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Inventario> registrarMovimiento(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Movimiento a registrar (el producto debe existir)", required = true,
                    content = @Content(schema = @Schema(implementation = Inventario.class),
                            examples = @ExampleObject(name = "nuevoMovimiento", value = EJEMPLO_MOVIMIENTO)))
            @RequestBody Inventario inventario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.save(inventario));
    }

    @Operation(summary = "Actualizar un movimiento de inventario",
            description = "Modifica los datos de un movimiento existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimiento actualizado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Inventario.class))),
            @ApiResponse(responseCode = "404", description = "No existe un movimiento con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Inventario> actualizarMovimiento(
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
            return ResponseEntity.ok(inventarioService.save(inventario));
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
