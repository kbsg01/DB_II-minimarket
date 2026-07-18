package com.minimarket.service.dto;

public class RotacionProductoDTO {

    private Long productoId;
    private String nombreProducto;
    private int cantidadVendida;

    public RotacionProductoDTO(Long productoId, String nombreProducto, int cantidadVendida) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidadVendida = cantidadVendida;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }

    public void setCantidadVendida(int cantidadVendida) {
        this.cantidadVendida = cantidadVendida;
    }
}
