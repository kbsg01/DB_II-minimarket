package com.minimarket.controller;

import com.minimarket.entity.Rol;
import com.minimarket.entity.Usuario;
import com.minimarket.security.model.LoginRequest;
import com.minimarket.security.model.LoginResponse;
import com.minimarket.security.util.JwtUtil;
import com.minimarket.service.RolService;
import com.minimarket.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Controlador de autenticación y registro de usuarios.
 * Expone endpoints públicos (sin JWT): /auth/login y /auth/registro.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UsuarioService usuarioService,
                          RolService rolService,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.usuarioService = usuarioService;
        this.rolService = rolService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Endpoint de autenticación (login).
     * Recibe credenciales, valida contra la BD y devuelve un JWT si son correctas.
     *
     * POST /auth/login
     * Body: { "username": "...", "password": "..." }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Spring Security valida las credenciales usando CustomUserDetailsService
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Generamos el JWT a partir del UserDetails autenticado
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new LoginResponse(token));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas. Verifica usuario y contraseña.");
        }
    }

    /**
     * Endpoint de registro de nuevos usuarios.
     * Asigna por defecto el rol CLIENTE; el rol puede cambiarse a través del parámetro opcional.
     *
     * POST /auth/registro
     * Body: { "username": "...", "password": "..." }
     * Param (opcional): rol=EMPLEADO|GERENTE|CLIENTE
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody LoginRequest request,
                                      @RequestParam(defaultValue = "CLIENTE") String rol) {
        // Verificar que el username no exista ya
        if (usuarioService.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("El usuario '" + request.getUsername() + "' ya existe.");
        }

        // Buscar el rol solicitado en la BD
        Optional<Rol> rolOpt = rolService.findByNombre(rol);
        if (rolOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Rol no válido: " + rol + ". Opciones: CLIENTE, EMPLEADO, GERENTE");
        }

        // Crear y persistir el nuevo usuario con contraseña hasheada (BCrypt)
        Usuario nuevo = new Usuario();
        nuevo.setUsername(request.getUsername());
        nuevo.setPassword(passwordEncoder.encode(request.getPassword())); // hashing seguro

        Set<Rol> roles = new HashSet<>();
        roles.add(rolOpt.get());
        nuevo.setRoles(roles);

        usuarioService.save(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Usuario '" + request.getUsername() + "' registrado con rol " + rol);
    }
}
