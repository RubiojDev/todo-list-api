package com.rubiojdev.todolist.taskitems.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskItemUpdateDto {

    private String name;

    private Boolean completed;

}
