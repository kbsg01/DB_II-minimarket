package com.minimarket.assembler;

import com.minimarket.controller.InventarioController;
import com.minimarket.controller.ProductoController;
import com.minimarket.entity.Inventario;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Ensambla un movimiento de {@link Inventario} con enlaces HATEOAS: self,
 * colección de movimientos y producto afectado.
 */
@Component
public class InventarioModelAssembler implements RepresentationModelAssembler<Inventario, EntityModel<Inventario>> {

    @Override
    public EntityModel<Inventario> toModel(Inventario inventario) {
        EntityModel<Inventario> model = EntityModel.of(inventario,
                linkTo(methodOn(InventarioController.class).obtenerMovimientoPorId(inventario.getId())).withSelfRel(),
                linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario(null)).withRel("inventario"));

        if (inventario.getProducto() != null) {
            model.add(linkTo(methodOn(ProductoController.class)
                    .obtenerProductoPorId(inventario.getProducto().getId())).withRel("producto"));
        }

        return model;
    }
}
