package com.minimarket.service;

import com.minimarket.service.dto.RotacionProductoDTO;

import java.util.Date;
import java.util.List;

public interface ReporteService {
    /**
     * Productos más y menos vendidos (por cantidad) en un rango de fechas, ordenados
     * de mayor a menor. Retorna una lista vacía si no hay ventas en el rango (FR-007).
     */
    List<RotacionProductoDTO> obtenerRotacion(Date desde, Date hasta);
}
