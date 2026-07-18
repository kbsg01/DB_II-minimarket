package com.minimarket.controller;

import com.minimarket.entity.Categoria;
import com.minimarket.service.CategoriaService;
import com.minimarket.web.CategoriaModelAssembler;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "Categorías", description = "Categorías de producto. Mutaciones reservadas a roles de gestión.")
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CategoriaModelAssembler categoriaModelAssembler;

    @Operation(summary = "Listar todas las categorías")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de categorías")
    })
    @GetMapping
    public CollectionModel<EntityModel<Categoria>> listarCategorias() {
        List<EntityModel<Categoria>> modelos = categoriaService.findAll().stream()
                .map(categoriaModelAssembler::toModel)
                .toList();
        return CollectionModel.of(modelos,
                linkTo(methodOn(CategoriaController.class).listarCategorias()).withSelfRel());
    }

    @Operation(summary = "Obtener una categoría por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría encontrada"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Categoria>> obtenerCategoriaPorId(@PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        return (categoria != null)
                ? ResponseEntity.ok(categoriaModelAssembler.toModel(categoria))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Crear una categoría")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría creada"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización")
    })
    @PreAuthorize("hasAnyRole('GERENTE_SUCURSAL','ADMINISTRADOR')")
    @PostMapping
    public Categoria guardarCategoria(@RequestBody Categoria categoria) {
        return categoriaService.save(categoria);
    }

    @Operation(summary = "Actualizar una categoría existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Categoría actualizada"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @PreAuthorize("hasAnyRole('GERENTE_SUCURSAL','ADMINISTRADOR')")
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizarCategoria(@PathVariable Long id, @RequestBody Categoria categoria) {
        Categoria categoriaExistente = categoriaService.findById(id);
        if (categoriaExistente != null) {
            categoria.setId(id);
            return ResponseEntity.ok(categoriaService.save(categoria));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar una categoría")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Categoría eliminada"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @PreAuthorize("hasAnyRole('GERENTE_SUCURSAL','ADMINISTRADOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        if (categoria != null) {
            categoriaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
