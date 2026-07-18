package com.minimarket.web;

import com.minimarket.controller.PedidoController;
import com.minimarket.entity.Pedido;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/** Ensambla los enlaces HATEOAS de Pedido: self y consulta de disponibilidad (FR-013). */
@Component
public class PedidoModelAssembler implements RepresentationModelAssembler<Pedido, EntityModel<Pedido>> {

    @Override
    public EntityModel<Pedido> toModel(Pedido pedido) {
        return EntityModel.of(pedido,
                linkTo(methodOn(PedidoController.class).obtenerPedidoPorId(pedido.getId())).withSelfRel()
        );
    }
}
