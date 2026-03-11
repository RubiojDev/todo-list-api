package com.rubiojdev.todolist.taskitems.entities;

import com.rubiojdev.todolist.tasks.entities.Task;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa una subtarea asociada a una {@link Task}.
 *
 * <p>Un {@code TaskItem} corresponde a una unidad de trabajo más pequeña
 * dentro de una tarea principal, permitiendo dividir una tarea en
 * múltiples pasos o subtareas.</p>
 *
 * <p>Cada subtarea pertenece obligatoriamente a una tarea mediante una
 * relación {@code ManyToOne}. Además, mantiene información básica como
 * el nombre de la subtarea y su estado de finalización.</p>
 *
 * <p>Al momento de ser creada, el estado {@code completed} se inicializa
 * automáticamente en {@code false} mediante el método {@link #onCreated()}.</p>
 *
 * Atributos de la clase:
 * <ul>
 *     <li>{@code id}: Identificador único de la subtarea en la base de datos.</li>
 *     <li>{@code task}: Tarea a la cual pertenece la subtarea.</li>
 *     <li>{@code name}: Nombre de la subtarea.</li>
 *     <li>{@code completed}: Indica si la subtarea ha sido completada.</li>
 * </ul>
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "task_items")
public class TaskItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @NonNull
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean completed;

    /**
     * Inicializa el estado de la subtarea antes de persistirse en la base de datos.
     * <p>
     * Por defecto, toda subtarea nueva se crea como no completada.
     * </p>
     */
    @PrePersist
    private void onCreated() {
        this.completed = false;
    }
}
