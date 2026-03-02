package com.rubiojdev.todolist.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JwtService.class)
@TestPropertySource(properties = {
        "jwt.secret=bXktc3VwZXItc2VjcmV0LWtleS1teS1zdXBlci1zZWNyZXQ="
})
class JwtServiceTest {

    @Autowired
    JwtService jwtService;

    @Value("${jwt.secret}")
    private String secret;

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Test
    void generateToken_validAuthentication_returnsValidToken() {
        //Arrange
        String username = "user@gmail.com";
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(username, null);

        //Act
        String token = jwtService.generateToken(authentication);

        //Assert
        assertNotNull(token);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("rubio.app", claims.getIssuer());
        assertEquals(username, claims.getSubject());
        assertTrue(claims.getExpiration().after(new Date()));
    }


    @Test
    void extractUsername_validToken_returnsUsername() {
        //Arrange
        String username = "user@gmail.com";

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(username, null);

        //Act
        String token = jwtService.generateToken(authentication);
        String extractedUsername = jwtService.extractUsername(token);

        //Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    void isTokenValid_validToken_returnsTrue() {
        //Arrange
        String username = "user@gmail.com";

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(username)
                        .password("1234")
                        .authorities("USER")
                        .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(username, null);

        //Act
        String token = jwtService.generateToken(authentication);

        //Assert
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void isTokenValid_usernameMismatch_returnsFalse() {
        //Arrange
        Authentication authentication =
                new UsernamePasswordAuthenticationToken("user@gmail.com", null);

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("other@gmail.com")
                        .password("1234")
                        .authorities("USER")
                        .build();

        //Act
        String token = jwtService.generateToken(authentication);

        //Assert
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertFalse(isValid);
    }

    @Test
    void isTokenValid_expiredToken_returnsFalse() {
        //Arrange
        String username = "user@gmail.com";

        Date now = new Date();
        Date expiredDate = new Date(now.getTime() - 1000 * 60 * 60); // ya expirado

        String expiredToken = Jwts.builder()
                .setIssuer("rubio.app")
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(username)
                        .password("1234")
                        .authorities("USER")
                        .build();

        //Act & Assert
        boolean isValid = jwtService.isTokenValid(expiredToken, userDetails);

        assertFalse(isValid);
    }
}