package com.minimarket.assembler;

import com.minimarket.controller.CarritoController;
import com.minimarket.controller.ProductoController;
import com.minimarket.controller.UsuarioController;
import com.minimarket.entity.Carrito;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Ensambla un ítem de {@link Carrito} con enlaces HATEOAS: self, colección
 * de carritos, producto agregado y usuario dueño del carrito.
 */
@Component
public class CarritoModelAssembler implements RepresentationModelAssembler<Carrito, EntityModel<Carrito>> {

    @Override
    public EntityModel<Carrito> toModel(Carrito carrito) {
        EntityModel<Carrito> model = EntityModel.of(carrito,
                linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(carrito.getId())).withSelfRel(),
                linkTo(methodOn(CarritoController.class).listarCarrito(null)).withRel("carritos"));

        if (carrito.getProducto() != null) {
            model.add(linkTo(methodOn(ProductoController.class)
                    .obtenerProductoPorId(carrito.getProducto().getId())).withRel("producto"));
        }
        if (carrito.getUsuario() != null) {
            model.add(linkTo(methodOn(UsuarioController.class)
                    .obtenerUsuarioPorId(carrito.getUsuario().getId())).withRel("usuario"));
        }

        return model;
    }
}
