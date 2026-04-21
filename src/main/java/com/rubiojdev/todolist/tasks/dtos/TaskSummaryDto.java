package com.rubiojdev.todolist.tasks.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskSummaryDto {

    private Long id;
    private String name;
    private boolean completed;
    private Instant updatedAt;

    private long totalSubTasks;
    private long pendingSubTasks;
}
