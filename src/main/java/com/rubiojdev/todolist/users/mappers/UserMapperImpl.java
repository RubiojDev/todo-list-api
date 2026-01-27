package com.rubiojdev.todolist.users.mappers;

import com.rubiojdev.todolist.users.dtos.UserCreateDto;
import com.rubiojdev.todolist.users.dtos.UserResponseDto;
import com.rubiojdev.todolist.users.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper{

    @Override
    public User toEntity(UserCreateDto dto) {
        if (dto == null) throw new RuntimeException("UserCreateDto no puede ser NULL");

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());

        return user;
    }

    @Override
    public UserResponseDto toResponseDto(User user) {

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(user.getId());
        responseDto.setUsername(user.getUsername());
        responseDto.setEmail(user.getEmail());
        responseDto.setCreatedAt(user.getCreatedAt());

        return responseDto;
    }
}
