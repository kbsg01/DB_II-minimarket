package com.minimarket.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minimarket.security.filter.JwtAuthenticationFilter;
import com.minimarket.security.handler.JwtAuthenticationEntryPoint;
import com.minimarket.security.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuración central de Spring Security para MiniMarket Plus.
 *
 * Estrategia: stateless con JWT.
 *  - No se mantiene sesión en servidor (SessionCreationPolicy.STATELESS).
 *  - CSRF deshabilitado (innecesario en APIs REST sin cookies de sesión).
 *  - El filtro JwtAuthenticationFilter intercepta cada request antes del
 *    UsernamePasswordAuthenticationFilter para poblar el SecurityContext.
 *
 * Roles definidos:
 *  ROLE_ADMIN    → acceso total
 *  ROLE_EMPLEADO → gestión de inventario, productos, categorías, ventas
 *  ROLE_CLIENTE  → consulta de productos/categorías, gestión de su carrito
 */
@Configuration
@EnableMethodSecurity   // Habilita @PreAuthorize / @PostAuthorize en controllers
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtEntryPoint;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
                          JwtAuthenticationFilter jwtAuthFilter,
                          JwtAuthenticationEntryPoint jwtEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthFilter = jwtAuthFilter;
        this.jwtEntryPoint = jwtEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // --- CSRF: innecesario en API REST stateless (sin cookies de sesión) ---
            .csrf(AbstractHttpConfigurer::disable)

            // --- Sesión: STATELESS → no se crea ni usa HttpSession ---
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // --- Entry point personalizado para errores 401/403 (JSON, no redirect) ---
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtEntryPoint)
                .accessDeniedHandler(accessDeniedHandler())
            )

            // --- Reglas de autorización por endpoint / rol ---
            .authorizeHttpRequests(auth -> auth

                // Endpoints públicos (incluye /error para que AccessDeniedHandler responda 403)
                .requestMatchers("/public/**", "/api/auth/**", "/error").permitAll()

                // Consola H2 solo en desarrollo (ADMIN)
                .requestMatchers("/h2-console/**").hasRole("ADMIN")

                // Gestión de usuarios: solo ADMIN
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

                // Inventario: ADMIN y EMPLEADO
                .requestMatchers("/api/inventario/**").hasAnyRole("ADMIN", "EMPLEADO")

                // Productos y categorías: lectura libre para autenticados, escritura solo ADMIN/EMPLEADO
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/productos/**")
                    .hasAnyRole("ADMIN", "EMPLEADO", "CLIENTE")
                .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/categorias/**")
                    .hasAnyRole("ADMIN", "EMPLEADO", "CLIENTE")
                .requestMatchers("/api/productos/**").hasAnyRole("ADMIN", "EMPLEADO")
                .requestMatchers("/api/categorias/**").hasAnyRole("ADMIN", "EMPLEADO")

                // Carrito: CLIENTE y ADMIN
                .requestMatchers("/api/carrito/**").hasAnyRole("ADMIN", "CLIENTE")

                // Ventas y detalle: ADMIN, EMPLEADO, CLIENTE
                .requestMatchers("/api/ventas/**").hasAnyRole("ADMIN", "EMPLEADO", "CLIENTE")
                .requestMatchers("/api/detalle-ventas/**").hasAnyRole("ADMIN", "EMPLEADO")

                // Cualquier otro endpoint requiere autenticación
                .anyRequest().authenticated()
            )

            // --- Agrega el filtro JWT antes del filtro de autenticación estándar ---
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

            // --- Cabeceras: permite frames para consola H2 (solo desarrollo) ---
            .headers(h -> h.frameOptions(fo -> fo.sameOrigin()));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt con factor de costo 12 (balance seguridad/rendimiento)
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Handler para errores 403 (usuario autenticado pero sin permisos suficientes).
     * Devuelve JSON en lugar del redirect por defecto de Spring Security.
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            Map<String, Object> body = new HashMap<>();
            body.put("status", 403);
            body.put("error", "Acceso denegado");
            body.put("message", "No tienes permisos para acceder a este recurso.");
            body.put("path", request.getServletPath());
            new ObjectMapper().writeValue(response.getOutputStream(), body);
        };
    }
}
