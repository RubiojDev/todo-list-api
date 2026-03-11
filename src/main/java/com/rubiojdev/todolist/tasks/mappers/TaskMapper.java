package com.rubiojdev.todolist.tasks.mappers;

import com.rubiojdev.todolist.tasks.dtos.TaskCreateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskResponseDto;
import com.rubiojdev.todolist.tasks.dtos.TaskUpdateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskWithItemsResponseDto;
import com.rubiojdev.todolist.tasks.entities.Task;

public interface TaskMapper {

     Task toEntity(TaskCreateDto dto);

     void updateEntity(Task task, TaskUpdateDto dto);

     TaskResponseDto toResponseDto(Task task);

    TaskWithItemsResponseDto toResponseDtoWhitItem(Task task);
}
