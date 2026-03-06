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
import jakarta.persistence.EntityNotFoundException;
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
    private RefreshTokenService refreshTokenService;
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
        String validRefreshToken = "000111222333444";

        User user = new User();
        user.setEmail("user@email.com");

        CustomUserDetails userDetails = new CustomUserDetails(user);

        // Simulamos el Authentication que devuelve el authenticationManager
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(validRefreshToken);

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(authentication);

        when(jwtService.generateToken(authentication))
                .thenReturn(validToken);

        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);

        // Act
        AuthResponse result = authService.login(request);

        // Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(validToken, result.getToken());
        Assertions.assertEquals(validRefreshToken, result.getRefreshToken());

        verify(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        verify(authenticationManager).authenticate(
                argThat(auth ->
                        auth.getPrincipal().equals("user@email.com") &&
                                auth.getCredentials().equals("1234")
                )
        );

        verify(jwtService).generateToken(authentication);
        verify(refreshTokenService).createRefreshToken(user);
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

        verify(jwtService, never()).generateToken(any(Authentication.class));
        verify(refreshTokenService, never()).createRefreshToken(any(User.class));
    }

    @Test
    void register_validRequest_returnsAuthResponse() {
        //Arrange
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("User1");
        registerRequest.setEmail("user1@gmail.com");
        registerRequest.setPassword("1234");

        String token = "valid.token.1234";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("000111222333");

        User user = new User("User1", "user1@gmail.com", "1234");
        UserCreateDto userCreateDto =
                new UserCreateDto("User1", "user1@gmail.com", "1234");

        when(mapper.toUserCreateDto(registerRequest)).thenReturn(userCreateDto);
        when(userService.createNewUser(userCreateDto)).thenReturn(user);
        when(jwtService.generateToken(any(CustomUserDetails.class))).thenReturn(token);
        when(refreshTokenService.createRefreshToken(user)).thenReturn(refreshToken);

        //Act
        AuthResponse result = authService.register(registerRequest);

        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(token, result.getToken());
        Assertions.assertEquals(refreshToken.getToken(), result.getRefreshToken());

        verify(mapper).toUserCreateDto(registerRequest);
        verify(userService).createNewUser(userCreateDto);
        verify(jwtService).generateToken(any(CustomUserDetails.class));
        verify(refreshTokenService).createRefreshToken(user);
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

        verify(jwtService, never()).generateToken(any(CustomUserDetails.class));
        verify(refreshTokenService, never()).createRefreshToken(any(User.class));
    }

    @Test
    void refreshToken_validToken_returnsAuthResponse() {
        //Arrange
        RefreshTokenRequest tokenRequest = new RefreshTokenRequest();
        tokenRequest.setRefreshToken("000111222333");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(tokenRequest.getRefreshToken());
        refreshToken.setUser(new User("User1", "user1@gmail.com", "1234"));
        String newAuthToken = "valid.token.3456";
        RefreshToken newRefreshToken = new RefreshToken();
        newRefreshToken.setToken("33399488203");

        when(refreshTokenService.findByToken(tokenRequest.getRefreshToken())).thenReturn(refreshToken);
        when(jwtService.generateToken(any(CustomUserDetails.class))).thenReturn(newAuthToken);
        when(refreshTokenService.createRefreshToken(refreshToken.getUser())).thenReturn(newRefreshToken);

        //Act
        AuthResponse result = authService.refreshToken(tokenRequest);

        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(newAuthToken, result.getToken());
        Assertions.assertEquals(newRefreshToken.getToken(), result.getRefreshToken());

        verify(refreshTokenService).findByToken(tokenRequest.getRefreshToken());
        verify(jwtService).generateToken(any(CustomUserDetails.class));
        verify(refreshTokenService).createRefreshToken(refreshToken.getUser());
        verify(refreshTokenService).deleteRefreshToken(refreshToken);
    }

    @Test
    void refreshToken_tokenNotFound_throwsEntityNotFoundException() {
        //Arrange
        String messageExpected = "Refresh Token no encontrado";

        RefreshTokenRequest tokenRequest = new RefreshTokenRequest();
        tokenRequest.setRefreshToken("000111222333");

        when(refreshTokenService.findByToken(tokenRequest.getRefreshToken()))
                .thenThrow(new EntityNotFoundException("Refresh Token no encontrado"));

        //Act & Assert
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
                authService.refreshToken(tokenRequest));

        Assertions.assertEquals(messageExpected, exception.getMessage());

        verify(refreshTokenService).findByToken(tokenRequest.getRefreshToken());
        verify(jwtService, never()).generateToken(any(CustomUserDetails.class));
        verify(refreshTokenService, never()).createRefreshToken(any(User.class));
        verify(refreshTokenService, never()).deleteRefreshToken(any(RefreshToken.class));
    }

    @Test
    void logout_validToken_revokeToken() {
        //Arrange
        RefreshTokenRequest refreshToken = new RefreshTokenRequest();
        refreshToken.setRefreshToken("6545797");

        //Act
        authService.logout(refreshToken);

        //Assert
        verify(refreshTokenService).revokeToken(refreshToken.getRefreshToken());
    }
}