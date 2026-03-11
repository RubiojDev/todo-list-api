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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio encargado de gestionar la lógica de negocio relacionada con la autenticación y autorización.
 * <p>
 * Esta clase forma parte de la capa de servicio dentro de una arquitectura en capas,
 * y se encarga de coordinar las operaciones entre los controladores y los servicios especializados
 * de autenticación ({@link RefreshTokenService}, {@link UserService}, {@link JwtService}).
 * <p>
 * Además, valida las reglas de negocio relacionadas con la seguridad y garantiza que cada operación
 * de autenticación sea realizada de forma segura, incluyendo validación de credenciales,
 * generación de tokens JWT y gestión de tokens de refresco.
 * <p>
 * Responsabilidades principales:
 * <ul>
 *     <li>Autenticar usuarios mediante credenciales (email y contraseña)</li>
 *     <li>Registrar nuevos usuarios en el sistema</li>
 *     <li>Generar tokens JWT y tokens de refresco</li>
 *     <li>Renovar sesiones mediante tokens de refresco</li>
 *     <li>Revocar tokens al cerrar sesión</li>
 * </ul>
 *
 * @see RefreshTokenService
 * @see UserService
 * @see JwtService
 * @see AuthMapper
 */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthMapper mapper;

    public AuthService(AuthenticationManager authenticationManager,
                       RefreshTokenService refreshTokenService,
                       UserService userService,
                       JwtService jwtService,
                       AuthMapper mapper) {

        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.mapper = mapper;
    }

    /**
     * Autentica un usuario mediante sus credenciales (email y contraseña).
     * <p>
     * Realiza las siguientes operaciones:
     * <ul>
     *     <li>Valida las credenciales del usuario mediante {@link AuthenticationManager}</li>
     *     <li>Extrae la información del usuario autenticado</li>
     *     <li>Genera un token JWT para acceso a recursos protegidos</li>
     *     <li>Crea un token de refresco para renovar la sesión con una validez de 7 días</li>
     *     <li>Devuelve ambos tokens al cliente</li>
     * </ul>
     * <p>
     * Los tokens generados son utilizados posteriormente para autenticar solicitudes HTTP
     * a través del filtro de seguridad JWT.
     *
     * @param request DTO que contiene el email y contraseña del usuario
     * @return {@link AuthResponse} con el token JWT y el token de refresco
     * @throws org.springframework.security.authentication.BadCredentialsException si las credenciales son inválidas
     */
    public AuthResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()
                        )
                );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        String token = jwtService.generateToken(authentication);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(token, refreshToken.getToken());
    }

    /**
     * Registra un nuevo usuario en el sistema y genera sus tokens de autenticación.
     * <p>
     * Realiza las siguientes operaciones:
     * <ul>
     *     <li>Convierte el DTO {@link RegisterRequest} a {@link UserCreateDto}</li>
     *     <li>Crea el nuevo usuario mediante {@link UserService}</li>
     *     <li>Genera un token JWT para el usuario registrado</li>
     *     <li>Crea un token de refresco con una validez de 7 días</li>
     *     <li>Devuelve ambos tokens al cliente para autenticación inmediata</li>
     * </ul>
     * <p>
     * Una vez completado el registro, el usuario puede acceder inmediatamente a los recursos
     * protegidos sin necesidad de realizar login adicional.
     *
     * @param registerRequest DTO que contiene los datos de registro (nombre de usuario, email, contraseña)
     * @return {@link AuthResponse} con el token JWT y el token de refresco
     * @throws com.rubiojdev.todolist.shared.exceptions.DuplicateResourceException si el email o nombre de usuario ya existen en el sistema
     */
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {

        UserCreateDto userDto = mapper.toUserCreateDto(registerRequest);

        User user = userService.createNewUser(userDto);

        CustomUserDetails userDetails = new CustomUserDetails(user);

        String token = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(token, refreshToken.getToken());
    }

    /**
     * Renueva la sesión del usuario generando nuevos tokens de autenticación.
     * <p>
     * Realiza las siguientes operaciones:
     * <ul>
     *     <li>Valida el token de refresco proporcionado</li>
     *     <li>Recupera la información del usuario asociado al token</li>
     *     <li>Genera un nuevo token JWT con la identidad del usuario</li>
     *     <li>Crea un nuevo token de refresco con una validez de 7 días</li>
     *     <li>Elimina el token de refresco anterior de la base de datos</li>
     *     <li>Devuelve los nuevos tokens al cliente</li>
     * </ul>
     * <p>
     * Esta operación permite que el usuario mantenga su sesión activa sin necesidad
     * de reintroducir sus credenciales. El token anterior se descarta después de renovar.
     *
     * @param token objeto {@link RefreshTokenRequest} que contiene el token de refresco
     * @return {@link AuthResponse} con el nuevo token JWT y el nuevo token de refresco
     * @throws com.rubiojdev.todolist.shared.exceptions.EntityNotFoundException si el token de refresco no existe
     * @throws com.rubiojdev.todolist.shared.exceptions.InvalidTokenException si el token de refresco está revocado o ha expirado
     */
    public AuthResponse refreshToken(RefreshTokenRequest token) {

        RefreshToken refreshToken = refreshTokenService.findByToken(token.getRefreshToken());

        User user = refreshToken.getUser();

        CustomUserDetails userDetails = new CustomUserDetails(user);

        String newAuthToken = jwtService.generateToken(userDetails);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        refreshTokenService.deleteRefreshToken(refreshToken);

        return new AuthResponse(newAuthToken, newRefreshToken.getToken());
    }

    /**
     * Cierra la sesión del usuario revocando su token de refresco.
     * <p>
     * Realiza la siguiente operación:
     * <ul>
     *     <li>Marca el token de refresco como revocado en la base de datos</li>
     * </ul>
     * <p>
     * Una vez revocado, el token de refresco no podrá ser utilizado para renovar
     * la sesión incluso si aún no ha expirado. El usuario debe volver a introducir
     * sus credenciales si desea acceder nuevamente al sistema.
     * <p>
     * <strong>Nota:</strong> El token JWT sigue siendo válido hasta su expiración.
     * Se recomienda que el cliente descarte localmente sus tokens después del logout.
     *
     * @param refreshToken objeto {@link RefreshTokenRequest} que contiene el token de refresco
     *                      que será revocado
     * @throws com.rubiojdev.todolist.shared.exceptions.EntityNotFoundException si el token de refresco no existe en la base de datos
     */
    public void logout(RefreshTokenRequest refreshToken) {
        refreshTokenService.revokeToken(refreshToken.getRefreshToken());
    }
}
