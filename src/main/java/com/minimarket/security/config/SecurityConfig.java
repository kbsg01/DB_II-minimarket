package com.minimarket.security.config;

import com.minimarket.security.filter.JwtAuthenticationFilter;
import com.minimarket.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración central de Spring Security.
 *
 * Estrategia adoptada:
 *  - Autenticación STATELESS via JWT (sin sesiones HTTP ni cookies).
 *  - CSRF deshabilitado (apropiado para APIs REST con JWT).
 *  - Roles: CLIENTE, EMPLEADO, GERENTE con restricciones por endpoint.
 *  - @EnableMethodSecurity habilita @PreAuthorize / @PostAuthorize en controllers.
 */
@Configuration
@EnableMethodSecurity          // habilita anotaciones @PreAuthorize en los controllers
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Cadena de filtros de seguridad principal.
     * Define qué endpoints son públicos y cuáles requieren roles específicos.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ── Deshabilitar CSRF: no necesario con JWT stateless ──────────────
            .csrf(csrf -> csrf.disable())

            // ── Política de sesión: STATELESS (no se crean sesiones HTTP) ──────
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ── Reglas de autorización por endpoint ───────────────────────────
            .authorizeHttpRequests(auth -> auth

                // Endpoints públicos: autenticación, registro y consola H2
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()

                // Gestión de usuarios y roles: solo GERENTE
                .requestMatchers("/api/usuarios/**").hasAuthority("GERENTE")

                // Ventas y detalle de ventas: GERENTE y EMPLEADO
                .requestMatchers("/api/ventas/**").hasAnyAuthority("GERENTE", "EMPLEADO")
                .requestMatchers("/api/detalleventa/**").hasAnyAuthority("GERENTE", "EMPLEADO")

                // Inventario: GERENTE y EMPLEADO
                .requestMatchers("/api/inventario/**").hasAnyAuthority("GERENTE", "EMPLEADO")

                // Catálogo de categorías: todos los autenticados
                .requestMatchers("/api/categorias/**").authenticated()

                // Catálogo de productos: todos los autenticados
                .requestMatchers("/api/productos/**").authenticated()

                // Carrito: CLIENTE (y GERENTE para supervisión)
                .requestMatchers("/api/carrito/**").hasAnyAuthority("CLIENTE", "GERENTE")

                // Cualquier otro endpoint requiere autenticación
                .anyRequest().authenticated()
            )

            // ── Desactivar formulario de login y HTTP Basic (usamos JWT) ──────
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            // ── Permitir frames de H2 Console (misma origen) ──────────────────
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

            // ── Registrar nuestro filtro JWT ANTES del filtro estándar ─────────
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * AuthenticationManager expuesto como Bean para uso en AuthController.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
            throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Encoder BCrypt para hashing de contraseñas.
     * Factor de coste por defecto: 10 rondas (balance seguridad/rendimiento).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
