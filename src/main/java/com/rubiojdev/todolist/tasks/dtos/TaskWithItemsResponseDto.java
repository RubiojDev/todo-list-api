package com.rubiojdev.todolist.tasks.dtos;

import com.rubiojdev.todolist.taskitems.dtos.TaskItemResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * DTO utilizado para devolver información de una Tarea y las subtareas asociadas a ella.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskWithItemsResponseDto {

    private Long id;

    private String name;

    private Instant updatedAt;

    private boolean completed;

    private List<TaskItemResponseDto> taskItemDtoList;
}
