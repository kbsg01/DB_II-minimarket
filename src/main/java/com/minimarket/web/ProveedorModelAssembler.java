package com.minimarket.web;

import com.minimarket.controller.ProveedorController;
import com.minimarket.entity.Proveedor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/** Ensambla los enlaces HATEOAS de Proveedor: self y colección (FR-013). */
@Component
public class ProveedorModelAssembler implements RepresentationModelAssembler<Proveedor, EntityModel<Proveedor>> {

    @Override
    public EntityModel<Proveedor> toModel(Proveedor proveedor) {
        return EntityModel.of(proveedor,
                linkTo(methodOn(ProveedorController.class).obtenerProveedorPorId(proveedor.getId())).withSelfRel(),
                linkTo(methodOn(ProveedorController.class).listarProveedores()).withRel("proveedores")
        );
    }
}
