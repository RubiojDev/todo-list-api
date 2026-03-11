package com.rubiojdev.todolist.taskitems.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para devolver información de una subtarea.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskItemResponseDto {

    private Long id;

    private String name;

    private boolean completed;
}
