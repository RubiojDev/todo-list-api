package com.rubiojdev.todolist.tasks.mappers;

import com.rubiojdev.todolist.taskitems.dtos.TaskItemResponseDto;
import com.rubiojdev.todolist.taskitems.entities.TaskItem;
import com.rubiojdev.todolist.tasks.dtos.TaskCreateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskResponseDto;
import com.rubiojdev.todolist.tasks.dtos.TaskUpdateDto;
import com.rubiojdev.todolist.tasks.entities.Task;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaskMapperImpl implements TaskMapper{

    @Override
    public Task toEntity(TaskCreateDto dto) {

        if (dto == null) throw new RuntimeException("Mensaje"); //crear personalizada

        Task task = new Task();
        task.setName(dto.getName().trim());

        return task;
    }

    @Override
    public void updateEntity(Task task, TaskUpdateDto dto) {

        if (dto.getName() != null && !dto.getName().isBlank()) task.setName(dto.getName().trim());

        if (dto.getCompleted() != null) task.setCompleted(dto.getCompleted());
    }

    @Override
    public TaskResponseDto toResponseDto(Task task) {

        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(task.getId());
        dto.setName(task.getName());
        dto.setCompleted(task.isCompleted());
        dto.setUpdatedAt(task.getUpdatedAt());

        List<TaskItem> taskItems = task.getTaskItems();
        List<TaskItemResponseDto> itemResponseDtoList = new ArrayList<>();

        if (taskItems != null) {
            for (TaskItem taskItem : taskItems) {
                itemResponseDtoList.add(toTaskItemDto(taskItem));
            }
        }

        dto.setTaskItemDtoList(itemResponseDtoList);

        return dto;
    }

    private TaskItemResponseDto toTaskItemDto(TaskItem taskItem) {

        TaskItemResponseDto taskItemResponseDto = new TaskItemResponseDto();
        taskItemResponseDto.setId(taskItem.getId());
        taskItemResponseDto.setName(taskItem.getName());
        taskItemResponseDto.setCompleted(taskItem.isCompleted());

        return taskItemResponseDto;
    }
}
