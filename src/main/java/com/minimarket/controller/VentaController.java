package com.minimarket.controller;

import com.minimarket.entity.Venta;
import com.minimarket.service.VentaService;
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

@Tag(name = "Ventas", description = "Registro y consulta de ventas del minimarket. " +
        "Los detalles de cada venta se gestionan en /api/detalle-ventas.")
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

    @Operation(summary = "Listar todas las ventas",
            description = "Retorna todas las ventas registradas con su usuario, fecha y detalles.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Venta.class)))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping
    public List<Venta> listarVentas() {
        return ventaService.findAll();
    }

    @Operation(summary = "Obtener una venta por ID",
            description = "Busca una venta específica por su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Venta encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Venta.class))),
            @ApiResponse(responseCode = "404", description = "No existe una venta con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerVentaPorId(
            @Parameter(description = "Identificador único de la venta", example = "1")
            @PathVariable Long id) {
        Venta venta = ventaService.findById(id);
        return (venta != null) ? ResponseEntity.ok(venta) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Registrar una nueva venta",
            description = "Crea la cabecera de una venta (usuario y fecha). Las líneas se agregan luego en /api/detalle-ventas.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Venta registrada correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Venta.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Venta> guardarVenta(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la venta a registrar (el usuario debe existir)", required = true,
                    content = @Content(schema = @Schema(implementation = Venta.class),
                            examples = @ExampleObject(name = "nuevaVenta", value = EJEMPLO_VENTA)))
            @RequestBody Venta venta) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.save(venta));
    }
}
