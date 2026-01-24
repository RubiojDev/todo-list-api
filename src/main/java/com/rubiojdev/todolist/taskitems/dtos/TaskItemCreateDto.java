package com.rubiojdev.todolist.taskitems.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskItemCreateDto {

    @NotEmpty(message = "Descripcion de la tarea necesaria")
    private String description;

}
