package com.minimarket.controller;

import com.minimarket.assembler.CategoriaModelAssembler;
import com.minimarket.entity.Categoria;
import com.minimarket.service.CategoriaService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Categorías", description = "Gestión de las categorías de productos del minimarket. " +
        "Las respuestas incluyen un enlace HATEOAS hacia los productos de cada categoría.")
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private static final String EJEMPLO_CATEGORIA = """
            {
              "nombre": "Bebidas"
            }""";

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CategoriaModelAssembler categoriaModelAssembler;

    @Operation(summary = "Listar todas las categorías",
            description = "Retorna todas las categorías registradas para clasificar productos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping
    public CollectionModel<EntityModel<Categoria>> listarCategorias() {
        var modelos = categoriaService.findAll().stream()
                .map(categoriaModelAssembler::toModel)
                .toList();
        return CollectionModel.of(modelos,
                linkTo(methodOn(CategoriaController.class).listarCategorias()).withSelfRel());
    }

    @Operation(summary = "Obtener una categoría por ID",
            description = "Busca una categoría específica por su identificador único.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría encontrada", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "No existe una categoría con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Categoria>> obtenerCategoriaPorId(
            @Parameter(description = "Identificador único de la categoría", example = "1")
            @PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        return (categoria != null)
                ? ResponseEntity.ok(categoriaModelAssembler.toModel(categoria))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Crear una nueva categoría",
            description = "Registra una categoría con nombre único.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoría creada correctamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Datos inválidos: nombre faltante o duplicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<EntityModel<Categoria>> guardarCategoria(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la categoría a crear", required = true,
                    content = @Content(schema = @Schema(implementation = Categoria.class),
                            examples = @ExampleObject(name = "nuevaCategoria", value = EJEMPLO_CATEGORIA)))
            @RequestBody Categoria categoria) {
        Categoria guardada = categoriaService.save(categoria);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaModelAssembler.toModel(guardada));
    }

    @Operation(summary = "Actualizar una categoría existente",
            description = "Reemplaza los datos de la categoría identificada por el ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría actualizada correctamente", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Datos inválidos: nombre faltante o duplicado", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe una categoría con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Categoria>> actualizarCategoria(
            @Parameter(description = "Identificador único de la categoría a actualizar", example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevos datos de la categoría", required = true,
                    content = @Content(schema = @Schema(implementation = Categoria.class),
                            examples = @ExampleObject(name = "categoriaActualizada", value = EJEMPLO_CATEGORIA)))
            @RequestBody Categoria categoria) {
        Categoria categoriaExistente = categoriaService.findById(id);
        if (categoriaExistente != null) {
            categoria.setId(id);
            return ResponseEntity.ok(categoriaModelAssembler.toModel(categoriaService.save(categoria)));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una categoría",
            description = "Elimina la categoría identificada por el ID. Sus productos asociados también se eliminan (cascade).")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoría eliminada correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "No existe una categoría con el ID indicado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(
            @Parameter(description = "Identificador único de la categoría a eliminar", example = "1")
            @PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        if (categoria != null) {
            categoriaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
