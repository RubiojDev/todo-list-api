package com.rubiojdev.todolist.tasks.entities;

import com.rubiojdev.todolist.taskitems.entities.TaskItem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NonNull
    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean completed;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskItem> taskItems = new ArrayList<>();

    public void addTaskItem(TaskItem taskItem) {
        this.taskItems.add(taskItem);
        taskItem.setTask(this);
    }

    public void removeTaskItem(TaskItem taskItem) {
        this.taskItems.remove(taskItem);
        taskItem.setTask(null);
    }

    @PrePersist
    private void onCreated() {
        this.createdAt = LocalDateTime.now();
        this.completed = false;
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
