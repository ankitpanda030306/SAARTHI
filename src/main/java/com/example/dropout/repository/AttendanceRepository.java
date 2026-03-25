package com.example.dropout.repository;
import com.example.dropout.entity.Attendance;
import com.example.dropout.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {}