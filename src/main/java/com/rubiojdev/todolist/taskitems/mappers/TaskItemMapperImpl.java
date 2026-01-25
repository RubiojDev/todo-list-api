package com.rubiojdev.todolist.taskitems.mappers;

import com.rubiojdev.todolist.taskitems.dtos.TaskItemCreateDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemResponseDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemUpdateDto;
import com.rubiojdev.todolist.taskitems.entities.TaskItem;
import org.springframework.stereotype.Component;

@Component
public class TaskItemMapperImpl implements TaskItemMapper {
    @Override
    public TaskItem toEntity(TaskItemCreateDto dto) {
        if (dto == null) throw new RuntimeException("TaskItemCreateDto no puede ser NULL");

        TaskItem taskItem = new TaskItem();
        taskItem.setName(dto.getName().trim());

        return taskItem;
    }

    @Override
    public TaskItemResponseDto toResponseDto(TaskItem taskItem) {

        TaskItemResponseDto taskItemResponseDto = new TaskItemResponseDto();
        taskItemResponseDto.setId(taskItem.getId());
        taskItemResponseDto.setName(taskItem.getName());
        taskItemResponseDto.setCompleted(taskItem.isCompleted());

        return taskItemResponseDto;
    }

    @Override
    public void updateEntity(TaskItem taskItem, TaskItemUpdateDto dto) {

        if (dto.getName() != null && !dto.getName().isBlank()) taskItem.setName(dto.getName().trim());

        if (dto.getCompleted() != null) taskItem.setCompleted(dto.getCompleted());

    }
}
