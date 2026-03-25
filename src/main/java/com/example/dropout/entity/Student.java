package com.example.dropout.entity;

import jakarta.persistence.*;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Basic Info
    private String fullName;
    private String schoolName;
    private String currentClass;
    private String section;
    private String rollNumber;
    private String gender;
    private String guardianName;
    private String contactNumber;
    // Add these Getters and Setters inside Student.java
public double getTerm1Score() { return term1Score; }
public void setTerm1Score(double term1Score) { this.term1Score = term1Score; }

public double getTerm2Score() { return term2Score; }
public void setTerm2Score(double term2Score) { this.term2Score = term2Score; }

    // Academic & Risk Factors
    private double attendance;
    private double mathScore;
    private double familyIncome;
    private int feeDelayDays;
    private int stressScore; 
    private String riskLevel; 
    // Add these to Student.java
    private double term1Score;
    private double term2Score;
// mathScore will act as Term 3
    
    // Logic Fields (Required by StudentService and MySQL)
    private boolean failedHalfYearly = false;
    private int hasInternet; // 1 for Yes, 0 for No (Fixes your MySQL Error)

    // --- MANUAL GETTERS AND SETTERS (No Lombok) ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public String getCurrentClass() { return currentClass; }
    public void setCurrentClass(String currentClass) { this.currentClass = currentClass; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getGuardianName() { return guardianName; }
    public void setGuardianName(String guardianName) { this.guardianName = guardianName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public double getAttendance() { return attendance; }
    public void setAttendance(double attendance) { this.attendance = attendance; }

    public double getMathScore() { return mathScore; }
    public void setMathScore(double mathScore) { this.mathScore = mathScore; }

    public double getFamilyIncome() { return familyIncome; }
    public void setFamilyIncome(double familyIncome) { this.familyIncome = familyIncome; }

    public int getFeeDelayDays() { return feeDelayDays; }
    public void setFeeDelayDays(int feeDelayDays) { this.feeDelayDays = feeDelayDays; }

    public int getStressScore() { return stressScore; }
    public void setStressScore(int stressScore) { this.stressScore = stressScore; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public boolean isFailedHalfYearly() { return failedHalfYearly; }
    public void setFailedHalfYearly(boolean failedHalfYearly) { this.failedHalfYearly = failedHalfYearly; }

    public int getHasInternet() { return hasInternet; }
    public void setHasInternet(int hasInternet) { this.hasInternet = hasInternet; }

    // --- HELPER METHODS FOR DASHBOARD GRAPHS ---
    public double getEconomicStability() {
        return Math.min((this.familyIncome / 55000) * 100, 100);
    }

    public double getWellBeingScore() {
        return (11 - this.stressScore) * 10;
    }
}