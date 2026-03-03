package com.rubiojdev.todolist.auth.services;

import com.rubiojdev.todolist.auth.dtos.AuthResponse;
import com.rubiojdev.todolist.auth.dtos.LoginRequest;
import com.rubiojdev.todolist.auth.dtos.RegisterRequest;
import com.rubiojdev.todolist.auth.mappers.AuthMapper;
import com.rubiojdev.todolist.security.jwt.JwtService;
import com.rubiojdev.todolist.security.model.CustomUserDetails;
import com.rubiojdev.todolist.users.dtos.UserCreateDto;
import com.rubiojdev.todolist.users.entities.User;
import com.rubiojdev.todolist.users.services.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserService userService;
    @Mock
    private AuthMapper mapper;
    @InjectMocks
    private AuthService authService;

    @Test
    void login_validCredentials_returnsAuthResponseWithToken() {

        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("user@email.com");
        request.setPassword("1234");

        String validToken = "valid.token.1234";

        // Simulamos el Authentication que devuelve el authenticationManager
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                );

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);

        when(jwtService.generateToken(authentication))
                .thenReturn(validToken);

        // Act
        AuthResponse result = authService.login(request);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(validToken, result.getToken());

        verify(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(authenticationManager).authenticate(
                argThat(auth ->
                        auth.getPrincipal().equals("user@email.com") &&
                                auth.getCredentials().equals("1234")
                )
        );

        verify(jwtService).generateToken(authentication);
    }

    @Test
    void login_invalidCredentials_shouldThrowException() {

        LoginRequest request = new LoginRequest();
        request.setEmail("user@email.com");
        request.setPassword("wrong");

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        Assertions.assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void register_validRequest_returnsAuthResponseWithToken() {
        //Arrange
        String token = "valid.token.1234";

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("User1");
        registerRequest.setEmail("user1@gmail.com");
        registerRequest.setPassword("1234");

        UserCreateDto userCreateDto =
                new UserCreateDto("User1", "user1@gmail.com", "1234");

        User user = new User("User1", "user1@gmail.com", "1234");

        when(mapper.toUserCreateDto(registerRequest)).thenReturn(userCreateDto);
        when(userService.createNewUser(userCreateDto)).thenReturn(user);
        when(jwtService.generateToken(any(Authentication.class))).thenReturn(token);

        //Act
        AuthResponse result = authService.register(registerRequest);

        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(token, result.getToken());

        verify(mapper).toUserCreateDto(registerRequest);
        verify(userService).createNewUser(userCreateDto);

        verify(jwtService).generateToken(
                argThat(auth ->
                        auth.getPrincipal() instanceof CustomUserDetails
                )
        );
    }

    @Test
    void register_userAlreadyExists_shouldThrowException() {
        //Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@gmail.com");

        UserCreateDto dto = new UserCreateDto();

        when(mapper.toUserCreateDto(request)).thenReturn(dto);
        when(userService.createNewUser(dto))
                .thenThrow(new IllegalStateException("User already exists"));

        //Act & Assert
        Assertions.assertThrows(IllegalStateException.class, () ->
                authService.register(request));

        verify(jwtService, never()).generateToken(any());
    }
}