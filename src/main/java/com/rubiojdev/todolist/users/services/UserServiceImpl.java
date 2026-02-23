package com.rubiojdev.todolist.users.services;

import com.rubiojdev.todolist.shared.exceptions.DuplicateResourceException;
import com.rubiojdev.todolist.shared.exceptions.EntotyNotFoundException;
import com.rubiojdev.todolist.users.dtos.UserCreateDto;
import com.rubiojdev.todolist.users.dtos.UserResponseDto;
import com.rubiojdev.todolist.users.entities.User;
import com.rubiojdev.todolist.users.mappers.UserMapper;
import com.rubiojdev.todolist.users.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
<<<<<<< HEAD
=======
import org.springframework.security.crypto.password.PasswordEncoder;
>>>>>>> main
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository repository;
    private final UserMapper mapper;
<<<<<<< HEAD

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.repository = userRepository;
        this.mapper = userMapper;
=======
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder) {
        this.repository = userRepository;
        this.mapper = userMapper;
        this.passwordEncoder = passwordEncoder;
>>>>>>> main
    }


    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getCurrentUser() {

        Long userId = 1L;
        User user =repository.findById(userId)
                .orElseThrow(() -> new EntotyNotFoundException("Usuario no encontrado"));

        return mapper.toResponseDto(user);
    }

    @Override
    @Transactional
<<<<<<< HEAD
    public UserResponseDto createNewUser(UserCreateDto dto) {
=======
    public User createNewUser(UserCreateDto dto) {
>>>>>>> main

        if (repository.existsByUsernameIgnoreCaseOrEmailIgnoreCase(
                dto.getUsername(),
                dto.getEmail())) {
            throw new DuplicateResourceException("Ese nombre de Usuario o Email ya estan en uso");
        }

<<<<<<< HEAD
        String password = dto.getPassword();
        String passwordHash = password + "clave";
=======

        String passwordHash = passwordEncoder.encode(dto.getPassword());
>>>>>>> main

        User user = mapper.toEntity(dto);
        user.setPassword(passwordHash);

        User result = repository.save(user);
<<<<<<< HEAD
        UserResponseDto responseDto = mapper.toResponseDto(result);

        return responseDto;
=======
        //UserResponseDto responseDto = mapper.toResponseDto(result);

        //return responseDto;
        return result;
>>>>>>> main
    }

    @Override
    @Transactional
    public void deleteCurrentUser() {

        Long userId = 1L;
        User user = repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        repository.delete(user);
    }
}
