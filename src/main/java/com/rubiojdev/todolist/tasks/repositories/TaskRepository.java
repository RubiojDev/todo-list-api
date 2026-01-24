package com.rubiojdev.todolist.tasks.repositories;

import com.rubiojdev.todolist.tasks.entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
            SELECT DISTINCT t
            FROM Task t
            INNER JOIN FETCH t.taskItems ti
            WHERE t.user.id = :userId
            ORDER BY t.updatedAt DESC
            """)
    List<Task> findAllByUserId(
            @Param("userId") Long userId
    );

    @Query("""
            SELECT DISTINCT t
            FROM Task t
            INNER JOIN FETCH t.taskItems ti
            WHERE t.user.id = :userId
            AND t.id = :id
            """)
    Optional<Task> findByIdAndUser(
            @Param("userId") Long userId,
            @Param("id") Long id
    );

    @Query("""
            SELECT DISTINCT t
            FROM Task t
            INNER JOIN FETCH t.taskItems ti
            WHERE t.user.id = :userId
            AND LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))
            ORDER BY t.updatedAt ASC
            """)
    List<Task> findAllByNameAndUser(
            @Param("userId") Long userId,
            @Param("name") String name
    );

    boolean existsByNameIgnoreCaseAndUserId(String name, Long userId);

    boolean existsByNameIgnoreCaseAndUserIdAndIdNot(String name, Long userId, Long id);

}