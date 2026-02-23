package com.rubiojdev.todolist.users.services;

import com.rubiojdev.todolist.users.dtos.UserCreateDto;
import com.rubiojdev.todolist.users.dtos.UserResponseDto;
import com.rubiojdev.todolist.users.entities.User;

public interface UserService {

    UserResponseDto getCurrentUser();

    User createNewUser(UserCreateDto dto);

    void deleteCurrentUser();
}
