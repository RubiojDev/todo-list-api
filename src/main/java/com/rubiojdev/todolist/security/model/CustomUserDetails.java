package com.rubiojdev.todolist.security.model;

import com.rubiojdev.todolist.users.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Implementación personalizada de {@link UserDetails} utilizada por Spring Security.
 *
 * <p>Esta clase actúa como un adaptador entre la entidad {@link User}
 * almacenada en la base de datos y el modelo de autenticación que utiliza
 * Spring Security.</p>
 *
 * <p>Permite que la información del usuario, como el email y la contraseña,
 * sea utilizada por el framework durante el proceso de autenticación.</p>
 *
 * <p>En esta implementación no se manejan roles ni autoridades, por lo que
 * {@link #getAuthorities()} retorna una colección vacía.</p>
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * Retorna la entidad {@link User} asociada a este objeto de autenticación.
     *
     * @return entidad del usuario autenticado
     */
    public User getUser() {
        return user;
    }
}
