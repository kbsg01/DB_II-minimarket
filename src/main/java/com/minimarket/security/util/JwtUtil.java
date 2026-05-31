package com.minimarket.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilidad para la generación y validación de JSON Web Tokens (JWT).
 * Utiliza el algoritmo HS256 con una clave secreta configurada externamente.
 */
@Component
public class JwtUtil {

    /** Clave secreta en Base64, inyectada desde application.properties */
    @Value("${jwt.secret}")
    private String secretBase64;

    /** Tiempo de vida del token en milisegundos, inyectado desde application.properties */
    @Value("${jwt.expiration}")
    private long expirationMs;

    /**
     * Deriva la SecretKey a partir del valor Base64 configurado.
     * JJWT exige mínimo 256 bits (32 bytes) para HS256.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secretBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un JWT firmado con HS256.
     *
     * @param userDetails detalles del usuario autenticado
     * @return token JWT compacto (header.payload.signature)
     */
    public String generateToken(UserDetails userDetails) {
        // Extraemos los roles para incluirlos como claim personalizado
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .subject(userDetails.getUsername())          // claim "sub"
                .claim("roles", roles)                       // claim personalizado con roles
                .issuedAt(new Date())                        // claim "iat"
                .expiration(new Date(System.currentTimeMillis() + expirationMs)) // claim "exp"
                .signWith(getSigningKey())                   // firma HS256
                .compact();
    }

    /**
     * Extrae todos los claims del token JWT.
     *
     * @param token JWT compacto
     * @return Claims del payload
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extrae el username (subject) del token.
     *
     * @param token JWT compacto
     * @return username del usuario
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extrae la fecha de expiración del token.
     *
     * @param token JWT compacto
     * @return fecha de expiración
     */
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /**
     * Verifica si el token ha expirado.
     *
     * @param token JWT compacto
     * @return true si el token está expirado
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Valida que el token pertenezca al usuario y no haya expirado.
     *
     * @param token       JWT compacto
     * @param userDetails detalles del usuario a comparar
     * @return true si el token es válido
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
