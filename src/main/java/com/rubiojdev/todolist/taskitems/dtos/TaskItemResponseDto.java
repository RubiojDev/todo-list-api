package com.rubiojdev.todolist.taskitems.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskItemResponseDto {

    private Long id;

    private String name;

    private boolean completed;
}
