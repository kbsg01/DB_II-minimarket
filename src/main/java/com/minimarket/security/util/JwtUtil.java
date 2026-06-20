package com.minimarket.security.util;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    public String extractUsername(String token) {
        return null;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        return false;
    }
}
