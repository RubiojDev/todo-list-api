package com.rubiojdev.todolist.tasks.services;

import com.rubiojdev.todolist.shared.dto.PageResponse;
import com.rubiojdev.todolist.shared.exceptions.DuplicateResourceException;
import com.rubiojdev.todolist.tasks.dtos.TaskCreateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskResponseDto;
import com.rubiojdev.todolist.tasks.dtos.TaskUpdateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskWhitItemsResponseDto;
import com.rubiojdev.todolist.tasks.entities.Task;
import com.rubiojdev.todolist.tasks.mappers.TaskMapper;
import com.rubiojdev.todolist.tasks.repositories.TaskRepository;
import com.rubiojdev.todolist.users.entities.User;
import com.rubiojdev.todolist.users.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public PageResponse<TaskResponseDto> getAllTasks(User user, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Task> tasks = repository.findAllByUserOrderByUpdatedAtDesc(user, pageable);
        Page<TaskResponseDto> taskResponseDtos = tasks.map(mapper::toResponseDto);

        return PageResponse.toPage(taskResponseDtos);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskWhitItemsResponseDto findTaskById(User user, Long id) {

        Task task = repository.findTaskWithItemsByIdAndUser(user, id)
                .orElseThrow(() -> new EntityNotFoundException("Tarea no encontrada o no pertenece al usuario"));

        return mapper.toResponseDtoWhitItem(task);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskResponseDto> findAllTaskByName(User user, String name, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasks = repository.findAllByNameContainingIgnoreCaseAndUser(name, user, pageable);
        Page<TaskResponseDto> taskResponseDtos = tasks.map(mapper::toResponseDto);

        return PageResponse.toPage(taskResponseDtos);
    }

    @Override
    @Transactional
    public TaskResponseDto createNewTask(User user, TaskCreateDto taskDto) {

        if (repository.existsByNameIgnoreCaseAndUser(taskDto.getName(), user)) {
            throw new DuplicateResourceException("Ese nombre ya existe");
        }

//        User user = userRepository.findByUser(user)
//                .orElseThrow(() -> new EntityNotFoundException("Usuario no existe"));

        Task task = mapper.toEntity(taskDto);
        task.setUser(user);

        Task saved = repository.save(task);
        return mapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public TaskResponseDto updateTask(User user, Long id, TaskUpdateDto taskDto) {

        if (taskDto.getName() != null &&
                repository.existsByNameIgnoreCaseAndUserAndIdNot(
                        taskDto.getName(), user, id
                )) {
            throw new DuplicateResourceException("Ese nombre ya existe");
        }

        Task task = repository.findTaskWithItemsByIdAndUser(user, id)
                .orElseThrow(() -> new EntityNotFoundException("Task no encontrada o no pertenece al usuario"));

        mapper.updateEntity(task, taskDto);

        return mapper.toResponseDto(task);
    }

    @Override
    @Transactional
    public void deleteTask(User user, Long id) {
        Task task = repository.findTaskWithItemsByIdAndUser(user, id)
                .orElseThrow(() -> new EntityNotFoundException("Task no encontrada o no pertenece al usuario"));

        repository.delete(task);
    }
}
