package com.rubiojdev.todolist.taskitems.mappers;

import com.rubiojdev.todolist.taskitems.dtos.TaskItemCreateDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemResponseDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemUpdateDto;
import com.rubiojdev.todolist.taskitems.entities.TaskItem;

public interface TaskItemMapper {

    TaskItem toEntity(TaskItemCreateDto dto);

    TaskItemResponseDto toResponseDto(TaskItem taskItem);

    void updateEntity(TaskItem taskItem, TaskItemUpdateDto dto);

}
