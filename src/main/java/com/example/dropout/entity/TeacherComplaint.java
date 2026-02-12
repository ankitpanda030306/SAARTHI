package com.example.dropout.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class TeacherComplaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String teacherName;
    private String studentName;
    private String complaintText;
    private boolean isRead = false;
    private LocalDateTime timestamp;

    public TeacherComplaint() {
        this.timestamp = LocalDateTime.now();
    }

    public TeacherComplaint(String teacherName, String studentName, String complaintText) {
        this.teacherName = teacherName;
        this.studentName = studentName;
        this.complaintText = complaintText;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getComplaintText() { return complaintText; }
    public void setComplaintText(String complaintText) { this.complaintText = complaintText; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public LocalDateTime getTimestamp() { return timestamp; }
}