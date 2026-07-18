package com.minimarket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    /**
     * Lado inverso de la relación con Producto. Se ignora en JSON: sin esto,
     * Producto->categoria->productos->cada producto->categoria->... recursa hasta el
     * límite de anidamiento de Jackson (1000), tal como Usuario/Rol y Venta/DetalleVenta
     * (T044). La consulta de productos por categoría ya existe como endpoint dedicado
     * (`ProductoService.findByCategoriaId`), por lo que no se pierde funcionalidad.
     */
    @JsonIgnore
    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Producto> productos;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}
