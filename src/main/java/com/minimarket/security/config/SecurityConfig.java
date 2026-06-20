package com.minimarket.security.config;

import com.minimarket.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;

/**
 * Configuración central de Spring Security para el sistema Minimarket.
 *
 * Estrategia seleccionada: Autenticación con nombre de usuario y contraseña
 * vía base de datos (DaoAuthenticationProvider + CustomUserDetailsService + Oracle DB).
 *
 * Evaluación Desarrollo Backend II – PBY2202 – Semana 1
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Habilita @PreAuthorize y @Secured a nivel de método/servicio
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ── 1. Cabeceras de seguridad HTTP (mitigación OWASP) ──────────────────────
            .headers(headers -> headers
                // X-Frame-Options: DENY — previene clickjacking
                .frameOptions(frame -> frame.deny())
                // X-Content-Type-Options: nosniff — previene MIME-sniffing (vector XSS)
                .contentTypeOptions(ct -> {})
                // Strict-Transport-Security — fuerza HTTPS en producción
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31_536_000))  // 1 año
                // Referrer-Policy — controla información de origen en requests externos
                .referrerPolicy(ref -> ref.policy(
                    ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
            )

            // ── 2. CSRF — deshabilitado para API REST sin estado ──────────────────────
            // Justificación: la API REST usa autenticación stateless (sin cookies persistentes
            // entre requests). El form login emite sesión solo para el proceso de autenticación.
            .csrf(csrf -> csrf.disable())

            // ── 3. Autorización basada en roles (RBAC) ────────────────────────────────
            .authorizeHttpRequests(auth -> auth

                // Recursos públicos — sin autenticación
                .requestMatchers("/public/**", "/auth/**").permitAll()

                // GERENTE — acceso total: gestión de usuarios y administración del sistema
                .requestMatchers("/api/usuarios/**").hasRole("GERENTE")

                // GERENTE + EMPLEADO — gestión operativa del minimarket
                .requestMatchers("/api/inventario/**").hasAnyRole("GERENTE", "EMPLEADO")
                .requestMatchers("/api/ventas/**").hasAnyRole("GERENTE", "EMPLEADO")
                .requestMatchers("/api/detalle-venta/**").hasAnyRole("GERENTE", "EMPLEADO")

                // Todos los roles autenticados — catálogo y carrito de compras
                .requestMatchers("/api/productos/**").hasAnyRole("GERENTE", "EMPLEADO", "CLIENTE")
                .requestMatchers("/api/categorias/**").hasAnyRole("GERENTE", "EMPLEADO", "CLIENTE")
                .requestMatchers("/api/carrito/**").hasAnyRole("GERENTE", "EMPLEADO", "CLIENTE")

                // Cualquier otra ruta requiere autenticación válida
                .anyRequest().authenticated()
            )

            // ── 4. Formulario de autenticación con usuario y contraseña ───────────────
            // Método seleccionado para Semana 1: username/password vía Oracle DB
            .formLogin(form -> form
                .loginPage("/auth/login")              // GET — muestra el formulario de login
                .loginProcessingUrl("/auth/login")     // POST — Spring procesa las credenciales
                .defaultSuccessUrl("/api/productos", true)
                .failureUrl("/auth/login?error=true")
                .permitAll()
            )

            // ── 5. Logout seguro ──────────────────────────────────────────────────────
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/public/index")
                .invalidateHttpSession(true)           // Invalida la sesión de Spring Security
                .deleteCookies("JSESSIONID")           // Elimina la cookie de sesión del cliente
                .clearAuthentication(true)
                .permitAll()
            );

        return http.build();
    }

    /**
     * AuthenticationManager expuesto como Bean para inyección en
     * controladores de autenticación personalizados.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * BCryptPasswordEncoder con strength=12.
     * Recomendación NIST SP 800-63B: factor de coste que haga cada
     * verificación ~100ms en hardware moderno. Strength 12 cumple este criterio.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
