package com.rubiojdev.todolist.security.service;

import com.rubiojdev.todolist.users.entities.User;
import com.rubiojdev.todolist.users.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_existEmail_returnsUserDetails() {
        //Arrange
        String email = "user@gmail.com";
        User user = new User("User1", "user@gmail.com", "1234");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        //Act
        UserDetails result = customUserDetailsService.loadUserByUsername(email);

        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(user.getEmail(), result.getUsername());
        Assertions.assertEquals(user.getPassword(), result.getPassword());

        verify(userRepository).findByEmail(email);
    }

    @Test
    void loadUserByUsername_emailNotFound_throwUsernameNotFoundException() {
        //Arrange
        String email = "user@gmail.com";
        String messageExpected = "Usuario no encontrado";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //Act & Assert
        UsernameNotFoundException exception = Assertions.assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername(email));

        Assertions.assertEquals(messageExpected, exception.getMessage());

        verify(userRepository).findByEmail(email);
    }
}