package com.rubiojdev.todolist.taskitems.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO utilizado para actualizar una subtarea existente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskItemUpdateDto {

    private String name;

    private Boolean completed;

}
