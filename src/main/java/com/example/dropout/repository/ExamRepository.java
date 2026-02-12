package com.example.dropout.repository;

import com.example.dropout.entity.Exam;
import com.example.dropout.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ExamRepository extends JpaRepository<Exam, Long> {}
