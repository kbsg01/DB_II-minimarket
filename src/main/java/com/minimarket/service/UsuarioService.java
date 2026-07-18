package com.minimarket.service;

import com.minimarket.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<Usuario> findAll();
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByUsername(String username);
    Usuario save(Usuario usuario);
    void deleteById(Long id);

    /** Verdadero solo si username, nombre, apellido, email y dirección están presentes. */
    boolean datosCompletos(Usuario usuario);

    /** Registra el usuario solo si sus datos están completos; si no, lanza excepción. */
    Usuario registrar(Usuario usuario);

    /** Verdadero si el usuario posee un rol habilitado para registrar ventas (ADMIN o VENDEDOR). */
    boolean puedeRegistrarVenta(Usuario usuario);
}
