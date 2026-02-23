package com.rubiojdev.todolist.users.repositories;

import com.rubiojdev.todolist.users.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsernameIgnoreCaseOrEmailIgnoreCase(String username, String email);

    Optional<User> findByEmail(String email);
}
