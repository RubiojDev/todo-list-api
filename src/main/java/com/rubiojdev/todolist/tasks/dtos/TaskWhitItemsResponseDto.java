package com.rubiojdev.todolist.tasks.dtos;

import com.rubiojdev.todolist.taskitems.dtos.TaskItemResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskWhitItemsResponseDto {

    private Long id;

    private String name;

    private LocalDateTime updatedAt;

    private boolean completed;

    private List<TaskItemResponseDto> taskItemDtoList;
}
