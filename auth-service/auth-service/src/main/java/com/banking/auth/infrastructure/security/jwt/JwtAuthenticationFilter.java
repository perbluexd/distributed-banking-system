package com.banking.auth.infrastructure.security.jwt;

import com.banking.auth.application.port.out.JwtTokenVerifierPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
     /*
    Es un filtro personalizado que se ejecuta una vez por request, extrae el Bearer token del header
    Authorization, lo valida, construye un Authentication, lo guarda en el SecurityContext, y
    luego permite que la request continúe por la cadena de filtros mediante
    filterChain.doFilter(request, response).
     */

    private final JwtTokenVerifierPort verifier;

    public JwtAuthenticationFilter(JwtTokenVerifierPort verifier) {
        this.verifier = verifier;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring("Bearer ".length()).trim();

            // valida access token y extrae userId
            UUID userId = verifier.verifyAccessTokenAndGetUserId(token);

            // auth mínima: principal = userId, en la parte de credentials suele ir la contraseña pero no la tenemos aquí, y authorities = vacía porque no manejamos roles/permissions en este ejemplo
            var auth = new UsernamePasswordAuthenticationToken(userId.toString(), null, List.of());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
