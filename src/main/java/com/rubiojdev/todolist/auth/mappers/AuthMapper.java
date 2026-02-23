package com.rubiojdev.todolist.auth.mappers;

import com.rubiojdev.todolist.auth.dtos.RegisterRequest;
import com.rubiojdev.todolist.users.dtos.UserCreateDto;

public interface AuthMapper {
    UserCreateDto toUserCreateDto(RegisterRequest registerRequest);
}
