package com.example.dropout.repository;

import com.example.dropout.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // Import this!

public interface UserRepository extends JpaRepository<User, Long> {
    // This MUST return Optional<User>
    Optional<User> findByUsername(String username);
}