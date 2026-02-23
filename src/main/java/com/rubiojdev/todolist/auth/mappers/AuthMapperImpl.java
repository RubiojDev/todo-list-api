package com.rubiojdev.todolist.auth.mappers;

import com.rubiojdev.todolist.auth.dtos.RegisterRequest;
import com.rubiojdev.todolist.users.dtos.UserCreateDto;
import org.springframework.stereotype.Component;

@Component
public class AuthMapperImpl implements AuthMapper{

    @Override
    public UserCreateDto toUserCreateDto(RegisterRequest registerRequest) {

        UserCreateDto dto = new UserCreateDto();
        dto.setUsername(registerRequest.getUsername());
        dto.setEmail(registerRequest.getEmail());
        dto.setPassword(registerRequest.getPassword());

        return dto;
    }
}
