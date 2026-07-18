package com.minimarket.web;

import com.minimarket.controller.DetalleVentaController;
import com.minimarket.controller.VentaController;
import com.minimarket.entity.Venta;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/** Ensambla los enlaces HATEOAS de Venta: self, colección y su detalle (FR-013). */
@Component
public class VentaModelAssembler implements RepresentationModelAssembler<Venta, EntityModel<Venta>> {

    @Override
    public EntityModel<Venta> toModel(Venta venta) {
        return EntityModel.of(venta,
                linkTo(methodOn(VentaController.class).obtenerVentaPorId(venta.getId())).withSelfRel(),
                linkTo(methodOn(VentaController.class).listarVentas(null)).withRel("ventas"),
                linkTo(methodOn(DetalleVentaController.class).listarDetalleVentas(venta.getId())).withRel("detalle")
        );
    }
}
