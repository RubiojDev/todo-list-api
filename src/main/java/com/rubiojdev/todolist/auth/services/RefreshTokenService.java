package com.rubiojdev.todolist.auth.services;

import com.rubiojdev.todolist.auth.entities.RefreshToken;
import com.rubiojdev.todolist.users.entities.User;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(User user);

    RefreshToken findByToken(String token);

    void revokeToken(String token);

    void deleteRefreshToken(RefreshToken token);
}
