package com.minimarket.service.impl;

import com.minimarket.entity.Inventario;
import com.minimarket.exception.DatosIncompletosException;
import com.minimarket.repository.InventarioRepository;
import com.minimarket.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventarioServiceImpl implements InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;

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
        if (inventario.getTipoMovimiento() == null || inventario.getTipoMovimiento().isBlank()) {
            throw new DatosIncompletosException("El tipo de movimiento no puede ser nulo o vacío");
        }
        if (inventario.getCantidad() == null || inventario.getCantidad() <= 0) {
            throw new DatosIncompletosException("La cantidad debe ser un valor positivo");
        }
        if (inventario.getProducto() == null) {
            throw new DatosIncompletosException("El producto no puede ser nulo");
        }
        return inventarioRepository.save(inventario);
    }
}
