package com.rubiojdev.todolist.tasks.docs;

import com.rubiojdev.todolist.security.model.CustomUserDetails;
import com.rubiojdev.todolist.shared.dto.ErrorResponse;
import com.rubiojdev.todolist.shared.dto.PageResponse;
import com.rubiojdev.todolist.tasks.dtos.TaskCreateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskResponseDto;
import com.rubiojdev.todolist.tasks.dtos.TaskUpdateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskWithItemsResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Tasks", description = "Enpoint para la gestión de tareas del usuario autenticado")
public interface TaskApiDocs {

    @Operation(
            summary = "Obtener todas las tareas",
            description = "Permite obtener todas las tareas paginadas para el actual usuario",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Solicitud realiza con exito",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PageResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parametros invalidos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciales invalidas",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<PageResponse<TaskResponseDto>> getAllTasks(
            @Parameter(name = "page", description = "Número de página (empieza en 0)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(name = "size", description = "Cantidad de elementos por página (entre 1 y 20)", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) int size,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    );

    @Operation(
            summary = "Obtener una tarea",
            description = "Permite obtener una tarea por el ID para el actual usuario",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Solicitud realiza con exito",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskWithItemsResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Formato del ID incorrecto",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciales invalidas",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Tarea no encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<TaskWithItemsResponseDto> findTaskById(
            @Parameter(description = "ID de la Tarea a buscar", example = "1")
            @PathVariable Long id,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    );

    @Operation(
            summary = "Obtener todas las tareas por el nombre",
            description = "Permite obtener todas las tareas paginadas que coincidan con el nombre, esto para el actual usuario",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Solicitud realiza con exito",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PageResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parametros invalidos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciales invalidas",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<PageResponse<TaskResponseDto>> findAllTaskByName(
            @Parameter(description = "Nombre de la tarea a buscar", example = "Compras")
            @RequestParam String name,

            @Parameter(name = "page", description = "Número de página (empieza en 0)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(name = "size", description = "Cantidad de elementos por página (entre 1 y 20)", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) int size,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    );

    @Operation(
            summary = "Crea nuevas tareas",
            description = "Permite crear nuevas tareas",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nombre que tendra la tarea",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskCreateDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Tarea creada con exito",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parametros invalidos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciales invalidas",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "La tarea ya existe",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<TaskResponseDto> createNewTask(
            @RequestBody @Valid TaskCreateDto taskDto,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    );

    @Operation(
            summary = "Actualizar tarea",
            description = "Permite actualizar la tarea especificada por el ID, para el actual usuario",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Se puede actualizar el nombre y el estatus de la tarea",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskUpdateDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Tarea actualizada con exito",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskResponseDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parametros invalidos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciales invalidas",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Tarea no encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<TaskResponseDto> updateTask(
            @Parameter(description = "ID de la tarea a actualizar")
            @PathVariable Long id,

            @RequestBody @Valid TaskUpdateDto taskDto,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    );

    @Operation(
            summary = "Borrar tarea y subtareas",
            description = "Permite borrar tanto la tarea como las subtareas relacionadas a traves del ID, para el actual usuario",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Tarea borrada con exito"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Parametros invalidos",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Credenciales invalidas",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Tarea no encontrada",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<Void> deleteTask(
            @Parameter(description = "ID de la tarea a borrar")
            @PathVariable Long id,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    );
}
