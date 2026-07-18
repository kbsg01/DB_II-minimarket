package com.minimarket.web;

import com.minimarket.controller.InventarioController;
import com.minimarket.controller.ProductoController;
import com.minimarket.entity.Inventario;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Ensambla los enlaces HATEOAS de Inventario: self, colección y su producto (FR-013).
 * No se enlaza a un recurso de Sucursal porque esa entidad no expone un controlador REST
 * propio en el alcance actual (Sucursal solo se referencia por id desde otros recursos).
 */
@Component
public class InventarioModelAssembler implements RepresentationModelAssembler<Inventario, EntityModel<Inventario>> {

    @Override
    public EntityModel<Inventario> toModel(Inventario inventario) {
        return EntityModel.of(inventario,
                linkTo(methodOn(InventarioController.class).obtenerMovimientoPorId(inventario.getId())).withSelfRel(),
                linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario(null)).withRel("inventario"),
                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(inventario.getProducto().getId())).withRel("producto")
        );
    }
}
