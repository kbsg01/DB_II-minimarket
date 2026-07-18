package com.minimarket.web;

import com.minimarket.controller.OrdenDeCompraController;
import com.minimarket.entity.OrdenDeCompra;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/** Ensambla los enlaces HATEOAS de OrdenDeCompra: self y colección (FR-013). */
@Component
public class OrdenDeCompraModelAssembler implements RepresentationModelAssembler<OrdenDeCompra, EntityModel<OrdenDeCompra>> {

    @Override
    public EntityModel<OrdenDeCompra> toModel(OrdenDeCompra orden) {
        return EntityModel.of(orden,
                linkTo(methodOn(OrdenDeCompraController.class).obtenerOrdenPorId(orden.getId())).withSelfRel(),
                linkTo(methodOn(OrdenDeCompraController.class).listarOrdenesDeCompra()).withRel("ordenes-compra")
        );
    }
}
