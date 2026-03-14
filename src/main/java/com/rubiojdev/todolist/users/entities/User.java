package com.rubiojdev.todolist.users.entities;

import com.rubiojdev.todolist.tasks.entities.Task;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un usuario dentro del sistema.
 *
 * <p>Un {@code User} es el propietario de los recursos gestionados por
 * la aplicación, como las {@link Task}. Cada usuario posee credenciales
 * de autenticación y puede crear múltiples tareas asociadas a su cuenta.</p>
 *
 * <p>El sistema garantiza la unicidad de {@code username} y {@code email}
 * mediante restricciones a nivel de base de datos, evitando la creación
 * de cuentas duplicadas.</p>
 *
 * Relaciones:
 * <ul>
 *     <li>{@code OneToMany} con {@link Task}: un usuario puede tener múltiples tareas.</li>
 * </ul>
 *
 * Comportamiento:
 * <ul>
 *     <li>Las tareas asociadas se gestionan mediante el método
 *     {@link #addTask(Task)} para mantener la consistencia de la relación
 *     bidireccional entre {@link User} y {@link Task}.</li>
 *     <li>Cuando un usuario es eliminado, todas sus tareas asociadas
 *     también se eliminan automáticamente gracias a
 *     {@code CascadeType.ALL} y {@code orphanRemoval=true}.</li>
 * </ul>
 *
 * Atributos de la entidad:
 * <ul>
 *     <li>{@code id}: identificador único del usuario.</li>
 *     <li>{@code username}: nombre de usuario único utilizado para identificar la cuenta.</li>
 *     <li>{@code email}: dirección de correo electrónico única del usuario.</li>
 *     <li>{@code password}: contraseña encriptada del usuario.</li>
 *     <li>{@code createdAt}: fecha y hora de creación de la cuenta.</li>
 *     <li>{@code tasks}: lista de tareas pertenecientes al usuario.</li>
 * </ul>
 *
 * @see Task
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @NonNull
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NonNull
    @Column(nullable = false)
    private String password;

    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();

    /**
     * Asocia una tarea al usuario y mantiene sincronizada
     * la relación bidireccional entre {@link User} y {@link Task}.
     *
     * @param task tarea a agregar al usuario
     */
    public void addTask(Task task) {
        this.tasks.add(task);
        task.setUser(this);
    }

    /**
     * Inicializa los valores por defecto antes de persistir el usuario.
     *
     * <p>Establece automáticamente la fecha de creación de la cuenta.</p>
     */
    @PrePersist
    private void onCreated() {
        this.createdAt = Instant.now();
    }
}
