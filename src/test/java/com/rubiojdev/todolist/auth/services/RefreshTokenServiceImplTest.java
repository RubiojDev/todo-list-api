package com.rubiojdev.todolist.auth.services;

import com.rubiojdev.todolist.auth.entities.RefreshToken;
import com.rubiojdev.todolist.auth.repositories.RefreshTokenRepository;
import com.rubiojdev.todolist.users.entities.User;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceImplTest {

    @Mock
    RefreshTokenRepository repository;
    @InjectMocks
    RefreshTokenServiceImpl service;

    @Test
    void createRefreshToken_validUser_returnsSavedToken() {

        //Arrange
        User user = new User("User1", "user1@gmail.com", "1234");

        RefreshToken savedRefreshToken = new RefreshToken();
        savedRefreshToken.setToken("405010");
        savedRefreshToken.setUser(user);

        when(repository.save(any(RefreshToken.class))).thenReturn(savedRefreshToken);

        //Act
        RefreshToken result = service.createRefreshToken(user);
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);

        //Assert
        verify(repository).save(captor.capture());

        RefreshToken saved = captor.getValue();

        Assertions.assertEquals(user, saved.getUser());
        Assertions.assertNotNull(saved.getToken());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(savedRefreshToken.getToken(), result.getToken());
        Assertions.assertEquals(user, result.getUser());
    }

    @Test
    void findByToken_validRefreshToken_returnsRefreshToken() {
        //Arrange
        String token = "123456";
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setRevoked(false);
        refreshToken.setExpiryDate(Instant.now().plus(Duration.ofDays(7)));

        when(repository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        //Act
        RefreshToken result = service.findByToken(token);

        //Assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(token, result.getToken());
        Assertions.assertFalse(result.isRevoked());
        Assertions.assertTrue(result.getExpiryDate().isAfter(Instant.now()));

        verify(repository).findByToken(token);
    }

    @Test
    void findByToken_tokenNotFound_throwsEntityNotFoundException() {
        //Arrange
        String token = "123456";
        String messageExpected = "Refresh Token no encontrado";

        when(repository.findByToken(token)).thenReturn(Optional.empty());

        //Act & Assert
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
                service.findByToken(token));

        Assertions.assertEquals(messageExpected, exception.getMessage());

        verify(repository).findByToken(token);
    }

    @Test
    void findByToken_tokenIsRevoked_throwsIllegalArgumentException() {
        //Arrange
        String token = "123456";
        String messageExpected = "Refresh token Invalido";

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setRevoked(true);
        refreshToken.setExpiryDate(Instant.now().plus(Duration.ofDays(7)));

        when(repository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        //Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                service.findByToken(token));

        Assertions.assertEquals(messageExpected, exception.getMessage());

        verify(repository).findByToken(token);
    }

    @Test
    void findByToken_tokenIsExpired_throwsIllegalArgumentException() {
        //Arrange
        String token = "123456";
        String messageExpected = "Refresh token expirado";

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setRevoked(false);
        refreshToken.setExpiryDate(Instant.now().minusSeconds(1));

        when(repository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        //Act & Assert
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
                service.findByToken(token));

        Assertions.assertEquals(messageExpected, exception.getMessage());

        verify(repository).findByToken(token);
    }

    @Test
    void revokeToken_validToken_isTrueRevoked() {
        //Arrange
        String token = "123455";

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRevoked(false);

        when(repository.findByToken(token)).thenReturn(Optional.of(refreshToken));

        //Act
        service.revokeToken(token);

        //Assert
        Assertions.assertTrue(refreshToken.isRevoked());
        verify(repository).findByToken(token);
        verify(repository).save(refreshToken);
    }

    @Test
    void revokeToken_tokenNotFound_throwsEntityNotFoundException() {
        //Arrange
        String messageExpected = "Refresh Token no encontrado";
        String token = "123455";

        when(repository.findByToken(token)).thenReturn(Optional.empty());

        //Act & Assert
        EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class, () ->
                service.revokeToken(token));

        Assertions.assertEquals(messageExpected, exception.getMessage());

        verify(repository).findByToken(token);
        verify(repository, never()).save(any(RefreshToken.class));
    }

    @Test
    void deleteRefreshToken_deletedRefreshToken_callDeleteByToken() {
        //Arrange
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("123456");

        //Act
        service.deleteRefreshToken(refreshToken);

        //Assert
        verify(repository).deleteByToken(refreshToken.getToken());
    }
}