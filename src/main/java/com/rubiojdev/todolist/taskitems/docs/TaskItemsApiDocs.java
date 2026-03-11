package com.rubiojdev.todolist.taskitems.docs;

import com.rubiojdev.todolist.security.model.CustomUserDetails;
import com.rubiojdev.todolist.shared.dto.ErrorResponse;
import com.rubiojdev.todolist.shared.dto.PageResponse;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemCreateDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemResponseDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemUpdateDto;
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

@Tag(name = "TaskItems", description = "Endpoint para la gestión de los items pertenecientes a las tareas del usuario autenticado")
public interface TaskItemsApiDocs {

    @Operation(
            summary = "Obtener los items de una tarea",
            description = "Permite obtener los items paginados perteneciente a una tarea del usuario autenticado",
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
                    )
            }
    )
    ResponseEntity<PageResponse<TaskItemResponseDto>> getItemsByTask(
            @Parameter(description = "ID de la tarea a la que pertenece el item", example = "15")
            @PathVariable Long taskId,

            @Parameter(name = "page", description = "Número de página (empieza en 0)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(name = "size", description = "Cantidad de elementos por página (entre 1 y 20)", example = "10")
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) int size,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    );

    @Operation(
            summary = "Crea nuevos items",
            description = "Permite crear nuevos items relacionados con la tarea para el usuario autenticado",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nombre que tendra el item",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskItemCreateDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Item creado con exito",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskItemResponseDto.class)
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
                            description = "La tarea relacionada no existe",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<TaskItemResponseDto> createNewTaskItem(
            @Parameter(description = "ID de la tarea a la que pertenece el item", example = "4")
            @PathVariable Long taskId,

            @RequestBody @Valid TaskItemCreateDto taskItemCreateDto,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    );

    @Operation(
            summary = "Actualizar un item",
            description = "Permite actualizar un item relacionado con la tarea perteneciente al usuario logeado",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Se puede actualizar el nombre y el estatus de del item",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskItemUpdateDto.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Item actualizado con exito",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskItemResponseDto.class)
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
                            description = "Tarea o item no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<TaskItemResponseDto> updateTaskItem(
            @Parameter(description = "ID de la tarea", example = "10")
            @PathVariable Long taskId,

            @Parameter(description = "ID del item", example = "5")
            @PathVariable Long id,

            @RequestBody @Valid TaskItemUpdateDto taskItemUpdateDto,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    );

    @Operation(
            summary = "Borrar un item",
            description = "Permite borrar un item perteneciente a la tarea, para el usuario logeado",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Item borrado con exito"
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
                            description = "Item no encontrado",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorResponse.class)
                            )
                    )
            }
    )
    ResponseEntity<Void> deleteTaskItem(
            @Parameter(description = "ID de la Tarea", example = "10")
            @PathVariable Long taskId,

            @Parameter(description = "ID del item", example = "1")
            @PathVariable Long id,

            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    );
}