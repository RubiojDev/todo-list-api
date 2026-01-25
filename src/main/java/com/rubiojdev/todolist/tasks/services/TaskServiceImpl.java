package com.rubiojdev.todolist.tasks.services;

import com.rubiojdev.todolist.tasks.dtos.TaskCreateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskResponseDto;
import com.rubiojdev.todolist.tasks.dtos.TaskUpdateDto;
import com.rubiojdev.todolist.tasks.entities.Task;
import com.rubiojdev.todolist.tasks.mappers.TaskMapper;
import com.rubiojdev.todolist.tasks.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService{

    private final TaskRepository repository;
    private final UserRepository userRepository;
    private final TaskMapper mapper;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository,
                           TaskMapper taskMapper,
                           UserRepository userRepository) {
        this.mapper = taskMapper;
        this.repository = taskRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> getAllTasks(Long userId) {

        List<Task> tasks = repository.findAllByUserId(userId);
        List<TaskResponseDto> taskResponseDtos = new ArrayList<>();

        for (Task task : tasks) {
            taskResponseDtos.add(mapper.toResponseDto(task));
        }

        return taskResponseDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponseDto findTaskById(Long userId, Long id) {

        Task task = repository.findTaskWithItemsByIdAndUserId(userId, id)
                .orElseThrow(() -> new RuntimeException("id no encontrado"));

        return mapper.toResponseDto(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskResponseDto> findAllTaskByName(Long userId, String name) {

        List<Task> tasks = repository.findAllByNameAndUser(userId, name);
        List<TaskResponseDto> taskResponseDtos = new ArrayList<>();

        for (Task task : tasks) {
            taskResponseDtos.add(mapper.toResponseDto(task));
        }

        return taskResponseDtos;
    }

    @Override
    @Transactional
    public TaskResponseDto createNewTask(Long userId, TaskCreateDto taskDto) {

        if (repository.existsByNameIgnoreCaseAndUserId(taskDto.getName(), userId)) {
            throw new RuntimeException("Ese nombre ya existe");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no existe"));

        Task task = mapper.toEntity(taskDto);
        task.setUser(user);

        Task saved = repository.save(task);
        return mapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public TaskResponseDto updateTask(Long userId, Long id, TaskUpdateDto taskDto) {

        if (taskDto.getName() != null &&
                repository.existsByNameIgnoreCaseAndUserIdAndIdNot(
                        taskDto.getName(), userId, id
                )) {
            throw new RuntimeException("Ese nombre ya existe");
        }

        Task task = repository.findTaskWithItemsByIdAndUserId(userId, id)
                .orElseThrow(() -> new RuntimeException("Task no encontrada"));

        mapper.updateEntity(task, taskDto);

        return mapper.toResponseDto(task);
    }

    @Override
    @Transactional
    public void deleteTask(Long userId, Long id) {
        Task task = repository.findTaskWithItemsByIdAndUserId(userId, id)
                .orElseThrow(() -> new RuntimeException("Task no encontrada"));

        repository.delete(task);
    }
}
