package com.rubiojdev.todolist.taskitems.services;

import com.rubiojdev.todolist.shared.dto.PageResponse;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemCreateDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemResponseDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemUpdateDto;

import java.util.List;

public interface TaskItemService {

    PageResponse<TaskItemResponseDto> getItemsByTask(Long userId, Long taskId, int page, int size);

    TaskItemResponseDto createNewTaskItem(Long userId, Long taskId,
                                                  TaskItemCreateDto taskItemCreateDto);

    TaskItemResponseDto updateTaskItem(Long userId, Long taskId, Long id,
                                       TaskItemUpdateDto taskItemUpdateDto);

    void deleteTaskItem(Long userId, Long taskId, Long id);
}
