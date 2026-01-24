package com.rubiojdev.todolist.tasks.controllers;

import com.rubiojdev.todolist.tasks.dtos.TaskCreateDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemCreateDto;
import com.rubiojdev.todolist.tasks.dtos.TaskResponseDto;
import com.rubiojdev.todolist.tasks.dtos.TaskUpdateDto;
import com.rubiojdev.todolist.tasks.services.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
        return ResponseEntity.ok(service.getAllTasks(1L));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<TaskResponseDto> findTaskById(@PathVariable @NotNull Long id) {
        return ResponseEntity.ok(service.findTaskById(1L, id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<TaskResponseDto>> findAllTaskByName(@PathVariable @NotNull String name) {
        return ResponseEntity.ok(service.findAllTaskByName(1L, name));
    }

    @PostMapping
    public ResponseEntity<TaskResponseDto> createNewTask(@RequestBody @Valid TaskCreateDto taskDto){
        TaskResponseDto response = service.createNewTask(1L, taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /*@PostMapping("/{taskId}/items")
    public ResponseEntity<TaskResponseDto> createNewSubTask(
            @PathVariable @NotNull Long taskId,
            @RequestBody @Valid TaskItemCreateDto taskItemDto) {

        TaskResponseDto response = service.createNewSubTask(taskId, taskItemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }*/

    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable @NotNull Long id,
            @RequestBody @Valid TaskUpdateDto taskDto) {

        TaskResponseDto response = service.updateTask(1L, id, taskDto);
        return ResponseEntity.ok(response);
    }

    /*@PatchMapping("/items/{id}")
    public ResponseEntity<TaskResponseDto> updateSubTask(
            @PathVariable @NotNull Long id,
            @RequestBody @Valid TaskItemUpdateDto taskItemDto) {

        TaskResponseDto response = service.update(id, taskItemDto);
        return ResponseEntity.ok(response);
    }*/

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable @NotNull Long id){
        service.deleteTask(1L, id);
        return ResponseEntity.noContent().build();
    }

    /*@DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteSubTask(@PathVariable @NotNull Long id){
        service.deleteSubTask(id);
        return ResponseEntity.noContent().build();
    }*/
}
