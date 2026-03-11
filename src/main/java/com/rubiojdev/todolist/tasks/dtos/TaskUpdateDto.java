package com.rubiojdev.todolist.tasks.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para actualizar una tarea existente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateDto {

    private String name;

    private Boolean completed;
}
