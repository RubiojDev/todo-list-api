package com.rubiojdev.todolist.tasks.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para crear una nueva Tarea.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateDto {

    @NotBlank(message = "Se necesita el nombre de la tarea")
    private String name;

}
