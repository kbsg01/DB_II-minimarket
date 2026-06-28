package com.minimarket.controller;

import com.minimarket.entity.Venta;
import com.minimarket.service.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Ventas", description = "Registro de ventas. La creación de ventas requiere ROLE_CAJERO. Consultas disponibles para ROLE_ADMIN y ROLE_CAJERO.")
@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Operation(summary = "Listar ventas", description = "Retorna el historial completo de ventas registradas.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de ventas"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public List<Venta> listarVentas() {
        return ventaService.findAll();
    }

    @Operation(summary = "Obtener venta por ID", description = "Retorna una venta específica dado su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Venta encontrada"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Venta no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerVentaPorId(@PathVariable Long id) {
        Venta venta = ventaService.findById(id);
        return (venta != null) ? ResponseEntity.ok(venta) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Registrar venta",
               description = "Registra una nueva venta. Valida que el usuario tenga ROLE_CAJERO mediante puedeRegistrarVenta(). El campo total se calcula internamente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Venta registrada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos incompletos o usuario sin permiso de cajero"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado — se requiere ROLE_CAJERO")
    })
    @PostMapping
    public Venta guardarVenta(@RequestBody Venta venta) {
        return ventaService.save(venta);
    }
}
