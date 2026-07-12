package com.minimarket.assembler;

import com.minimarket.controller.DetalleVentaController;
import com.minimarket.controller.ProductoController;
import com.minimarket.controller.VentaController;
import com.minimarket.entity.DetalleVenta;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Ensambla un {@link DetalleVenta} con enlaces HATEOAS: self, colección de
 * detalles, venta a la que pertenece y producto vendido.
 */
@Component
public class DetalleVentaModelAssembler implements RepresentationModelAssembler<DetalleVenta, EntityModel<DetalleVenta>> {

    @Override
    public EntityModel<DetalleVenta> toModel(DetalleVenta detalleVenta) {
        EntityModel<DetalleVenta> model = EntityModel.of(detalleVenta,
                linkTo(methodOn(DetalleVentaController.class).obtenerDetalleVentaPorId(detalleVenta.getId())).withSelfRel(),
                linkTo(methodOn(DetalleVentaController.class).listarDetalleVentas(null)).withRel("detalle-ventas"));

        if (detalleVenta.getVenta() != null) {
            model.add(linkTo(methodOn(VentaController.class)
                    .obtenerVentaPorId(detalleVenta.getVenta().getId())).withRel("venta"));
        }
        if (detalleVenta.getProducto() != null) {
            model.add(linkTo(methodOn(ProductoController.class)
                    .obtenerProductoPorId(detalleVenta.getProducto().getId())).withRel("producto"));
        }

        return model;
    }
}
