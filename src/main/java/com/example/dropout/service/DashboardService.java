package com.example.dropout.service;

import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    public String getStudentStatus() {
        // later this will come from DB / ML model
        return "Student is currently ACTIVE";
    }
}

