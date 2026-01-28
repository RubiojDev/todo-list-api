package com.rubiojdev.todolist.taskitems.repositories;

import com.rubiojdev.todolist.taskitems.entities.TaskItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskItemRepository extends JpaRepository<TaskItem,  Long> {

    Page<TaskItem> findByTaskIdAndTaskUserIdOrderByIdAsc(Long taskId, Long userId, Pageable pageable);

    Optional<TaskItem> findTaskItemByIdAndTaskIdAndTaskUserId(Long id, Long taskId, Long userId);
}
