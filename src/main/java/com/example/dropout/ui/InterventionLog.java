package com.example.dropout.ui;

import java.time.LocalDateTime;

public class InterventionLog {
    private String studentName;
    private String action;
    private LocalDateTime timestamp;

    public InterventionLog(String studentName, String action, LocalDateTime timestamp) {
        this.studentName = studentName;
        this.action = action;
        this.timestamp = timestamp;
    }

    public String getStudentName() { return studentName; }
    public String getAction() { return action; }
    public LocalDateTime getTimestamp() { return timestamp; }
}