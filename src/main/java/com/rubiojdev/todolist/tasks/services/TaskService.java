package com.rubiojdev.todolist.tasks.services;

import com.rubiojdev.todolist.shared.dto.PageResponse;
import com.rubiojdev.todolist.tasks.dtos.TaskCreateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskResponseDto;
import com.rubiojdev.todolist.tasks.dtos.TaskUpdateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskWhitItemsResponseDto;
import com.rubiojdev.todolist.users.entities.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TaskService {

    PageResponse<TaskResponseDto> getAllTasks(User user, int page, int size);

    TaskWhitItemsResponseDto findTaskById(User user, Long id);

    PageResponse<TaskResponseDto> findAllTaskByName(User user, String name, int page, int size);

    TaskResponseDto createNewTask(User user, TaskCreateDto taskDto);

    TaskResponseDto updateTask(User user, Long id, TaskUpdateDto taskDto);

    void deleteTask(User user, Long id);
}
