package com.rubiojdev.todolist.taskitems.repositories;

import com.rubiojdev.todolist.taskitems.entities.TaskItem;
import com.rubiojdev.todolist.users.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskItemRepository extends JpaRepository<TaskItem,  Long> {

    Page<TaskItem> findByTaskIdAndTaskUserOrderByIdAsc(Long taskId, User user, Pageable pageable);

    Optional<TaskItem> findTaskItemByIdAndTaskIdAndTaskUser(Long id, Long taskId, User user);
}
