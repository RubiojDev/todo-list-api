package com.rubiojdev.todolist.auth.services;

import com.rubiojdev.todolist.auth.entities.RefreshToken;
import com.rubiojdev.todolist.users.entities.User;

/**
 * Define el contrato para la gestión de {@link RefreshToken} dentro del sistema
 * de autenticación.
 * <p>
 * Las implementaciones de esta interfaz son responsables de manejar el ciclo de
 * vida de los tokens de refresco utilizados para renovar la autenticación basada
 * en JWT sin necesidad de que el usuario vuelva a introducir sus credenciales.
 * <p>
 * Funcionalidades principales:
 * <ul>
 *     <li>Creación de nuevos tokens de refresco asociados a usuarios</li>
 *     <li>Búsqueda y validación de tokens de refresco</li>
 *     <li>Revocación de tokens comprometidos o inválidos</li>
 *     <li>Eliminación permanente de tokens de la base de datos</li>
 * </ul>
 *
 * @see RefreshToken
 */
public interface RefreshTokenService {

    /**
     * Crea un nuevo {@link RefreshToken} asociado al usuario especificado.
     *
     * @param user usuario al cual se le asignará el token de refresco
     * @return token de refresco creado y persistido
     */
    RefreshToken createRefreshToken(User user);

    /**
     * Busca y valida un token de refresco.
     * <p>
     * La implementación debe verificar que el token:
     * <ul>
     *     <li>Exista en el sistema</li>
     *     <li>No haya sido revocado</li>
     *     <li>No haya expirado</li>
     * </ul>
     *
     * @param token valor del token de refresco
     * @return {@link RefreshToken} válido asociado a un usuario
     * @throws com.rubiojdev.todolist.shared.exceptions.EntityNotFoundException si el token no existe
     * @throws com.rubiojdev.todolist.shared.exceptions.InvalidTokenException si el token está revocado o ha expirado
     */
    RefreshToken findByToken(String token);

    /**
     * Revoca un token de refresco, impidiendo su uso para renovar
     * la autenticación del usuario.
     *
     * @param token valor del token de refresco a revocar
     * @throws com.rubiojdev.todolist.shared.exceptions.EntityNotFoundException si el token no existe en el sistema
     */
    void revokeToken(String token);

    /**
     * Elimina un token de refresco de la base de datos de forma permanente.
     *
     * @param token entidad {@link RefreshToken} a eliminar
     */
    void deleteRefreshToken(RefreshToken token);
}
