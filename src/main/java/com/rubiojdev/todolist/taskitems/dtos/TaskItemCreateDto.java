package com.rubiojdev.todolist.taskitems.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskItemCreateDto {

    @NotBlank(message = "El nombre de la subtarea es obligatorio")
    private String name;

}
