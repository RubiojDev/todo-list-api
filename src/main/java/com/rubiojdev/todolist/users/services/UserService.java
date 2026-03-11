package com.rubiojdev.todolist.users.services;

import com.rubiojdev.todolist.users.dtos.UserCreateDto;
import com.rubiojdev.todolist.users.dtos.UserResponseDto;
import com.rubiojdev.todolist.users.entities.User;

/**
 * Define las operaciones relacionadas con la gestión de usuarios del sistema.
 *
 * <p>Esta interfaz forma parte de la capa de servicio y establece el contrato
 * que deben cumplir las implementaciones encargadas de manejar la lógica de
 * negocio asociada a los usuarios.</p>
 *
 * <p>Las operaciones incluyen la obtención de información del usuario
 * autenticado, la creación de nuevas cuentas y la eliminación de usuarios
 * junto con sus datos asociados.</p>
 *
 * @see com.rubiojdev.todolist.users.entities.User
 */
public interface UserService {

    /**
     * Obtiene la información del usuario actualmente autenticado.
     *
     * <p>La entidad {@link User} es convertida a {@link UserResponseDto}
     * para evitar exponer información sensible como contraseñas u otros
     * datos internos del sistema.</p>
     *
     * @param user usuario autenticado
     * @return {@link UserResponseDto} con la información pública del usuario
     */
    UserResponseDto getCurrentUser(User user);

    /**
     * Crea un nuevo usuario en el sistema.
     *
     * <p>La implementación debe validar que el nombre de usuario y el correo
     * electrónico sean únicos, encriptar la contraseña del usuario y
     * persistir la nueva entidad en la base de datos.</p>
     *
     * @param dto datos necesarios para la creación del usuario
     * @return {@link User} creado y persistido
     */
    User createNewUser(UserCreateDto dto);

    /**
     * Elimina la cuenta del usuario autenticado junto con sus datos asociados.
     *
     * <p>La implementación puede encargarse de limpiar información dependiente
     * del usuario, como tokens de autenticación u otros registros vinculados.</p>
     *
     * @param user usuario cuya cuenta será eliminada
     */
    void deleteCurrentUser(User user);
}
