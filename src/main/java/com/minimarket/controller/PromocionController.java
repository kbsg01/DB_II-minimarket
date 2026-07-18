package com.minimarket.controller;

import com.minimarket.entity.Promocion;
import com.minimarket.service.PromocionService;
import com.minimarket.web.PromocionModelAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Promociones", description = "Promociones centralizadas por producto (FR-011). Reservado a roles de gestión.")
@RestController
@RequestMapping("/api/promociones")
@PreAuthorize("hasAnyRole('GERENTE_SUCURSAL','ADMINISTRADOR')")
public class PromocionController {

    @Autowired
    private PromocionService promocionService;

    @Autowired
    private PromocionModelAssembler promocionModelAssembler;

    @Operation(summary = "Listar promociones vigentes y futuras")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de promociones"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización")
    })
    @GetMapping
    public CollectionModel<EntityModel<Promocion>> listarPromociones() {
        var modelos = promocionService.findAll().stream()
                .map(promocionModelAssembler::toModel)
                .toList();
        return CollectionModel.of(modelos,
                linkTo(methodOn(PromocionController.class).listarPromociones()).withSelfRel());
    }

    @Operation(summary = "Obtener una promoción por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Promoción encontrada"),
        @ApiResponse(responseCode = "404", description = "Promoción no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Promocion>> obtenerPromocionPorId(@PathVariable Long id) {
        Promocion promocion = promocionService.findById(id);
        return (promocion != null)
                ? ResponseEntity.ok(promocionModelAssembler.toModel(promocion))
                : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Crear promoción centralizada")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Promoción creada"),
        @ApiResponse(responseCode = "400", description = "Datos incompletos o rango de fechas inválido"),
        @ApiResponse(responseCode = "403", description = "Rol sin autorización")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Promocion>> guardarPromocion(@RequestBody Promocion promocion) {
        Promocion creada = promocionService.save(promocion);
        return ResponseEntity.status(HttpStatus.CREATED).body(promocionModelAssembler.toModel(creada));
    }

    @Operation(summary = "Actualizar una promoción existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Promoción actualizada"),
        @ApiResponse(responseCode = "400", description = "Datos incompletos o rango de fechas inválido"),
        @ApiResponse(responseCode = "404", description = "Promoción no encontrada")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Promocion>> actualizarPromocion(@PathVariable Long id, @RequestBody Promocion promocion) {
        Promocion existente = promocionService.findById(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        promocion.setId(id);
        return ResponseEntity.ok(promocionModelAssembler.toModel(promocionService.save(promocion)));
    }

    @Operation(summary = "Eliminar una promoción")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Promoción eliminada"),
        @ApiResponse(responseCode = "404", description = "Promoción no encontrada")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPromocion(@PathVariable Long id) {
        Promocion existente = promocionService.findById(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        promocionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
