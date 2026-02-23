package com.rubiojdev.todolist.users.mappers;

import com.rubiojdev.todolist.users.dtos.UserCreateDto;
import com.rubiojdev.todolist.users.dtos.UserResponseDto;
import com.rubiojdev.todolist.users.entities.User;

public interface UserMapper {

    User toEntity(UserCreateDto dto);

    UserResponseDto toResponseDto(User user);
}
