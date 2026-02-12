package com.example.dropout.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime; // CHANGED from LocalDate

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String fullName;
    private String role; // "PRINCIPAL" or "TEACHER"
    private String school; 
    private String assignedClass; // e.g., "10th A" or "N/A"
    
    // CHANGED: Use LocalDateTime to store Time too
    private LocalDateTime lastLogin; 

    public User() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getSchool() { return school; }
    public void setSchool(String school) { this.school = school; }
    public String getAssignedClass() { return assignedClass; }
    public void setAssignedClass(String assignedClass) { this.assignedClass = assignedClass; }
    
    // FIXED GETTER/SETTER
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
}