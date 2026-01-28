package com.rubiojdev.todolist.taskitems.repositories;

import com.rubiojdev.todolist.taskitems.entities.TaskItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskItemRepository extends JpaRepository<TaskItem,  Long> {

    List<TaskItem> findByTaskIdAndTask_User_Id(Long taskId, Long userId);

    Optional<TaskItem> findTaskItemByIdAndTaskIdAndTaskUserId(Long id, Long taskId, Long userId);
}
