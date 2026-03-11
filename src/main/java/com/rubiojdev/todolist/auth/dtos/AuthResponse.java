package com.rubiojdev.todolist.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * DTO utilizado para retornar los tokens generados tras una autenticación exitosa.
 * <p>
 * Contiene los tokens necesarios para que el cliente pueda acceder a recursos
 * protegidos y renovar su sesión sin volver a introducir credenciales.
 * </p>
 * <ul>
 *     <li>{@code token}: Token de acceso (JWT) utilizado para autenticar solicitudes.</li>
 *     <li>{@code refreshToken}: Token utilizado para solicitar nuevos tokens de acceso
 *     cuando el JWT expira.</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String refreshToken;
}
