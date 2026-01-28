package com.rubiojdev.todolist.users.services;

import com.rubiojdev.todolist.users.dtos.UserCreateDto;
import com.rubiojdev.todolist.users.dtos.UserResponseDto;
import com.rubiojdev.todolist.users.entities.User;
import com.rubiojdev.todolist.users.mappers.UserMapper;
import com.rubiojdev.todolist.users.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository repository;
    private final UserMapper mapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.repository = userRepository;
        this.mapper = userMapper;
    }


    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getCurrentUser() {

        Long userId = 1L;
        User user =repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


        return mapper.toResponseDto(user);
    }

    @Override
    @Transactional
    public UserResponseDto createNewUser(UserCreateDto dto) {

        if (repository.existsByUsernameIgnoreCaseOrEmailIgnoreCase(
                dto.getUsername(),
                dto.getEmail())) {
            throw new RuntimeException("Ese nombre de Usuario o Email ya estan en uso");
        }

        String password = dto.getPassword();
        String passwordHash = password + "clave";

        User user = mapper.toEntity(dto);
        user.setPassword(passwordHash);

        User result = repository.save(user);
        UserResponseDto responseDto = mapper.toResponseDto(result);

        return responseDto;
    }

    @Override
    @Transactional
    public void deleteCurrentUser() {

        Long userId = 1L;
        User user = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        repository.delete(user);
    }
}
