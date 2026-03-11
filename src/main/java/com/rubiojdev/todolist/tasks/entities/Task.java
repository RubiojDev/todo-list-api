package com.rubiojdev.todolist.tasks.entities;

import com.rubiojdev.todolist.taskitems.entities.TaskItem;
import com.rubiojdev.todolist.users.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una tarea perteneciente a un {@link User}.
 *
 * <p>Una {@code Task} corresponde a una unidad principal de trabajo dentro
 * del sistema. Cada tarea pertenece a un usuario y puede contener múltiples
 * {@link TaskItem} que representan subtareas o pasos individuales.</p>
 *
 * <p>El sistema garantiza que cada usuario no pueda tener dos tareas
 * con el mismo nombre mediante una restricción de unicidad compuesta
 * sobre los campos {@code user_id} y {@code name}.</p>
 *
 * <p>La entidad también mantiene información temporal sobre su creación
 * y última actualización mediante los campos {@code createdAt} y
 * {@code updatedAt}, los cuales se gestionan automáticamente mediante
 * métodos de ciclo de vida de persistencia.</p>
 *
 * Relaciones:
 * <ul>
 *     <li>{@code ManyToOne} con {@link User}: cada tarea pertenece a un usuario.</li>
 *     <li>{@code OneToMany} con {@link TaskItem}: una tarea puede contener múltiples subtareas.</li>
 * </ul>
 *
 * Comportamiento:
 * <ul>
 *     <li>Las subtareas se gestionan mediante los métodos {@link #addTaskItem(TaskItem)}
 *     y {@link #removeTaskItem(TaskItem)} para mantener la consistencia de la relación.</li>
 *     <li>Las subtareas asociadas se eliminan automáticamente cuando la tarea es eliminada
 *     gracias a {@code CascadeType.ALL} y {@code orphanRemoval=true}.</li>
 * </ul>
 *
 * Atributos de la entidad:
 * <ul>
 *     <li>{@code id}: identificador único de la tarea.</li>
 *     <li>{@code user}: usuario propietario de la tarea.</li>
 *     <li>{@code name}: nombre de la tarea.</li>
 *     <li>{@code createdAt}: fecha y hora de creación de la tarea.</li>
 *     <li>{@code updatedAt}: fecha y hora de la última actualización.</li>
 *     <li>{@code completed}: indica si la tarea ha sido completada.</li>
 *     <li>{@code taskItems}: lista de subtareas asociadas.</li>
 * </ul>
 *
 * @see User
 * @see TaskItem
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "tasks",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "name"})
})
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NonNull
    @Column(nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean completed;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskItem> taskItems = new ArrayList<>();

    /**
     * Agrega una subtarea a la tarea y mantiene sincronizada
     * la relación bidireccional entre {@link Task} y {@link TaskItem}.
     *
     * @param taskItem subtarea a asociar con la tarea
     */
    public void addTaskItem(TaskItem taskItem) {
        this.taskItems.add(taskItem);
        taskItem.setTask(this);
    }

    /**
     * Elimina una subtarea de la tarea y actualiza la relación bidireccional.
     *
     * <p>Si {@code orphanRemoval=true} está habilitado, la subtarea será
     * eliminada automáticamente de la base de datos cuando la entidad
     * sea persistida.</p>
     *
     * @param taskItem subtarea a eliminar
     */
    public void removeTaskItem(TaskItem taskItem) {
        this.taskItems.remove(taskItem);
        taskItem.setTask(null);
    }

    /**
     * Inicializa los valores por defecto antes de persistir la entidad.
     *
     * <p>Establece la fecha de creación y marca la tarea como no completada.</p>
     */
    @PrePersist
    private void onCreated() {
        this.createdAt = LocalDateTime.now();
        this.completed = false;
    }

    /**
     * Actualiza la fecha de modificación cada vez que la entidad es actualizada.
     */
    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
