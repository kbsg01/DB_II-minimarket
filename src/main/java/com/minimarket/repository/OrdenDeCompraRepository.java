package com.minimarket.repository;

import com.minimarket.entity.OrdenDeCompra;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdenDeCompraRepository extends JpaRepository<OrdenDeCompra, Long> {
    Optional<OrdenDeCompra> findByProducto_IdAndSucursal_IdAndProveedor_IdAndEstado(
            Long productoId, Long sucursalId, Long proveedorId, String estado);
}
