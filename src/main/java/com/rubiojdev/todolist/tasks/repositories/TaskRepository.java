package com.rubiojdev.todolist.tasks.repositories;

import com.rubiojdev.todolist.tasks.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
//Agrupar las querys por simple querys y las fetch querys

    Page<Task> findAllByUserIdOrderByUpdatedAtDesc(
            Long userId,
            Pageable pageable
    );

    @Query("""
            SELECT DISTINCT t
            FROM Task t
            LEFT JOIN FETCH t.taskItems ti
            WHERE t.user.id = :userId
            AND t.id = :id
            """)
    Optional<Task> findTaskWithItemsByIdAndUserId(
            @Param("userId") Long userId,
            @Param("id") Long id
    );

    Optional<Task> findByIdAndUserId(Long id, Long userId);

    Page<Task> findAllByNameContainingIgnoreCaseAndUserId(
            String name,
            Long userId,
            Pageable pageable
    );

    boolean existsByNameIgnoreCaseAndUserId(String name, Long userId);

    boolean existsByNameIgnoreCaseAndUserIdAndIdNot(String name, Long userId, Long id);

}