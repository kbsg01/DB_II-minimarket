package com.minimarket.assembler;

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
 * Ensambla un {@link Producto} con enlaces HATEOAS: self, colección de
 * productos, categoría a la que pertenece e inventario asociado.
 */
@Component
public class ProductoModelAssembler implements RepresentationModelAssembler<Producto, EntityModel<Producto>> {

    @Override
    public EntityModel<Producto> toModel(Producto producto) {
        EntityModel<Producto> model = EntityModel.of(producto,
                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(producto.getId())).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarProductos(null)).withRel("productos"));

        if (producto.getCategoria() != null) {
            model.add(linkTo(methodOn(CategoriaController.class)
                    .obtenerCategoriaPorId(producto.getCategoria().getId())).withRel("categoria"));
        }

        model.add(linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario(producto.getId()))
                .withRel("inventario"));

        return model;
    }
}
