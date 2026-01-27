package com.rubiojdev.todolist.users.services;

import com.rubiojdev.todolist.users.dtos.UserCreateDto;
import com.rubiojdev.todolist.users.dtos.UserResponseDto;

public interface UserService {

    UserResponseDto getCurrentUser();

    UserResponseDto createNewUser(UserCreateDto dto);

    void deleteCurrentUser();
}
