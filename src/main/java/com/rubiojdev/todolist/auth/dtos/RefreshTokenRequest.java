package com.rubiojdev.todolist.auth.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshTokenRequest {

    @NotBlank(message = "El token no puede estar vacio")
    String refreshToken;
}
