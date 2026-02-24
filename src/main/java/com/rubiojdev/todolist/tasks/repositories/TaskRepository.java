package com.rubiojdev.todolist.tasks.repositories;

import com.rubiojdev.todolist.tasks.entities.Task;
import com.rubiojdev.todolist.users.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
            SELECT DISTINCT t
            FROM Task t
            LEFT JOIN FETCH t.taskItems ti
            WHERE t.user = :user
            AND t.id = :id
            """)
    Optional<Task> findTaskWithItemsByIdAndUser(
            @Param("user") User user,
            @Param("id") Long id
    );

    Optional<Task> findByIdAndUser(Long id, User user);

    Page<Task> findAllByUserOrderByUpdatedAtDesc(
            User user,
            Pageable pageable
    );

    Page<Task> findAllByNameContainingIgnoreCaseAndUser(
            String name,
            User user,
            Pageable pageable
    );

    boolean existsByNameIgnoreCaseAndUser(String name, User user);

    boolean existsByNameIgnoreCaseAndUserAndIdNot(String name, User user, Long id);

}