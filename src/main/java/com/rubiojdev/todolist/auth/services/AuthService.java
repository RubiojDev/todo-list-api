package com.rubiojdev.todolist.auth.services;

import com.rubiojdev.todolist.auth.dtos.AuthResponse;
import com.rubiojdev.todolist.auth.dtos.LoginRequest;
import com.rubiojdev.todolist.auth.dtos.RegisterRequest;
import com.rubiojdev.todolist.security.jwt.JwtService;
import com.rubiojdev.todolist.users.dtos.UserCreateDto;
import com.rubiojdev.todolist.users.entities.User;
import com.rubiojdev.todolist.users.services.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       UserService userService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        String token = jwtService.generateToken(authentication);
        return new AuthResponse(token);
    }

    public AuthResponse signup(RegisterRequest registerRequest) {

        UserCreateDto userDto = new UserCreateDto();
        userDto.setUsername(registerRequest.getUsername());
        userDto.setEmail(registerRequest.getEmail());
        userDto.setPassword(registerRequest.getPassword());

        User user = userService.createNewUser(userDto);

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }
}
