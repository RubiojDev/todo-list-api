package com.rubiojdev.todolist.auth.entities;

import com.rubiojdev.todolist.users.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Representa un token de refresco utilizado en el sistema de autenticación.
 * <p>
 * Los refresh tokens permiten renovar un token de acceso (JWT) sin que el
 * usuario tenga que volver a autenticarse con sus credenciales.
 * Cada token está asociado a un {@link User} y tiene una fecha de expiración
 * después de la cual deja de ser válido.
 * <p>
 * Además, un token puede ser marcado como revocado para impedir su uso incluso
 * si aún no ha expirado. Esto permite invalidar sesiones en situaciones como:
 * <ul>
 *     <li>Cierre de sesión del usuario</li>
 *     <li>Rotación de tokens durante el proceso de renovación</li>
 *     <li>Eventos de seguridad como cambio de contraseña o sospecha de compromiso</li>
 * </ul>
 * <p>
 * Los tokens se almacenan en la tabla {@code refresh_tokens}.
 * Atributos de la clase:
 * <ul>
 *     <li>{@code id}: Identificador único del token en la base de datos.</li>
 *     <li>{@code token}: Valor único del token de refresco utilizado por el cliente
 *                          para solicitar nuevos tokens de acceso</li>
 *     <li>{@code expiryDate}: Fecha y hora en la que el token deja de ser válido.</li>
 *     <li>{@code revoked}: Indica si el token ha sido revocado manualmente.
 *                          Un token revocado no puede utilizarse para renovar autenticación.</li>
 *     <li>{@code user}: Usuario al cual pertenece el token de refresco.<br>
 *                          La relación es muchos-a-uno, ya que un usuario puede tener
 *                          múltiples tokens activos dependiendo de las sesiones abiertas.</li>
 * </ul>
 *
 * @see User
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false, unique = true)
    private String token;

    @NonNull
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Método ejecutado antes de persistir la entidad.
     * <p>
     * Inicializa el estado del token como no revocado al momento
     * de su creación.
     */
    @PrePersist
    private void onCreated() {
        this.revoked = false;
    }
}
