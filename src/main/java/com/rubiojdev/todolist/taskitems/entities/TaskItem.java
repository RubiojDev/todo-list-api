package com.rubiojdev.todolist.taskitems.entities;

import com.rubiojdev.todolist.tasks.entities.Task;
import jakarta.persistence.*;
import lombok.*;

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

    @PrePersist
    private void onCreated() {
        this.completed = false;
    }
}
