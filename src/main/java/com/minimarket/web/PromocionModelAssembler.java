package com.minimarket.web;

import com.minimarket.controller.PromocionController;
import com.minimarket.entity.Promocion;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/** Ensambla los enlaces HATEOAS de Promocion: self y colección (FR-013). */
@Component
public class PromocionModelAssembler implements RepresentationModelAssembler<Promocion, EntityModel<Promocion>> {

    @Override
    public EntityModel<Promocion> toModel(Promocion promocion) {
        return EntityModel.of(promocion,
                linkTo(methodOn(PromocionController.class).obtenerPromocionPorId(promocion.getId())).withSelfRel(),
                linkTo(methodOn(PromocionController.class).listarPromociones()).withRel("promociones")
        );
    }
}
