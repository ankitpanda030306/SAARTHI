package com.example.dropout.service;

import com.example.dropout.entity.GovernmentScheme;
import com.example.dropout.entity.Student;
import com.example.dropout.repository.GovernmentSchemeRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class SchemeService {

    private final GovernmentSchemeRepository repository;

    public SchemeService(GovernmentSchemeRepository repository) {
        this.repository = repository;
    }

    public List<GovernmentScheme> findAll() {
        return repository.findAll();
    }

    // --- SMART RECOMMENDATION ENGINE ---
    public List<GovernmentScheme> getRecommendedSchemes(Student s) {
        List<GovernmentScheme> all = repository.findAll();
        List<GovernmentScheme> recommended = new ArrayList<>();

        for (GovernmentScheme scheme : all) {
            boolean isMatch = false;

            // 1. Check Income Eligibility (Assuming schemes mentioning "Income" require < 1.5L)
            if (scheme.getEligibilityCriteria().contains("Income")) {
                if (s.getFamilyIncome() < 150000) isMatch = true;
            }

            // 2. Check Gender Eligibility
            if (scheme.getEligibilityCriteria().contains("Girl") || scheme.getEligibilityCriteria().contains("Female")) {
                if ("Female".equalsIgnoreCase(s.getGender())) isMatch = true;
                else isMatch = false; // If it's a girl scheme but student is male, reject
            }

            // 3. Default: If generic, add it
            if (!scheme.getEligibilityCriteria().contains("Income") && !scheme.getEligibilityCriteria().contains("Girl")) {
                isMatch = true;
            }

            if (isMatch) recommended.add(scheme);
        }
        return recommended;
    }
}