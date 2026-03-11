package com.rubiojdev.todolist.security.service;

import com.rubiojdev.todolist.security.model.CustomUserDetails;
import com.rubiojdev.todolist.users.entities.User;
import com.rubiojdev.todolist.users.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementación personalizada de {@link UserDetailsService} utilizada por
 * Spring Security para cargar la información de los usuarios desde la base de datos.
 *
 * <p>Durante el proceso de autenticación, Spring Security invoca el método
 * {@link #loadUserByUsername(String)} para obtener los datos del usuario
 * a partir de su identificador. En este caso, el sistema utiliza el
 * email como identificador único.</p>
 *
 * <p>Si el usuario existe, se envuelve dentro de un objeto
 * {@link CustomUserDetails}, el cual adapta la entidad {@link User}
 * al modelo de autenticación que utiliza Spring Security.</p>
 *
 * <p>Si el usuario no existe, se lanza una {@link UsernameNotFoundException}
 * para indicar que la autenticación no puede continuar.</p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Busca un usuario en la base de datos utilizando su email.
     *
     * @param email identificador del usuario
     * @return objeto {@link UserDetails} con la información del usuario
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new CustomUserDetails(user);
    }
}
