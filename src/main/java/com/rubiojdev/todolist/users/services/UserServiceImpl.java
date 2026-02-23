package com.rubiojdev.todolist.users.services;

import com.rubiojdev.todolist.shared.exceptions.DuplicateResourceException;
import com.rubiojdev.todolist.shared.exceptions.EntotyNotFoundException;
import com.rubiojdev.todolist.users.dtos.UserCreateDto;
import com.rubiojdev.todolist.users.dtos.UserResponseDto;
import com.rubiojdev.todolist.users.entities.User;
import com.rubiojdev.todolist.users.mappers.UserMapper;
import com.rubiojdev.todolist.users.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder) {
        this.repository = userRepository;
        this.mapper = userMapper;
        this.passwordEncoder = passwordEncoder;
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
    public User createNewUser(UserCreateDto dto) {

        if (repository.existsByUsernameIgnoreCaseOrEmailIgnoreCase(
                dto.getUsername(),
                dto.getEmail())) {
            throw new DuplicateResourceException("Ese nombre de Usuario o Email ya estan en uso");
        }


        String passwordHash = passwordEncoder.encode(dto.getPassword());

        User user = mapper.toEntity(dto);
        user.setPassword(passwordHash);

        User result = repository.save(user);
        //UserResponseDto responseDto = mapper.toResponseDto(result);

        //return responseDto;
        return result;
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
