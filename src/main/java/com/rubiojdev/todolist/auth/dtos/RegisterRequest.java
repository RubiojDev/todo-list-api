package com.rubiojdev.todolist.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO utilizado para el registro de nuevos usuarios en el sistema.
 * <p>
 * Contiene los datos necesarios para crear una nueva cuenta y permitir
 * que el usuario pueda autenticarse posteriormente en la aplicación.
 * </p>
 * <ul>
 *     <li>{@code username}: Nombre de usuario utilizado para identificar la cuenta.</li>
 *     <li>{@code email}: Dirección de correo utilizada para identificar al usuario en el sistema.</li>
 *     <li>{@code password}: Contraseña utilizada posteriormente para el inicio de sesión.</li>
 * </ul>
 */
@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "El username es obligatorio")
    private String username;

    @Email(message = "Email inválido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
