package com.minimarket.service.impl;

import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.exception.DatosIncompletosException;
import com.minimarket.exception.StockInsuficienteException;
import com.minimarket.repository.CarritoRepository;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Carrito> findAll() {
        return carritoRepository.findAll();
    }

    @Override
    public Carrito findById(Long id) {
        return carritoRepository.findById(id).orElse(null);
    }

    @Override
    public Carrito save(Carrito carrito) {
        return carritoRepository.save(carrito);
    }

    @Override
    public void deleteById(Long id) {
        carritoRepository.deleteById(id);
    }

    @Override
    public List<Carrito> findByUsuarioId(Long usuarioId) {
        return carritoRepository.findByUsuarioId(usuarioId);
    }

    @Override
    public Carrito agregarProducto(Long usuarioId, Long productoId, int cantidad) {
        if (cantidad <= 0) {
            throw new DatosIncompletosException("La cantidad debe ser mayor a cero");
        }

        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + productoId));

        if (producto.getStock() < cantidad) {
            throw new StockInsuficienteException(
                    "Stock insuficiente. Disponible: " + producto.getStock() + ", solicitado: " + cantidad);
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));

        Optional<Carrito> existente = carritoRepository.findByUsuarioIdAndProductoId(usuarioId, productoId);
        if (existente.isPresent()) {
            Carrito carrito = existente.get();
            carrito.setCantidad(carrito.getCantidad() + cantidad);
            return carritoRepository.save(carrito);
        }

        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setProducto(producto);
        carrito.setCantidad(cantidad);
        return carritoRepository.save(carrito);
    }
}
