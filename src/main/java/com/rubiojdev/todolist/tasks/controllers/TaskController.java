package com.rubiojdev.todolist.tasks.controllers;

import com.rubiojdev.todolist.security.model.CustomUserDetails;
import com.rubiojdev.todolist.shared.dto.PageResponse;
import com.rubiojdev.todolist.tasks.dtos.TaskCreateDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemCreateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskResponseDto;
import com.rubiojdev.todolist.tasks.dtos.TaskUpdateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskWhitItemsResponseDto;
import com.rubiojdev.todolist.tasks.services.TaskService;
import com.rubiojdev.todolist.users.entities.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) int size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
        return ResponseEntity.ok(service.getAllTasks(user, page, size));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<TaskWhitItemsResponseDto> findTaskById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
        return ResponseEntity.ok(service.findTaskById(user, id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<PageResponse<TaskResponseDto>> findAllTaskByName(
            @PathVariable String name,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) int size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
        return ResponseEntity.ok(service.findAllTaskByName(user, name, page, size));
    }

    @PostMapping
    public ResponseEntity<TaskResponseDto> createNewTask(
            @RequestBody @Valid TaskCreateDto taskDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails){

        User user = customUserDetails.getUser();
        TaskResponseDto response = service.createNewTask(user, taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long id,
            @RequestBody @Valid TaskUpdateDto taskDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
        TaskResponseDto response = service.updateTask(user, id, taskDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails){

        User user = customUserDetails.getUser();
        service.deleteTask(user, id);
        return ResponseEntity.noContent().build();
    }

}
