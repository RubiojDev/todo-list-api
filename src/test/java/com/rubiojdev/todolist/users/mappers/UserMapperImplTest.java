package com.rubiojdev.todolist.users.mappers;

import com.rubiojdev.todolist.users.dtos.UserCreateDto;
import com.rubiojdev.todolist.users.dtos.UserResponseDto;
import com.rubiojdev.todolist.users.entities.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class UserMapperImplTest {

    @InjectMocks
    private UserMapperImpl mapper;

    @Test
    void testToEntity_creatingUser_returnUser() {
        //Arrange
        UserCreateDto createDto = new UserCreateDto(
                "User1",
                "user1@gmail.com",
                "1234");

        //Act
        User result = mapper.toEntity(createDto);

        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(createDto.getUsername(), result.getUsername());
        Assertions.assertEquals(createDto.getEmail(), result.getEmail());
        Assertions.assertNull(result.getPassword());
    }

    @Test
    void testToEntity_nullUserCreateDto_throwIllegalArgumentException() {
        //Arrange
        UserCreateDto createDto = null;
        String expectedMessage = "UserCreateDto no puede ser NULL";

        //Act & Assert
       IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
               mapper.toEntity(createDto));

       Assertions.assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testToResponseDto_creatingUserResponseDto_returnUserResponseDto() {
        //Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("User1");
        user.setEmail("user1@gmail.com");
        user.setCreatedAt(LocalDateTime.now());

        //Act
        UserResponseDto result = mapper.toResponseDto(user);

        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(user.getId(), result.getId());
        Assertions.assertEquals(user.getUsername(), result.getUsername());
        Assertions.assertEquals(user.getEmail(), result.getEmail());
        Assertions.assertEquals(user.getCreatedAt(), result.getCreatedAt());
    }
}