package com.minimarket.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Schema(description = "Movimiento de inventario (entrada o salida) de un producto")
public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del movimiento", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    @Schema(description = "Producto afectado por el movimiento")
    private Producto producto;

    @Column(nullable = false)
    @Schema(description = "Cantidad de unidades del movimiento", example = "10")
    private Integer cantidad;

    @Column(nullable = false)
    @Schema(description = "Tipo de movimiento de inventario", example = "Entrada", allowableValues = {"Entrada", "Salida"})
    private String tipoMovimiento; // Ejemplo: "Entrada" o "Salida"

    @Column(nullable = false)
    @Schema(description = "Fecha y hora del movimiento", example = "2026-07-05T10:00:00.000+00:00")
    private Date fechaMovimiento;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Date getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Date fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }
}
