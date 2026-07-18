package com.minimarket.service.impl;

import com.minimarket.entity.DetalleVenta;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Venta;
import com.minimarket.repository.VentaRepository;
import com.minimarket.service.ReporteService;
import com.minimarket.service.dto.RotacionProductoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReporteServiceImpl implements ReporteService {

    @Autowired
    private VentaRepository ventaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RotacionProductoDTO> obtenerRotacion(Date desde, Date hasta) {
        List<Venta> ventas = ventaRepository.findByFechaBetween(desde, hasta);

        Map<Long, RotacionProductoDTO> acumulado = new LinkedHashMap<>();
        for (Venta venta : ventas) {
            if (venta.getDetalles() == null) {
                continue;
            }
            for (DetalleVenta detalle : venta.getDetalles()) {
                Producto producto = detalle.getProducto();
                RotacionProductoDTO dto = acumulado.computeIfAbsent(producto.getId(),
                        id -> new RotacionProductoDTO(producto.getId(), producto.getNombre(), 0));
                dto.setCantidadVendida(dto.getCantidadVendida() + detalle.getCantidad());
            }
        }

        return acumulado.values().stream()
                .sorted(Comparator.comparingInt(RotacionProductoDTO::getCantidadVendida).reversed())
                .toList();
    }
}
