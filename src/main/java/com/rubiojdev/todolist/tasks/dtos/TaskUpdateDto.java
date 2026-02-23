package com.rubiojdev.todolist.tasks.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateDto {

    private String name;

    private Boolean completed;
}
