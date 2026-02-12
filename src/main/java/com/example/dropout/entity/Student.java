package com.example.dropout.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String schoolName;
    private String currentClass;
    private String section;
    private String rollNumber;
    private String gender;
    private LocalDate dob;
    private String guardianName;
    private String contactNumber;
    private String address;

    // Academic
    private double mathScore;  
    private int backlogs;
    private boolean failedHalfYearly; // NEW FIELD for "Failed Both" logic

    // Attendance & Smart Factors
    private double attendance; 
    private int feeDelayDays;
    private double familyIncome;
    private int stressScore;

    private String riskLevel; // Low, Medium, High, Critical

    public Student() {}

    // Getters and Setters
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

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getGuardianName() { return guardianName; }
    public void setGuardianName(String guardianName) { this.guardianName = guardianName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getMathScore() { return mathScore; }
    public void setMathScore(double mathScore) { this.mathScore = mathScore; }

    public int getBacklogs() { return backlogs; }
    public void setBacklogs(int backlogs) { this.backlogs = backlogs; }

    public boolean isFailedHalfYearly() { return failedHalfYearly; }
    public void setFailedHalfYearly(boolean failedHalfYearly) { this.failedHalfYearly = failedHalfYearly; }

    public double getAttendance() { return attendance; }
    public void setAttendance(double attendance) { this.attendance = attendance; }

    public int getFeeDelayDays() { return feeDelayDays; }
    public void setFeeDelayDays(int feeDelayDays) { this.feeDelayDays = feeDelayDays; }

    public double getFamilyIncome() { return familyIncome; }
    public void setFamilyIncome(double familyIncome) { this.familyIncome = familyIncome; }

    public int getStressScore() { return stressScore; }
    public void setStressScore(int stressScore) { this.stressScore = stressScore; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
}