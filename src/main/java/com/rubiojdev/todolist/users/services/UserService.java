package com.rubiojdev.todolist.users.services;

import com.rubiojdev.todolist.users.dtos.UserCreateDto;
import com.rubiojdev.todolist.users.dtos.UserResponseDto;
<<<<<<< HEAD
=======
import com.rubiojdev.todolist.users.entities.User;
>>>>>>> main

public interface UserService {

    UserResponseDto getCurrentUser();

<<<<<<< HEAD
    UserResponseDto createNewUser(UserCreateDto dto);
=======
    User createNewUser(UserCreateDto dto);
>>>>>>> main

    void deleteCurrentUser();
}
