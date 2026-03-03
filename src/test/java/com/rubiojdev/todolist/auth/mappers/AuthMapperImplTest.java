package com.rubiojdev.todolist.auth.mappers;

import com.rubiojdev.todolist.auth.dtos.RegisterRequest;
import com.rubiojdev.todolist.users.dtos.UserCreateDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthMapperImplTest {

    @InjectMocks
    AuthMapperImpl mapper;

    @Test
    void toUserCreateDto_creatingUserCreateDto_returnsUserCreateDto() {
        //Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("User1");
        registerRequest.setEmail("user@gmail.com");
        registerRequest.setPassword("1234");

        //Act
        UserCreateDto result = mapper.toUserCreateDto(registerRequest);

        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(registerRequest.getUsername(), result.getUsername());
        Assertions.assertEquals(registerRequest.getEmail(), result.getEmail());
        Assertions.assertEquals(registerRequest.getPassword(), result.getPassword());
    }
}