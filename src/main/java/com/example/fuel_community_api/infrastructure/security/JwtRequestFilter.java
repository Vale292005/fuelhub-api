package com.example.fuel_community_api.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        // 1. Validar si el header existe y comienza con el prefijo "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                email = jwtUtil.extractSubject(jwt);
            } catch (Exception e) {
                // Captura excepciones de tokens corruptos o expirados
                logger.error("No se pudo extraer el correo del token JWT", e);
            }
        }

        // 2. Verificar el token y establecer la sesión en el contexto de seguridad de Spring
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            if (jwtUtil.isTokenExpired(jwt)) {
                // Creamos el token de autenticación para Spring Security
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        email, null, new ArrayList<>() // Lista de authorities/roles vacía por ahora
                );
                
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Autenticamos formalmente al usuario en el contexto actual de la petición
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // 3. Permitir que la petición continúe su viaje hacia el controlador correspondiente
        filterChain.doFilter(request, response);
    }
}