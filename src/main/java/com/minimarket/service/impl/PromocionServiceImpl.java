package com.minimarket.service.impl;

import com.minimarket.entity.Producto;
import com.minimarket.entity.Promocion;
import com.minimarket.exception.DatosIncompletosException;
import com.minimarket.repository.PromocionRepository;
import com.minimarket.service.PromocionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

@Service
public class PromocionServiceImpl implements PromocionService {

    @Autowired
    private PromocionRepository promocionRepository;

    @Override
    public List<Promocion> findAll() {
        return promocionRepository.findAll();
    }

    @Override
    public Promocion findById(Long id) {
        return promocionRepository.findById(id).orElse(null);
    }

    @Override
    public Promocion save(Promocion promocion) {
        if (promocion.getFechaInicio() == null || promocion.getFechaFin() == null) {
            throw new DatosIncompletosException("fechaInicio y fechaFin son obligatorias");
        }
        if (!promocion.getFechaFin().after(promocion.getFechaInicio())) {
            throw new DatosIncompletosException("fechaFin debe ser posterior a fechaInicio");
        }
        return promocionRepository.save(promocion);
    }

    @Override
    public void deleteById(Long id) {
        promocionRepository.deleteById(id);
    }

    @Override
    public List<Promocion> findVigentesPorProducto(Long productoId, Date fecha) {
        return promocionRepository.findByProductoId(productoId).stream()
                .filter(p -> !fecha.before(p.getFechaInicio()) && !fecha.after(p.getFechaFin()))
                .toList();
    }

    @Override
    public double calcularPrecioConPromocion(Producto producto, Date fecha) {
        List<Promocion> vigentes = findVigentesPorProducto(producto.getId(), fecha);
        if (vigentes.isEmpty()) {
            return producto.getPrecio();
        }
        Promocion masReciente = vigentes.stream()
                .max(Comparator.comparing(Promocion::getFechaInicio))
                .orElseThrow();
        double descuento = masReciente.getDescuentoPorcentaje();
        return producto.getPrecio() * (1 - descuento / 100.0);
    }
}
