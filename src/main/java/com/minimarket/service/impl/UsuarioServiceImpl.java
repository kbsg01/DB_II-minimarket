package com.minimarket.service.impl;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.exception.DatosIncompletosException;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Override
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public void deleteById(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public boolean datosCompletos(Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        return tieneValor(usuario.getUsername())
                && tieneValor(usuario.getNombre())
                && tieneValor(usuario.getApellido())
                && tieneValor(usuario.getEmail())
                && tieneValor(usuario.getDireccion());
    }

    @Override
    public Usuario registrar(Usuario usuario) {
        if (!datosCompletos(usuario)) {
            throw new DatosIncompletosException(
                    "El usuario no puede registrarse: faltan datos obligatorios " +
                    "(username, nombre, apellido, email o dirección).");
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    public boolean puedeRegistrarVenta(Usuario usuario) {
        if (usuario == null || usuario.getRoles() == null) {
            return false;
        }
        Set<Rol> roles = usuario.getRoles();
        return roles.stream()
                .filter(rol -> rol != null && rol.getNombre() != null)
                .anyMatch(rol -> {
                    String nombre = rol.getNombre().toUpperCase();
                    return nombre.equals("ADMIN") || nombre.equals("VENDEDOR");
                });
    }

    private boolean tieneValor(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }
}
