package com.rubiojdev.todolist.auth.services;

import com.rubiojdev.todolist.auth.entities.RefreshToken;
import com.rubiojdev.todolist.auth.repositories.RefreshTokenRepository;
import com.rubiojdev.todolist.users.entities.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final RefreshTokenRepository repository;

    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plus(Duration.ofDays(7)));
        refreshToken.setUser(user);

        RefreshToken savedToken = repository.save(refreshToken);
        return savedToken;
    }

    @Override
    @Transactional()
    public RefreshToken findByToken(String token) {

        RefreshToken refreshToken = repository.findByToken(token).orElseThrow(() ->
                        new EntityNotFoundException("Refresh Token no encontrado"));

        if (refreshToken.isRevoked()) throw new IllegalArgumentException("Refresh token Invalido");

        verifyExpiration(refreshToken);

        return refreshToken;
    }

    @Override
    @Transactional
    public void revokeToken(String token) {

        RefreshToken refreshToken = repository.findByToken(token).orElseThrow(() ->
                new EntityNotFoundException("Refresh Token no encontrado"));

        refreshToken.setRevoked(true);
        repository.save(refreshToken);
    }

    @Override
    @Transactional
    public void deleteRefreshToken(RefreshToken refreshToken) {
        repository.deleteByToken(refreshToken.getToken());
    }

    private void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            token.setRevoked(true);
            repository.save(token);
            throw new IllegalArgumentException("Refresh token expirado");
        }
    }
}
