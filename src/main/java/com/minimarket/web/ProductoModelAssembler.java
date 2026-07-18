package com.minimarket.web;

import com.minimarket.controller.CategoriaController;
import com.minimarket.controller.InventarioController;
import com.minimarket.controller.ProductoController;
import com.minimarket.entity.Producto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Ensambla los enlaces HATEOAS de Producto (FR-013): self, colección, categoría e
 * inventario del producto. Es el primer assembler real del proyecto — ninguna rama del
 * historial (S1-S8) tenía una implementación real de HATEOAS (ver research.md de
 * specs/002, Decisión 4, y doc/grupo7.html).
 */
@Component
public class ProductoModelAssembler implements RepresentationModelAssembler<Producto, EntityModel<Producto>> {

    @Override
    public EntityModel<Producto> toModel(Producto producto) {
        return EntityModel.of(producto,
                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(producto.getId())).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("productos"),
                linkTo(methodOn(CategoriaController.class).obtenerCategoriaPorId(producto.getCategoria().getId())).withRel("categoria"),
                linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario(producto.getId())).withRel("inventario")
        );
    }
}
