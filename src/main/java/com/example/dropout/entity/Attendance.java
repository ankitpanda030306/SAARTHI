package com.example.dropout.entity;

import jakarta.persistence.*;

@Entity
public class Attendance {


        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private double attendancePercentage;

        @ManyToOne
        private Student student;
    }

