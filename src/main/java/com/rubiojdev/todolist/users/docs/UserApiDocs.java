package com.rubiojdev.todolist.users.docs;

import com.rubiojdev.todolist.security.model.CustomUserDetails;
import com.rubiojdev.todolist.shared.dto.ErrorResponse;
import com.rubiojdev.todolist.users.dtos.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "User", description = "Endpoint que sirve para obtener y borrar el usuario actual")
public interface UserApiDocs {

    @Operation(
            summary = "Obtiene el usuario actual",
            description = "Devuelve el username, email y fecha de creacion del usuario que realiza la petición",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Token o credenciales invalidas",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<UserResponseDto> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails customUserDetails);

    @Operation(
            summary = "Borra el usuario actual",
            description = "Elimina el usuario autenticado que realiza la petición",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Usuario borrado con exito"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Token o credenciales invalidas",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Usuario no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<Void> deleteCurrentUser(
            @AuthenticationPrincipal CustomUserDetails customUserDetails);
}
