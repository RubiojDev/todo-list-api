package com.rubiojdev.todolist.users.services;

import com.rubiojdev.todolist.shared.exceptions.DuplicateResourceException;
import com.rubiojdev.todolist.users.dtos.UserCreateDto;
import com.rubiojdev.todolist.users.dtos.UserResponseDto;
import com.rubiojdev.todolist.users.entities.User;
import com.rubiojdev.todolist.users.mappers.UserMapper;
import com.rubiojdev.todolist.users.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper mapper;
    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl service;

    private User user;
    private UserCreateDto createDto;

    @BeforeEach
    public void setUp() {
        user = new User("User1", "user1@gmail.com", "1234");
        createDto = new UserCreateDto("User1", "user1@gmail.com", "1234");
    }

    @Test
    void getCurrentUser_validUser_returnsUserResponseDto() {
        // Arrange
        UserResponseDto responseDto = new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt());

        when(mapper.toResponseDto(user)).thenReturn(responseDto);

        // Act
        UserResponseDto result = service.getCurrentUser(user);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(responseDto.getId(), result.getId());
        Assertions.assertEquals(responseDto.getUsername(), result.getUsername());
        Assertions.assertEquals(responseDto.getEmail(), result.getEmail());
        Assertions.assertEquals(responseDto.getCreatedAt(), result.getCreatedAt());

        verify(mapper).toResponseDto(user);
    }

    @Test
    void createNewUser_verifyUsernameAndEmail_returnsUser() {
        // Arrange
        String passwordHash = "udh432";

        when(repository.existsByUsernameIgnoreCaseOrEmailIgnoreCase(createDto.getUsername(), createDto.getEmail()))
                .thenReturn(false);

        when(passwordEncoder.encode(createDto.getPassword()))
                .thenReturn(passwordHash);

        when(mapper.toEntity(createDto)).thenReturn(user);

        when(repository.save(user)).thenReturn(user);

        // Act
        User result = service.createNewUser(createDto);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(createDto.getUsername(), result.getUsername());
        Assertions.assertEquals(createDto.getEmail(), result.getEmail());
        Assertions.assertEquals(passwordHash, result.getPassword());

        verify(repository).existsByUsernameIgnoreCaseOrEmailIgnoreCase(createDto.getUsername(), createDto.getEmail());
        verify(passwordEncoder).encode(createDto.getPassword());
        verify(mapper).toEntity(createDto);
        verify(repository).save(user);
    }

    @Test
    void createNewUser_existingUsernameOrEmail_throwsDuplicateResourceException() {
        // Arrange
        String expectedMessage = "Ese nombre de Usuario o Email ya estan en uso";

        when(repository.existsByUsernameIgnoreCaseOrEmailIgnoreCase(createDto.getUsername(), createDto.getEmail()))
                .thenReturn(true);

        // Act & Assert
        DuplicateResourceException exception = Assertions.assertThrows(DuplicateResourceException.class, () ->
                service.createNewUser(createDto));

        Assertions.assertEquals(expectedMessage, exception.getMessage());

        verify(repository).existsByUsernameIgnoreCaseOrEmailIgnoreCase(createDto.getUsername(), createDto.getEmail());
        verify(passwordEncoder, never()).encode(any());
        verify(mapper, never()).toEntity(any());
        verify(repository, never()).save(any());
    }

    @Test
    void deleteCurrentUser_validUser_callsRepositoryDelete() {
        // Arrange

        // Act & Assert
        service.deleteCurrentUser(user);

        verify(repository).delete(user);
    }
}