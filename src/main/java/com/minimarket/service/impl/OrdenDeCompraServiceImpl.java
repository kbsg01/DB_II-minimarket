package com.minimarket.service.impl;

import com.minimarket.entity.OrdenDeCompra;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Sucursal;
import com.minimarket.repository.OrdenDeCompraRepository;
import com.minimarket.service.OrdenDeCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrdenDeCompraServiceImpl implements OrdenDeCompraService {

    private static final String ESTADO_PENDIENTE = "PENDIENTE";

    @Autowired
    private OrdenDeCompraRepository ordenDeCompraRepository;

    @Override
    public List<OrdenDeCompra> findAll() {
        return ordenDeCompraRepository.findAll();
    }

    @Override
    public OrdenDeCompra findById(Long id) {
        return ordenDeCompraRepository.findById(id).orElse(null);
    }

    @Override
    public OrdenDeCompra generarSiNecesario(Producto producto, Sucursal sucursal, int stockVigente) {
        if (producto == null || sucursal == null) {
            return null;
        }
        Integer stockMinimo = producto.getStockMinimo();
        if (stockMinimo == null || stockVigente > stockMinimo) {
            return null;
        }
        if (producto.getProveedor() == null) {
            return null;
        }

        boolean yaExiste = ordenDeCompraRepository.findByProducto_IdAndSucursal_IdAndProveedor_IdAndEstado(
                producto.getId(), sucursal.getId(), producto.getProveedor().getId(), ESTADO_PENDIENTE
        ).isPresent();
        if (yaExiste) {
            return null;
        }

        OrdenDeCompra orden = new OrdenDeCompra();
        orden.setProducto(producto);
        orden.setSucursal(sucursal);
        orden.setProveedor(producto.getProveedor());
        orden.setCantidadSolicitada(stockMinimo);
        orden.setEstado(ESTADO_PENDIENTE);
        orden.setFechaGeneracion(new Date());
        return ordenDeCompraRepository.save(orden);
    }
}
