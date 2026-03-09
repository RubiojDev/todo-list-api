package com.rubiojdev.todolist.taskitems.controllers;

import com.rubiojdev.todolist.security.model.CustomUserDetails;
import com.rubiojdev.todolist.shared.dto.PageResponse;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemCreateDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemResponseDto;
import com.rubiojdev.todolist.taskitems.dtos.TaskItemUpdateDto;
import com.rubiojdev.todolist.taskitems.services.TaskItemService;
import com.rubiojdev.todolist.users.entities.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
public class TaskItemController {

    private final TaskItemService service;

    @Autowired
    public TaskItemController(TaskItemService taskItemService) {
        this.service = taskItemService;
    }

    @GetMapping("/tasks/{taskId}/items")
    public ResponseEntity <PageResponse<TaskItemResponseDto>> getItemsByTask(
            @PathVariable Long taskId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(20) int size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
        return ResponseEntity.ok(service.getItemsByTask(user, taskId, page, size));
    }

    @PostMapping("/tasks/{taskId}/items")
    public ResponseEntity<TaskItemResponseDto> createNewTaskItem(
            @PathVariable Long taskId,
            @RequestBody @Valid TaskItemCreateDto taskItemCreateDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
        TaskItemResponseDto response = service.createNewTaskItem(user, taskId, taskItemCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/tasks/{taskId}/items/{id}")
    public ResponseEntity<TaskItemResponseDto> updateTaskItem(
            @PathVariable Long taskId,
            @PathVariable Long id,
            @RequestBody @Valid TaskItemUpdateDto taskItemUpdateDto,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
        TaskItemResponseDto response = service.updateTaskItem(user, taskId, id, taskItemUpdateDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/tasks/{taskId}/items/{id}")
    public ResponseEntity<Void> deleteTaskItem(
            @PathVariable Long taskId,
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
        service.deleteTaskItem(user, taskId, id);
        return ResponseEntity.noContent().build();
    }

}
