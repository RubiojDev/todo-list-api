package com.rubiojdev.todolist.auth.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO utilizado para enviar un refresh token al servidor.
 * <p>
 * Este objeto es utilizado por el cliente cuando necesita renovar
 * su token de acceso (JWT) sin volver a autenticarse con sus credenciales.
 * </p>
 * <ul>
 *     <li>{@code refreshToken}: Código del token utilizado para solicitar
 *     nuevos tokens de acceso cuando el JWT expira.</li>
 * </ul>
 */
@Getter
@Setter
public class RefreshTokenRequest {

    @NotBlank(message = "El token no puede estar vacio")
    String refreshToken;
}
