package com.example.dropout.repository;
import com.example.dropout.entity.Prediction;
import com.example.dropout.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PredictionRepository extends JpaRepository<Prediction, Long> {}
