package com.rubiojdev.todolist.auth.services;

import com.rubiojdev.todolist.auth.entities.RefreshToken;
import com.rubiojdev.todolist.auth.repositories.RefreshTokenRepository;
import com.rubiojdev.todolist.shared.exceptions.EntityNotFoundException;
import com.rubiojdev.todolist.shared.exceptions.InvalidTokenException;
import com.rubiojdev.todolist.users.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Servicio encargado de gestionar la lógica de negocio relacionada con {@link RefreshToken}.
 * <p>
 * Esta clase forma parte de la capa de servicio dentro de una arquitectura en capas,
 * y se encarga de coordinar las operaciones entre los controladores y la capa
 * de persistencia ({@link RefreshTokenRepository}).
 * <p>
 * Además, valida las reglas de negocio relacionadas con la autenticación y garantiza
 * que cada operación sobre tokens de refresco se realice de forma segura, incluyendo
 * la verificación de expiración y revocación de tokens.
 * <p>
 * Responsabilidades principales:
 * <ul>
 *     <li>Crear nuevos tokens de refresco asociados a usuarios autenticados</li>
 *     <li>Buscar y validar tokens de refresco</li>
 *     <li>Verificar la expiración de tokens</li>
 *     <li>Revocar tokens de refresco inválidos o comprometidos</li>
 *     <li>Eliminar tokens de refresco de la base de datos</li>
 * </ul>
 *
 * @see RefreshTokenService
 * @see RefreshTokenRepository
 * @see RefreshToken
 */
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService{

    private final RefreshTokenRepository repository;

    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    /**
     * Crea un nuevo token de refresco asociado al usuario autenticado.
     * <p>
     * Realiza las siguientes operaciones:
     * <ul>
     *     <li>Genera un identificador único para el token mediante UUID</li>
     *     <li>Establece la fecha de expiración a 7 días desde el momento actual</li>
     *     <li>Asocia el token al usuario especificado</li>
     *     <li>Persiste la entidad {@link RefreshToken} en la base de datos</li>
     * </ul>
     * <p>
     * El token generado puede utilizarse posteriormente para renovar la autenticación
     * del usuario sin necesidad de reintroducir sus credenciales.
     *
     * @param user usuario autenticado para el cual se crea el token de refresco
     * @return {@link RefreshToken} creado y persistido en la base de datos
     */
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

    /**
     * Busca y valida un token de refresco.
     * <p>
     * Realiza las siguientes validaciones antes de devolver el token:
     * <ul>
     *     <li>Verifica que el token existe en la base de datos</li>
     *     <li>Valida que el token no ha sido revocado</li>
     *     <li>Verifica que el token no ha expirado (vencimiento a los 7 días)</li>
     * </ul>
     * <p>
     * Si el token ha expirado, se marca automáticamente como revocado.
     *
     * @param token valor del token de refresco a buscar y validar
     * @return {@link RefreshToken} validado y no expirado
     * @throws EntityNotFoundException si el token no existe en la base de datos
     * @throws InvalidTokenException si el token está revocado o ha expirado
     */
    @Override
    @Transactional()
    public RefreshToken findByToken(String token) {

        RefreshToken refreshToken = repository.findByToken(token).orElseThrow(() ->
                        new EntityNotFoundException("Refresh Token no encontrado"));

        if (refreshToken.isRevoked()) throw new InvalidTokenException("Refresh token Invalido");

        verifyExpiration(refreshToken);

        return refreshToken;
    }

    /**
     * Revoca un token de refresco, invalidándolo para futuras operaciones de autenticación.
     * <p>
     * Busca el token especificado y establece su estado como revocado. Un token revocado
     * no podrá ser utilizado para renovar la autenticación incluso si aún no ha expirado.
     * <p>
     * Esta operación es útil para invalidar tokens en caso de:
     * <ul>
     *     <li>Cierre de sesión del usuario</li>
     *     <li>Sospecha de seguridad o compromiso del token</li>
     *     <li>Cambio de contraseña del usuario</li>
     * </ul>
     *
     * @param token valor del token de refresco a revocar
     * @throws EntityNotFoundException si el token no existe en la base de datos
     */
    @Override
    @Transactional
    public void revokeToken(String token) {

        RefreshToken refreshToken = repository.findByToken(token).orElseThrow(() ->
                new EntityNotFoundException("Refresh Token no encontrado"));

        refreshToken.setRevoked(true);
        repository.save(refreshToken);
    }

    /**
     * Elimina un token de refresco de la base de datos de forma permanente.
     * <p>
     * Realiza la eliminación física del token de la persistencia, a diferencia de
     * {@link #revokeToken(String)} que solo marca el token como revocado.
     * <p>
     * Esta operación es destructiva y definitiva, eliminando completamente
     * el registro del token del sistema.
     *
     * @param refreshToken entidad {@link RefreshToken} a eliminar de la base de datos
     */
    @Override
    @Transactional
    public void deleteRefreshToken(RefreshToken refreshToken) {
        repository.deleteByToken(refreshToken.getToken());
    }

    /**
     * Verifica si el {@link RefreshToken} ha expirado.
     * <p>
     * Si la fecha de expiración del token es anterior al momento actual,
     * el token se marca automáticamente como revocado y se persiste el
     * cambio en la base de datos.
     * <p>
     * Posteriormente se lanza una {@link InvalidTokenException} indicando
     * que el token ha expirado.
     *
     * @param token token de refresco a validar
     * @throws InvalidTokenException si el token ya ha expirado
     */
    private void verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            token.setRevoked(true);
            repository.save(token);
            throw new InvalidTokenException("Refresh token expirado");
        }
    }
}
