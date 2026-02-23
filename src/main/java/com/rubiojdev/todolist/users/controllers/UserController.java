package com.rubiojdev.todolist.users.controllers;

import com.rubiojdev.todolist.users.dtos.UserCreateDto;
import com.rubiojdev.todolist.users.dtos.UserResponseDto;
import com.rubiojdev.todolist.users.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser() {

        UserResponseDto response = service.getCurrentUser();
        return ResponseEntity.ok(response);
    }

<<<<<<< HEAD
    @PostMapping
=======
    /*@PostMapping
>>>>>>> main
    public ResponseEntity<UserResponseDto> createNewUser(@RequestBody @Valid UserCreateDto dto) {

        UserResponseDto response = service.createNewUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
<<<<<<< HEAD
    }
=======
    }*/
>>>>>>> main

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser() {
        service.deleteCurrentUser();
        return ResponseEntity.noContent().build();
    }
}