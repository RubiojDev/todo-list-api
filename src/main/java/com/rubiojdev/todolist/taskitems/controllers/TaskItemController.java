package com.rubiojdev.todolist.taskitems.controllers;

import com.rubiojdev.todolist.taskitems.dtos.TaskItemCreateDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemResponseDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemUpdateDto;
import com.rubiojdev.todolist.taskitems.services.TaskItemService;
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
public class TaskItemController {
    private final Long userId = 1L; //SOLO PARA PROBAR. SERA BORRADO LUEGO
    private final TaskItemService service;

    @Autowired
    public TaskItemController(TaskItemService taskItemService) {
        this.service = taskItemService;
    }

    @GetMapping("/task/{taskId}/items")
    public ResponseEntity <List<TaskItemResponseDto>> getItemsByTask(@PathVariable @NotNull Long taskId) {

        return ResponseEntity.ok(service.getItemsByTask(userId, taskId));
    }

    @PostMapping("/task/{taskId}/items")
    public ResponseEntity<TaskItemResponseDto> createNewTaskItem(
            @PathVariable @NotNull Long taskId,
            @RequestBody @Valid TaskItemCreateDto taskItemCreateDto) {

        TaskItemResponseDto response = service.createNewTaskItem(userId, taskId, taskItemCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/task/{taskId}/items/{id}")
    public ResponseEntity<TaskItemResponseDto> updateTaskItem(
            @PathVariable @NotNull Long id,
            @PathVariable @NotNull Long taskId,
            @RequestBody @Valid TaskItemUpdateDto taskItemUpdateDto) {

        TaskItemResponseDto response = service.updateTaskItem(userId, taskId, id, taskItemUpdateDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/task/{taskId}/items/{id}")
    public ResponseEntity<Void> deleteTaskItem(
            @PathVariable @NotNull Long id,
            @PathVariable @NotNull Long taskId) {

        service.deleteTaskItem(userId, taskId, id);
        return ResponseEntity.noContent().build();
    }

}
