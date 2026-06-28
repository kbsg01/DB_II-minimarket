package com.minimarket.controller;

import com.minimarket.entity.Carrito;
import com.minimarket.service.CarritoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Carrito", description = "Gestión del carrito de compras. Cualquier usuario autenticado puede ver y modificar su carrito. Requiere token JWT.")
@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Operation(summary = "Listar ítems del carrito", description = "Retorna todos los ítems en el carrito de compras.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de ítems del carrito"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public List<Carrito> listarCarrito() {
        return carritoService.findAll();
    }

    @Operation(summary = "Obtener ítem por ID", description = "Retorna un ítem específico del carrito dado su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ítem encontrado"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Ítem no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Carrito> obtenerCarritoPorId(@PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        return (carrito != null) ? ResponseEntity.ok(carrito) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Agregar producto al carrito",
               description = "Agrega un producto al carrito. Valida que la cantidad sea positiva y que haya stock suficiente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto agregado correctamente"),
        @ApiResponse(responseCode = "400", description = "Cantidad inválida (DatosIncompletosException)"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "409", description = "Stock insuficiente (StockInsuficienteException)")
    })
    @PostMapping
    public Carrito agregarProductoAlCarrito(@RequestBody Carrito carrito) {
        return carritoService.save(carrito);
    }

    @Operation(summary = "Actualizar ítem del carrito", description = "Actualiza la cantidad de un producto en el carrito.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Ítem actualizado"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Ítem no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Carrito> actualizarCarrito(@PathVariable Long id, @RequestBody Carrito carrito) {
        Carrito existente = carritoService.findById(id);
        if (existente != null) {
            carrito.setId(id);
            return ResponseEntity.ok(carritoService.save(carrito));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar ítem del carrito", description = "Elimina un producto del carrito dado su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Ítem eliminado"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Ítem no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProductoDelCarrito(@PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito != null) {
            carritoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
