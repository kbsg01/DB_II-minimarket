package com.minimarket.controller;

import com.minimarket.entity.Producto;
import com.minimarket.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Productos", description = "Gestión del catálogo de productos. Lectura disponible para todos los usuarios autenticados. Creación, edición y eliminación requieren ROLE_ADMIN.")
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Operation(summary = "Listar productos", description = "Retorna el catálogo completo de productos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de productos"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping
    public List<Producto> listarProductos() {
        return productoService.findAll();
    }

    @Operation(summary = "Obtener producto por ID", description = "Retorna un producto específico dado su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerProductoPorId(@PathVariable Long id) {
        Producto producto = productoService.findById(id);
        return (producto != null) ? ResponseEntity.ok(producto) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Crear producto",
               description = "Crea un nuevo producto en el catálogo. Requiere ROLE_ADMIN. Valida que nombre, precio y stock sean datos completos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto creado"),
        @ApiResponse(responseCode = "400", description = "Datos incompletos (DatosIncompletosException)"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado — se requiere ROLE_ADMIN")
    })
    @PostMapping
    public Producto guardarProducto(@RequestBody Producto producto) {
        return productoService.save(producto);
    }

    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente. Requiere ROLE_ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Producto actualizado"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado — se requiere ROLE_ADMIN"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody Producto producto) {
        Producto productoExistente = productoService.findById(id);
        if (productoExistente != null) {
            producto.setId(id);
            return ResponseEntity.ok(productoService.save(producto));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar producto", description = "Elimina un producto del catálogo dado su ID. Requiere ROLE_ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Producto eliminado"),
        @ApiResponse(responseCode = "401", description = "No autenticado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado — se requiere ROLE_ADMIN"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long id) {
        Producto producto = productoService.findById(id);
        if (producto != null) {
            productoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
