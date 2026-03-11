package com.rubiojdev.todolist.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

/**
 * Servicio encargado de la generación, validación y extracción de información
 * de los JSON Web Tokens (JWT) utilizados en la autenticación del sistema.
 *
 * <p>Este servicio utiliza la librería JJWT para crear y verificar tokens
 * firmados mediante el algoritmo {@code HS256}. Los tokens generados contienen
 * información básica del usuario autenticado, como su identificador
 * (username/email), junto con metadatos como el emisor, fecha de emisión
 * y fecha de expiración.</p>
 *
 * <p>Responsabilidades principales:</p>
 * <ul>
 *     <li>Generar tokens JWT para usuarios autenticados.</li>
 *     <li>Extraer información (claims) almacenada dentro del token.</li>
 *     <li>Validar la integridad y expiración de los tokens.</li>
 * </ul>
 *
 * <p>La clave secreta utilizada para firmar los tokens se obtiene desde la
 * configuración de la aplicación mediante la propiedad {@code jwt.secret}.</p>
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    // Tiempo de expiración del token (15 minutos) (1segundo * 1min * 15 min = 15min)
    private static final long EXPIRATION = 1000 * 60 * 15;

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un token JWT a partir de un objeto {@link Authentication}.
     *
     * <p>Se utiliza principalmente después de que un usuario se autentica
     * correctamente en el sistema.</p>
     *
     * @param authentication objeto de autenticación que contiene los datos del usuario
     * @return token JWT firmado
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return buildToken(userDetails);
    }

    /**
     * Genera un token JWT utilizando directamente los datos del usuario.
     *
     * @param userDetails información del usuario autenticado
     * @return token JWT firmado
     */
    public String generateToken(UserDetails userDetails) {
        return buildToken(userDetails);
    }

    private String buildToken(UserDetails userDetails) {

        return Jwts.builder()
                .setIssuer("rubio.app")
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrae el username (subject) almacenado dentro del token JWT.
     *
     * @param token token JWT
     * @return username contenido en el token
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Verifica si un token JWT es válido para un usuario determinado.
     *
     * <p>La validación incluye:
     * <ul>
     *     <li>Comprobar que el username del token coincida con el usuario.</li>
     *     <li>Verificar que el token no haya expirado.</li>
     * </ul>
     *
     * @param token token JWT
     * @param userDetails datos del usuario
     * @return {@code true} si el token es válido, {@code false} en caso contrario
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername())
                    && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token)
                .getExpiration()
                .before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
