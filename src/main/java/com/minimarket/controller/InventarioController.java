package com.minimarket.controller;

import com.minimarket.entity.Inventario;
import com.minimarket.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Inventario", description = "Registro de movimientos de inventario (entradas y salidas de stock). Requiere ROLE_CAJERO o ROLE_ADMIN.")
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Operation(summary = "Listar movimientos de inventario", description = "Retorna todos los movimientos registrados en el inventario.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de movimientos"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public List<Inventario> listarMovimientosDeInventario() {
        return inventarioService.findAll();
    }

    @Operation(summary = "Obtener movimiento por ID", description = "Retorna un movimiento de inventario específico dado su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Movimiento encontrado"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Inventario> obtenerMovimientoPorId(@PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        return (inventario != null) ? ResponseEntity.ok(inventario) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Registrar movimiento de inventario",
               description = "Registra una entrada o salida de stock. Requiere ROLE_CAJERO o ROLE_ADMIN. Valida disponibilidad de stock.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Movimiento registrado"),
        @ApiResponse(responseCode = "400", description = "Datos incompletos (DatosIncompletosException)"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "409", description = "Stock insuficiente (StockInsuficienteException)")
    })
    @PostMapping
    public Inventario registrarMovimiento(@RequestBody Inventario inventario) {
        return inventarioService.save(inventario);
    }

    @Operation(summary = "Actualizar movimiento de inventario", description = "Actualiza los datos de un movimiento existente. Requiere ROLE_CAJERO o ROLE_ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Movimiento actualizado"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado"),
        @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Inventario> actualizarMovimiento(@PathVariable Long id, @RequestBody Inventario inventario) {
        Inventario existente = inventarioService.findById(id);
        if (existente != null) {
            inventario.setId(id);
            return ResponseEntity.ok(inventarioService.save(inventario));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar movimiento de inventario", description = "Elimina un registro de movimiento dado su ID. Requiere ROLE_ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Movimiento eliminado"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado — se requiere ROLE_ADMIN"),
        @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        if (inventario != null) {
            inventarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
