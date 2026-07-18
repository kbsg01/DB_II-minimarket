package com.minimarket.web;

import com.minimarket.controller.CarritoController;
import com.minimarket.controller.ProductoController;
import com.minimarket.entity.Carrito;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/** Ensambla los enlaces HATEOAS de Carrito: self, colección del usuario y producto (FR-013). */
@Component
public class CarritoModelAssembler implements RepresentationModelAssembler<Carrito, EntityModel<Carrito>> {

    @Override
    public EntityModel<Carrito> toModel(Carrito carrito) {
        return EntityModel.of(carrito,
                linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(carrito.getId())).withSelfRel(),
                linkTo(methodOn(CarritoController.class).listarCarrito(carrito.getUsuario().getId())).withRel("carrito"),
                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(carrito.getProducto().getId())).withRel("producto")
        );
    }
}
