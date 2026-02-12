package com.example.dropout.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentName;
    private String message;
    private String severity; // "CRITICAL" or "INFO"
    private boolean isRead;
    private LocalDateTime timestamp;

    public Alert() {}

    public Alert(String studentName, String message, String severity) {
        this.studentName = studentName;
        this.message = message;
        this.severity = severity;
        this.isRead = false;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getStudentName() { return studentName; }
    public String getMessage() { return message; }
    public String getSeverity() { return severity; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public LocalDateTime getTimestamp() { return timestamp; }
}