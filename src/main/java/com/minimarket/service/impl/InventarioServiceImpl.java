package com.minimarket.service.impl;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.exception.DatosIncompletosException;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.InventarioService;
import com.minimarket.service.OrdenDeCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioServiceImpl implements InventarioService {

    private static final String SALIDA = "Salida";
    private static final String ENTRADA = "Entrada";

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private OrdenDeCompraService ordenDeCompraService;

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Inventario> findAll() {
        return inventarioRepository.findAll();
    }

    @Override
    public Inventario findById(Long id) {
        return inventarioRepository.findById(id).orElse(null);
    }

    @Override
    public Inventario save(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }

    @Override
    public void deleteById(Long id) {
        inventarioRepository.deleteById(id);
    }

    @Override
    public List<Inventario> findByProductoId(Long productoId) {
        return inventarioRepository.findByProductoId(productoId);
    }

    @Override
    public Inventario registrarMovimiento(Inventario inventario) {
        validarMovimiento(inventario);

        Inventario guardado = inventarioRepository.save(inventario);

        if (SALIDA.equalsIgnoreCase(guardado.getTipoMovimiento())) {
            int stockVigente = calcularStockVigente(
                    guardado.getProducto().getId(), guardado.getSucursal().getId());
            // Recargar el Producto por completo: el body JSON de POST /api/inventario solo
            // trae {"id": X}, y OrdenDeCompraService.generarSiNecesario necesita
            // stockMinimo/proveedor reales, no una referencia parcial — sin este reload la
            // reposición automática (FR-006) fallaba en silencio (sin error, sin orden) en
            // cualquier llamada real a la API, hallazgo detectado al ejercitar el endpoint
            // en vivo, no solo por lectura de código.
            Producto productoCompleto = productoRepository.findById(guardado.getProducto().getId()).orElse(null);
            ordenDeCompraService.generarSiNecesario(
                    productoCompleto, guardado.getSucursal(), stockVigente);
        }

        return guardado;
    }

    @Override
    public Inventario actualizarMovimiento(Long id, Inventario inventario) {
        validarMovimiento(inventario);
        inventario.setId(id);
        return inventarioRepository.save(inventario);
    }

    private void validarMovimiento(Inventario inventario) {
        if (inventario.getTipoMovimiento() == null || inventario.getTipoMovimiento().isBlank()) {
            throw new DatosIncompletosException("El tipo de movimiento no puede ser nulo o vacío");
        }
        if (inventario.getCantidad() == null || inventario.getCantidad() <= 0) {
            throw new DatosIncompletosException("La cantidad debe ser un valor positivo");
        }
        if (inventario.getProducto() == null) {
            throw new DatosIncompletosException("El producto no puede ser nulo");
        }
        if (inventario.getSucursal() == null) {
            throw new DatosIncompletosException("La sucursal no puede ser nula");
        }
    }

    @Override
    public int calcularStockVigente(Long productoId, Long sucursalId) {
        List<Inventario> movimientos = inventarioRepository.findByProductoIdAndSucursalId(productoId, sucursalId);
        int stock = 0;
        for (Inventario movimiento : movimientos) {
            if (ENTRADA.equalsIgnoreCase(movimiento.getTipoMovimiento())) {
                stock += movimiento.getCantidad();
            } else if (SALIDA.equalsIgnoreCase(movimiento.getTipoMovimiento())) {
                stock -= movimiento.getCantidad();
            }
        }
        return stock;
    }
}
