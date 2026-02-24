package com.rubiojdev.todolist.taskitems.services;

import com.rubiojdev.todolist.shared.dto.PageResponse;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemCreateDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemResponseDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemUpdateDto;
import com.rubiojdev.todolist.users.entities.User;

import java.util.List;

public interface TaskItemService {

    PageResponse<TaskItemResponseDto> getItemsByTask(User user, Long taskId, int page, int size);

    TaskItemResponseDto createNewTaskItem(User user, Long taskId,
                                                  TaskItemCreateDto taskItemCreateDto);

    TaskItemResponseDto updateTaskItem(User user, Long taskId, Long id,
                                       TaskItemUpdateDto taskItemUpdateDto);

    void deleteTaskItem(User user, Long taskId, Long id);
}
