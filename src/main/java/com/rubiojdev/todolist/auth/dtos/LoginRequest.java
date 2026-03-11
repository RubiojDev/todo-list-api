package com.rubiojdev.todolist.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO utilizado para iniciar sesión en el sistema.
 * <p>
 * Contiene los datos que el cliente debe enviar para autenticarse.
 * </p>
 * <ul>
 *     <li>{@code email}: Dirección de correo utilizada como identificador del usuario.</li>
 *     <li>{@code password}: Contraseña secreta asociada a la cuenta del usuario.</li>
 * </ul>
 */
@Getter
@Setter
public class LoginRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
