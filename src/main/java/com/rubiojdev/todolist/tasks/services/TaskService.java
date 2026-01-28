package com.rubiojdev.todolist.tasks.services;

import com.rubiojdev.todolist.shared.dto.PageResponse;
import com.rubiojdev.todolist.tasks.dtos.TaskCreateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskResponseDto;
import com.rubiojdev.todolist.tasks.dtos.TaskUpdateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskWhitItemsResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TaskService {

    PageResponse<TaskResponseDto> getAllTasks(Long userId, int page, int size);

    TaskWhitItemsResponseDto findTaskById(Long userId, Long id);

    PageResponse<TaskResponseDto> findAllTaskByName(Long userId, String name, int page, int size);

    TaskResponseDto createNewTask(Long userId, TaskCreateDto taskDto);

    TaskResponseDto updateTask(Long userId, Long id, TaskUpdateDto taskDto);

    void deleteTask(Long userId, Long id);
}
