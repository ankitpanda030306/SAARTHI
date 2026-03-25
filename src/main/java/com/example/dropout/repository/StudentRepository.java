package com.example.dropout.repository;

import com.example.dropout.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    
    // --- THIS IS THE MISSING METHOD CAUSING THE ERROR ---
    List<Student> findBySchoolName(String schoolName);
}