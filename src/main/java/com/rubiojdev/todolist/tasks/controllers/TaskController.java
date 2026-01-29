package com.rubiojdev.todolist.tasks.controllers;

import com.rubiojdev.todolist.shared.dto.PageResponse;
import com.rubiojdev.todolist.tasks.dtos.TaskCreateDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemCreateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskResponseDto;
import com.rubiojdev.todolist.tasks.dtos.TaskUpdateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskWhitItemsResponseDto;
import com.rubiojdev.todolist.tasks.services.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService service;

    @Autowired
    public TaskController(TaskService taskService) {
        this.service = taskService;
    }

    @GetMapping
    public ResponseEntity<PageResponse<TaskResponseDto>> getAllTasks(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) int size) {

        return ResponseEntity.ok(service.getAllTasks(1L, page, size));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<TaskWhitItemsResponseDto> findTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findTaskById(1L, id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<PageResponse<TaskResponseDto>> findAllTaskByName(
            @PathVariable String name,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) int size) {

        return ResponseEntity.ok(service.findAllTaskByName(1L, name, page, size));
    }

    @PostMapping
    public ResponseEntity<TaskResponseDto> createNewTask(@RequestBody @Valid TaskCreateDto taskDto){
        TaskResponseDto response = service.createNewTask(1L, taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long id,
            @RequestBody @Valid TaskUpdateDto taskDto) {

        TaskResponseDto response = service.updateTask(1L, id, taskDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        service.deleteTask(1L, id);
        return ResponseEntity.noContent().build();
    }

}
