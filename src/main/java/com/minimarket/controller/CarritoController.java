package com.minimarket.controller;

import com.minimarket.entity.Carrito;
import com.minimarket.service.CarritoService;
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

/**
 * Ruta estandarizada a plural (/api/carritos) para mantener consistencia
 * con el resto de los recursos, según las buenas prácticas de la guía S7.
 */
@Tag(name = "Carritos", description = "Gestión de los ítems del carrito de compras de los usuarios")
@RestController
@RequestMapping("/api/carritos")
public class CarritoController {

    private static final String EJEMPLO_CARRITO = """
            {
              "usuario": { "id": 3 },
              "producto": { "id": 1 },
              "cantidad": 3
            }""";

    @Autowired
    private CarritoService carritoService;

    @Operation(summary = "Listar todos los ítems de carrito",
            description = "Retorna todos los ítems de carrito registrados, con su usuario, producto y cantidad.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Carrito.class)))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping
    public List<Carrito> listarCarrito() {
        return carritoService.findAll();
    }

    @Operation(summary = "Obtener un ítem de carrito por ID",
            description = "Busca un ítem de carrito específico por su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ítem encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Carrito.class))),
            @ApiResponse(responseCode = "404", description = "No existe un ítem con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Carrito> obtenerCarritoPorId(
            @Parameter(description = "Identificador único del ítem de carrito", example = "1")
            @PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        return (carrito != null) ? ResponseEntity.ok(carrito) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Agregar un producto al carrito",
            description = "Crea un ítem de carrito asociando un usuario, un producto existente y la cantidad deseada.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto agregado al carrito correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Carrito.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Carrito> agregarProductoAlCarrito(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Ítem de carrito a crear (usuario y producto deben existir)", required = true,
                    content = @Content(schema = @Schema(implementation = Carrito.class),
                            examples = @ExampleObject(name = "nuevoItemCarrito", value = EJEMPLO_CARRITO)))
            @RequestBody Carrito carrito) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carritoService.save(carrito));
    }

    @Operation(summary = "Actualizar un ítem del carrito",
            description = "Modifica el producto o la cantidad de un ítem de carrito existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ítem actualizado correctamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Carrito.class))),
            @ApiResponse(responseCode = "404", description = "No existe un ítem con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Carrito> actualizarCarrito(
            @Parameter(description = "Identificador único del ítem a actualizar", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos del ítem de carrito", required = true,
                    content = @Content(schema = @Schema(implementation = Carrito.class),
                            examples = @ExampleObject(name = "itemActualizado", value = EJEMPLO_CARRITO)))
            @RequestBody Carrito carrito) {
        Carrito existente = carritoService.findById(id);
        if (existente != null) {
            carrito.setId(id);
            return ResponseEntity.ok(carritoService.save(carrito));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un producto del carrito",
            description = "Elimina el ítem de carrito identificado por el ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ítem eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un ítem con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProductoDelCarrito(
            @Parameter(description = "Identificador único del ítem a eliminar", example = "1")
            @PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito != null) {
            carritoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
