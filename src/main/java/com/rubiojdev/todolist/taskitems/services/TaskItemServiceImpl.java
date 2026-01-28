package com.rubiojdev.todolist.taskitems.services;

import com.rubiojdev.todolist.shared.dto.PageResponse;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemCreateDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemResponseDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemUpdateDto;
import com.rubiojdev.todolist.taskitems.entities.TaskItem;
import com.rubiojdev.todolist.taskitems.mappers.TaskItemMapper;
import com.rubiojdev.todolist.taskitems.repositories.TaskItemRepository;
import com.rubiojdev.todolist.tasks.entities.Task;
import com.rubiojdev.todolist.tasks.repositories.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskItemServiceImpl implements TaskItemService{

    private final TaskItemMapper mapper;
    private final TaskRepository taskRepository;
    private final TaskItemRepository repository;

    @Autowired
    public TaskItemServiceImpl(TaskItemMapper mapper,
                               TaskRepository taskRepository,
                               TaskItemRepository repository) {

        this.mapper = mapper;
        this.repository = repository;
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TaskItemResponseDto> getItemsByTask(Long userId, Long taskId, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<TaskItem> taskItems = repository.findByTaskIdAndTaskUserIdOrderByIdAsc(taskId, userId, pageable);
        Page<TaskItemResponseDto> responseDtoList = taskItems.map(mapper::toResponseDto);

        return PageResponse.toPage(responseDtoList);
    }

    @Override
    @Transactional
    public TaskItemResponseDto createNewTaskItem(Long userId, Long taskId, TaskItemCreateDto taskItemCreateDto) {

        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() ->
                        new EntityNotFoundException("La Tarea no existe o no pertenece al usuario")
                );

        TaskItem taskItem = mapper.toEntity(taskItemCreateDto);
        taskItem.setTask(task);

        TaskItem saved = repository.save(taskItem);
        return mapper.toResponseDto(saved);
    }

    @Override
    @Transactional
    public TaskItemResponseDto updateTaskItem(Long userId, Long taskId, Long id,
                                              TaskItemUpdateDto taskItemUpdateDto) {

        TaskItem taskItem =repository.findTaskItemByIdAndTaskIdAndTaskUserId(id, taskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Subtarea no encontrada o no pertenece al usuario"));

        mapper.updateEntity(taskItem, taskItemUpdateDto);

        return mapper.toResponseDto(taskItem);
    }

    @Override
    @Transactional
    public void deleteTaskItem(Long userId, Long taskId, Long id) {

        TaskItem taskItem = repository.findTaskItemByIdAndTaskIdAndTaskUserId(id, taskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Subtarea no encontrada o no pertenece al usuario"));

        repository.delete(taskItem);
    }
}
