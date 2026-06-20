package com.minimarket.security.filter;

import com.minimarket.security.service.CustomUserDetailsService;
import com.minimarket.security.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de seguridad que intercepta cada request HTTP para validar el JWT.
 * Se ejecuta UNA sola vez por request (OncePerRequestFilter) y actúa
 * ANTES de UsernamePasswordAuthenticationFilter en la cadena de Spring Security.
 *
 * Flujo:
 *  1. Leer el header "Authorization: Bearer <token>"
 *  2. Extraer y validar el token con JwtUtil
 *  3. Cargar UserDetails y poblar el SecurityContext si el token es válido
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Obtener el header de autorización
        final String authHeader = request.getHeader("Authorization");

        // Si no hay header o no empieza con "Bearer ", continuar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraer el token (quitar el prefijo "Bearer ")
        final String token = authHeader.substring(7);
        String username = null;

        try {
            username = jwtUtil.extractUsername(token);
        } catch (Exception e) {
            // Token malformado, expirado o con firma inválida → continuar sin autenticar
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Si hay username y el SecurityContext aún no tiene autenticación
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 4. Validar token: firma + expiración + username coinciden
            if (jwtUtil.validateToken(token, userDetails)) {
                // 5. Crear el objeto de autenticación y poblarlo en el SecurityContext
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,                          // sin credencial: ya autenticado via JWT
                                userDetails.getAuthorities()   // roles del usuario
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
