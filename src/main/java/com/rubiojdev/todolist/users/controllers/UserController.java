package com.rubiojdev.todolist.users.controllers;

import com.rubiojdev.todolist.security.model.CustomUserDetails;
import com.rubiojdev.todolist.users.dtos.UserCreateDto;
import com.rubiojdev.todolist.users.dtos.UserResponseDto;
import com.rubiojdev.todolist.users.entities.User;
import com.rubiojdev.todolist.users.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
        UserResponseDto response = service.getCurrentUser(user);
        return ResponseEntity.ok(response);
    }

    /*@PostMapping
    public ResponseEntity<UserResponseDto> createNewUser(@RequestBody @Valid UserCreateDto dto) {

        UserResponseDto response = service.createNewUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }*/

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        User user = customUserDetails.getUser();
        service.deleteCurrentUser(user);
        return ResponseEntity.noContent().build();
    }
}