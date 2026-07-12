package com.minimarket.controller;

import com.minimarket.assembler.ProductoModelAssembler;
import com.minimarket.entity.Producto;
import com.minimarket.service.ProductoService;
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

@Tag(name = "Productos", description = "Gestión del catálogo de productos del minimarket. " +
        "Las respuestas incluyen enlaces HATEOAS hacia la categoría y el inventario relacionados.")
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private static final String EJEMPLO_PRODUCTO = """
            {
              "nombre": "Coca-Cola 1.5L",
              "precio": 1890,
              "stock": 24,
              "categoria": { "id": 1 }
            }""";

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoModelAssembler productoModelAssembler;

    @Operation(summary = "Listar productos",
            description = "Retorna el catálogo de productos registrados. Si se indica `categoriaId`, filtra solo los productos de esa categoría.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping
    public CollectionModel<EntityModel<Producto>> listarProductos(
            @Parameter(description = "Filtra los productos de una categoría específica", example = "1")
            @RequestParam(required = false) Long categoriaId) {
        List<Producto> productos = (categoriaId != null)
                ? productoService.findByCategoriaId(categoriaId)
                : productoService.findAll();
        List<EntityModel<Producto>> modelos = productos.stream()
                .map(productoModelAssembler::toModel)
                .toList();
        Link self = linkTo(methodOn(ProductoController.class).listarProductos(categoriaId)).withSelfRel();
        return CollectionModel.of(modelos, self);
    }

    @Operation(summary = "Obtener un producto por ID",
            description = "Busca un producto específico por su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No existe un producto con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Producto>> obtenerProductoPorId(
            @Parameter(description = "Identificador único del producto", example = "1")
            @PathVariable Long id) {
        Producto producto = productoService.findById(id);
        return (producto != null)
                ? ResponseEntity.ok(productoModelAssembler.toModel(producto))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Crear un nuevo producto",
            description = "Registra un producto en el catálogo. La categoría referenciada debe existir previamente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado correctamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Datos inválidos: nombre, precio, stock o categoría faltantes o mal formados", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<Producto>> guardarProducto(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del producto a crear", required = true,
                    content = @Content(schema = @Schema(implementation = Producto.class),
                            examples = @ExampleObject(name = "nuevoProducto", value = EJEMPLO_PRODUCTO)))
            @RequestBody Producto producto) {
        // 201 Created: código correcto para creación de recursos REST
        Producto guardado = productoService.save(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productoModelAssembler.toModel(guardado));
    }

    @Operation(summary = "Actualizar un producto existente",
            description = "Reemplaza los datos del producto identificado por el ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Datos inválidos: nombre, precio, stock o categoría faltantes o mal formados", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un producto con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Producto>> actualizarProducto(
            @Parameter(description = "Identificador único del producto a actualizar", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos del producto", required = true,
                    content = @Content(schema = @Schema(implementation = Producto.class),
                            examples = @ExampleObject(name = "productoActualizado", value = EJEMPLO_PRODUCTO)))
            @RequestBody Producto producto) {
        Producto productoExistente = productoService.findById(id);
        if (productoExistente != null) {
            producto.setId(id);
            return ResponseEntity.ok(productoModelAssembler.toModel(productoService.save(producto)));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar un producto",
            description = "Elimina definitivamente el producto identificado por el ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe un producto con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(
            @Parameter(description = "Identificador único del producto a eliminar", example = "1")
            @PathVariable Long id) {
        Producto producto = productoService.findById(id);
        if (producto != null) {
            productoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
