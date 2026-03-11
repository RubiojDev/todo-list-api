package com.rubiojdev.todolist.security.filter;

import com.rubiojdev.todolist.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación basado en JWT.
 *
 * <p>Este filtro intercepta cada petición HTTP entrante para verificar
 * si contiene un token JWT válido en el header {@code Authorization}.
 *
 * <p>El flujo de autenticación es el siguiente:
 * <ul>
 *     <li>Se obtiene el header {@code Authorization} de la petición.</li>
 *     <li>Se verifica que el header contenga un token con el prefijo {@code Bearer }.</li>
 *     <li>Se extrae el JWT y se obtiene el email del usuario desde el token.</li>
 *     <li>Si el usuario no está autenticado en él {@link org.springframework.security.core.context.SecurityContext},
 *     se cargan sus datos mediante {@link UserDetailsService}.</li>
 *     <li>Se valida el token utilizando {@link JwtService}.</li>
 *     <li>Si el token es válido, se crea un {@link UsernamePasswordAuthenticationToken}
 *     y se establece en el {@link SecurityContextHolder} para marcar la petición como autenticada.</li>
 * </ul>
 *
 * <p>Si el token no existe, es inválido o no tiene el formato esperado,
 * la petición continúa sin autenticación y será manejada posteriormente
 * por las reglas de seguridad configuradas en Spring Security.
 *
 * <p>Este filtro se ejecuta una sola vez por petición gracias a la
 * herencia de {@link OncePerRequestFilter}.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Procesa cada petición HTTP para verificar y validar el JWT
     * presente en el header Authorization.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(jwt);

        if (userEmail != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            if (jwtService.isTokenValid(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
