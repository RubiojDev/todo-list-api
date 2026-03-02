package com.rubiojdev.todolist.security.filter;

import com.rubiojdev.todolist.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @InjectMocks
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_noAuthHeader_shouldContinueFilterChain() throws Exception {
        //Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        //Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        //Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService);
    }

    @Test
    void doFilterInternal_validToken_shouldSetAuthentication() throws Exception {
        //Arrange
        String jwt = "valid.token.here";
        String email = "user@gmail.com";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + jwt);

        when(jwtService.extractUsername(jwt))
                .thenReturn(email);

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(email)
                        .password("1234")
                        .authorities("USER")
                        .build();

        when(userDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid(jwt, userDetails))
                .thenReturn(true);

        //Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        //Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_usernameNull_shouldNotAuthenticate() throws Exception {
        //Arrange
        String email = "user@gmail.com";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer token");

        when(jwtService.extractUsername("token"))
                .thenReturn(email);

        //Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        //Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_alreadyAuthenticated_shouldNotOverwriteAuthentication() throws Exception {
        //Arrange
        String jwt = "valid.token";
        String email = "user@gmail.com";

        // Simulamos que ya hay autenticación en el contexto
        UsernamePasswordAuthenticationToken existingAuth =
                new UsernamePasswordAuthenticationToken(
                        "existingUser",
                        null,
                        List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(existingAuth);

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + jwt);

        when(jwtService.extractUsername(jwt))
                .thenReturn(email);

        //Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        //Assert
        // Debe mantenerse la autenticación original
        assertEquals(
                existingAuth,
                SecurityContextHolder.getContext().getAuthentication()
        );

        // No debe intentar cargar usuario
        verifyNoInteractions(userDetailsService);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_invalidToken_shouldNotAuthenticate() throws Exception {
        //Arrange
        String jwt = "invalid.token";
        String email = "user@gmail.com";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + jwt);

        when(jwtService.extractUsername(jwt))
                .thenReturn(email);

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(email)
                        .password("1234")
                        .authorities("USER")
                        .build();

        when(userDetailsService.loadUserByUsername(email))
                .thenReturn(userDetails);

        when(jwtService.isTokenValid(jwt, userDetails))
                .thenReturn(false);

        //Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        //Assert
        //No debe autenticarse
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        //Siempre debe continuar la cadena
        verify(filterChain).doFilter(request, response);
    }
}