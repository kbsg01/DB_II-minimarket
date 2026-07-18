package com.minimarket.controller;

import com.minimarket.service.ReporteService;
import com.minimarket.service.dto.RotacionProductoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Tag(name = "Reportes", description = "Reportes de rotación de productos por rango de fechas (FR-007). Reservado a roles de gestión.")
@RestController
@RequestMapping("/api/reportes")
@PreAuthorize("hasAnyRole('GERENTE_SUCURSAL','ADMINISTRADOR')")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @Operation(summary = "Reporte de rotación de productos",
               description = "Productos más y menos vendidos (por cantidad) en el rango de fechas indicado.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado ordenado de mayor a menor cantidad vendida (vacío si no hay ventas en el rango)"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización")
    })
    @GetMapping("/rotacion")
    public List<RotacionProductoDTO> reporteRotacion(
            @Parameter(description = "Fecha de inicio del rango, formato yyyy-MM-dd")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date desde,
            @Parameter(description = "Fecha de fin del rango, formato yyyy-MM-dd")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date hasta) {
        return reporteService.obtenerRotacion(desde, hasta);
    }
}
