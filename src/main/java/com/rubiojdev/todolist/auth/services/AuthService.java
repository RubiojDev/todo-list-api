package com.rubiojdev.todolist.auth.services;

import com.rubiojdev.todolist.auth.dtos.AuthResponse;
import com.rubiojdev.todolist.auth.dtos.LoginRequest;
import com.rubiojdev.todolist.auth.dtos.RefreshTokenRequest;
import com.rubiojdev.todolist.auth.dtos.RegisterRequest;
import com.rubiojdev.todolist.auth.entities.RefreshToken;
import com.rubiojdev.todolist.auth.mappers.AuthMapper;
import com.rubiojdev.todolist.security.jwt.JwtService;
import com.rubiojdev.todolist.security.model.CustomUserDetails;
import com.rubiojdev.todolist.users.dtos.UserCreateDto;
import com.rubiojdev.todolist.users.entities.User;
import com.rubiojdev.todolist.users.services.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthMapper mapper;

    public AuthService(AuthenticationManager authenticationManager,
                       RefreshTokenService refreshTokenService,
                       UserService userService,
                       JwtService jwtService,
                       AuthMapper mapper) {

        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.mapper = mapper;
    }

    public AuthResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        String token = jwtService.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(token, refreshToken.getToken());
    }

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {

        UserCreateDto userDto = mapper.toUserCreateDto(registerRequest);

        User user = userService.createNewUser(userDto);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        /*UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );*/

        String token = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(token, refreshToken.getToken());
    }

    public AuthResponse refreshToken(RefreshTokenRequest token) {

        RefreshToken refreshToken = refreshTokenService.findByToken(token.getRefreshToken());

        User user = refreshToken.getUser();

        CustomUserDetails userDetails = new CustomUserDetails(user);

        String newAuthToken = jwtService.generateToken(userDetails);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        refreshTokenService.deleteRefreshToken(refreshToken);

        return new AuthResponse(newAuthToken, newRefreshToken.getToken());
    }

    public void logout(RefreshTokenRequest refreshToken) {
        refreshTokenService.revokeToken(refreshToken.getRefreshToken());
    }
}
