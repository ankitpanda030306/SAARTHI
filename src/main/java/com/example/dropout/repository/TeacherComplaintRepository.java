package com.example.dropout.repository;

import com.example.dropout.entity.TeacherComplaint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeacherComplaintRepository extends JpaRepository<TeacherComplaint, Long> {
    List<TeacherComplaint> findByIsReadFalse(); // To find unread alerts
}