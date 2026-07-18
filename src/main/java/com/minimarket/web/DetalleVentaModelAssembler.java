package com.minimarket.web;

import com.minimarket.controller.DetalleVentaController;
import com.minimarket.controller.VentaController;
import com.minimarket.entity.DetalleVenta;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/** Ensambla los enlaces HATEOAS de DetalleVenta: self, colección y su venta (FR-013). */
@Component
public class DetalleVentaModelAssembler implements RepresentationModelAssembler<DetalleVenta, EntityModel<DetalleVenta>> {

    @Override
    public EntityModel<DetalleVenta> toModel(DetalleVenta detalleVenta) {
        return EntityModel.of(detalleVenta,
                linkTo(methodOn(DetalleVentaController.class).obtenerDetalleVentaPorId(detalleVenta.getId())).withSelfRel(),
                linkTo(methodOn(DetalleVentaController.class).listarDetalleVentas(null)).withRel("detalle-ventas"),
                linkTo(methodOn(VentaController.class).obtenerVentaPorId(detalleVenta.getVenta().getId())).withRel("venta")
        );
    }
}
