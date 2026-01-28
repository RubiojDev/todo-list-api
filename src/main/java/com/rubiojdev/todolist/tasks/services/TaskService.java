package com.rubiojdev.todolist.tasks.services;

import com.rubiojdev.todolist.tasks.dtos.TaskCreateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskResponseDto;
import com.rubiojdev.todolist.tasks.dtos.TaskUpdateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskWhitItemsResponseDto;

import java.util.List;

public interface TaskService {

    List<TaskResponseDto> getAllTasks(Long userId);

    TaskWhitItemsResponseDto findTaskById(Long userId, Long id);

    List<TaskResponseDto> findAllTaskByName(Long userId, String name);

    TaskResponseDto createNewTask(Long userId, TaskCreateDto taskDto);

    TaskResponseDto updateTask(Long userId, Long id, TaskUpdateDto taskDto);

    void deleteTask(Long userId, Long id);
}
