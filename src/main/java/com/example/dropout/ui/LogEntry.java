package com.example.dropout.ui;

import java.time.LocalDateTime;

public class LogEntry {
    private String actor;
    private String action;
    private LocalDateTime timestamp;

    public LogEntry(String actor, String action, LocalDateTime timestamp) {
        this.actor = actor;
        this.action = action;
        this.timestamp = timestamp;
    }

    public String getActor() { return actor; }
    public String getAction() { return action; }
    public LocalDateTime getTimestamp() { return timestamp; }
}