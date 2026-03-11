package com.rubiojdev.todolist.users.services;

import com.rubiojdev.todolist.auth.repositories.RefreshTokenRepository;
import com.rubiojdev.todolist.shared.exceptions.DuplicateResourceException;
import com.rubiojdev.todolist.users.dtos.UserCreateDto;
import com.rubiojdev.todolist.users.dtos.UserResponseDto;
import com.rubiojdev.todolist.users.entities.User;
import com.rubiojdev.todolist.users.mappers.UserMapper;
import com.rubiojdev.todolist.users.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación de {@link UserService}.
 * <p>
 * Gestiona la lógica de negocio relacionada con la creación, consulta y eliminación
 * de usuarios en el sistema. Esta clase forma parte de la capa de servicio dentro
 * de una arquitectura en capas, y se encarga de coordinar las operaciones entre
 * los controladores y la capa de persistencia ({@link UserRepository}).
 * <p>
 * Además, valida las reglas de negocio, garantiza la unicidad de nombres de usuario
 * y correos electrónicos, y gestiona la eliminación de datos asociados cuando un
 * usuario es eliminado del sistema.
 * <p>
 * Responsabilidades principales:
 * <ul>
 *     <li>Crear nuevos usuarios con validación de duplicados</li>
 *     <li>Recuperar la información del usuario autenticado</li>
 *     <li>Eliminar la cuenta del usuario y sus datos asociados</li>
 *     <li>Encriptar contraseñas mediante {@link PasswordEncoder}</li>
 * </ul>
 *
 * @see UserService
 * @see UserRepository
 * @see UserMapper
 */
@Service
public class UserServiceImpl implements UserService{

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           RefreshTokenRepository refreshTokenRepository) {
        this.repository = userRepository;
        this.mapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Obtiene la información del usuario actualmente autenticado.
     * <p>
     * Realiza la conversión de la entidad {@link User} a un DTO de respuesta
     * ({@link UserResponseDto}) para exponerla al cliente sin información sensible.
     * <p>
     * Esta operación es de solo lectura {@code readOnly = true} para optimizar
     * el rendimiento de la transacción.
     *
     * @param user usuario autenticado cuya información se desea obtener
     * @return {@link UserResponseDto} con la información básica del usuario
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getCurrentUser(User user) {
        return mapper.toResponseDto(user);
    }

    /**
     * Crea un nuevo usuario en el sistema.
     * <p>
     * Realiza las siguientes validaciones y operaciones:
     * <ul>
     *     <li>Válida que el nombre de usuario y correo electrónico
     *         sean únicos en el sistema</li>
     *     <li>Encripta la contraseña del usuario mediante {@link PasswordEncoder}</li>
     *     <li>Convierte el DTO {@link UserCreateDto} a entidad {@link User}</li>
     *     <li>Persiste la entidad en la base de datos</li>
     * </ul>
     * <p>
     * <strong>Nota:</strong> Si el nombre de usuario o el correo electrónico
     * ya existen en el sistema, se lanza una {@link DuplicateResourceException}.
     *
     * @param dto DTO que contiene los datos necesarios para crear el usuario
     *            (nombre de usuario, email, contraseña, etc.)
     * @return entidad {@link User} creada y persistida en la base de datos
     * @throws DuplicateResourceException si el nombre de usuario o email
     *         ya existen en el sistema
     */
    @Override
    @Transactional
    public User createNewUser(UserCreateDto dto) {

        if (repository.existsByUsernameIgnoreCaseOrEmailIgnoreCase(
                dto.getUsername(),
                dto.getEmail())) {
            throw new DuplicateResourceException("Ese nombre de Usuario o Email ya estan en uso");
        }

        String passwordHash = passwordEncoder.encode(dto.getPassword());

        User user = mapper.toEntity(dto);
        user.setPassword(passwordHash);

        User result = repository.save(user);

        return result;
    }

    /**
     * Elimina la cuenta del usuario actualmente autenticado.
     * <p>
     * Realiza una eliminación en cascada de los datos asociados al usuario:
     * <ul>
     *     <li>Elimina todos los tokens de refresco ({@link RefreshTokenRepository})
     *         asociados al usuario para invalidar todas sus sesiones</li>
     *     <li>Elimina la entidad {@link User} de la base de datos</li>
     * </ul>
     * <p>
     * <strong>Advertencia:</strong> Esta operación es destructiva y definitiva.
     * Se recomienda que el usuario confirme su intención antes de ejecutarla.
     *
     * @param user usuario autenticado cuya cuenta será eliminada
     */
    @Override
    @Transactional
    public void deleteCurrentUser(User user) {
        refreshTokenRepository.deleteByUserId(user.getId());
        repository.delete(user);
    }
}
