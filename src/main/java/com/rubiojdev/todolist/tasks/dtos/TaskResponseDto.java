package com.rubiojdev.todolist.tasks.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO utilizado para devolver información de una Tarea.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {

    private Long id;

    private String name;

    private LocalDateTime updatedAt;

    private boolean completed;
}
