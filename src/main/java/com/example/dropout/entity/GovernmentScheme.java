package com.example.dropout.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class GovernmentScheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String schemeName;
    private String description;
    private String eligibilityCriteria; // e.g., "Income < 1L", "Female"
    private String benefit; // e.g., "Rs. 10,000/year"
    private String applicationLink;

    public GovernmentScheme() {}

    public GovernmentScheme(String schemeName, String description, String eligibility, String benefit, String link) {
        this.schemeName = schemeName;
        this.description = description;
        this.eligibilityCriteria = eligibility;
        this.benefit = benefit;
        this.applicationLink = link;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getSchemeName() { return schemeName; }
    public void setSchemeName(String schemeName) { this.schemeName = schemeName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEligibilityCriteria() { return eligibilityCriteria; }
    public void setEligibilityCriteria(String eligibilityCriteria) { this.eligibilityCriteria = eligibilityCriteria; }
    public String getBenefit() { return benefit; }
    public void setBenefit(String benefit) { this.benefit = benefit; }
    public String getApplicationLink() { return applicationLink; }
    public void setApplicationLink(String applicationLink) { this.applicationLink = applicationLink; }
}