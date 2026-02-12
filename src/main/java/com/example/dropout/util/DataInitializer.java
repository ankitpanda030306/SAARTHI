package com.example.dropout.util;

import com.example.dropout.entity.GovernmentScheme;
import com.example.dropout.entity.Student;
import com.example.dropout.entity.User;
import com.example.dropout.repository.GovernmentSchemeRepository;
import com.example.dropout.repository.StudentRepository;
import com.example.dropout.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final GovernmentSchemeRepository schemeRepository; // NEW
    private final Random random = new Random();
    private final String SCHOOL_NAME = "Government High School";

    public DataInitializer(UserRepository userRepository, StudentRepository studentRepository, GovernmentSchemeRepository schemeRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.schemeRepository = schemeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("principal").isEmpty()) generatePrincipal();
        if (userRepository.count() < 10) generateTeachers();
        if (studentRepository.count() < 100) generateStudents();
        if (schemeRepository.count() == 0) generateSchemes(); // NEW
    }

    private void generatePrincipal() {
        User principal = new User();
        principal.setFullName("Amitabh Ray");
        principal.setUsername("principal");
        principal.setPassword("admin123");
        principal.setRole("PRINCIPAL");
        principal.setSchool(SCHOOL_NAME);
        principal.setAssignedClass("N/A");
        principal.setLastLogin(LocalDateTime.now());
        userRepository.save(principal);
    }

    private void generateTeachers() {
        String[] names = {"Ramesh Sahoo", "Sita Nayak", "Gopal Mohanty", "Lakshmi Behera", "Bikash Das"};
        String[] classes = {"8th", "9th", "10th"};
        String[] sections = {"A", "B"};
        int count = 0;
        for(String name : names) {
            User teacher = new User();
            teacher.setFullName(name);
            teacher.setUsername("teacher" + (count + 1));
            teacher.setPassword("pass123");
            teacher.setRole("TEACHER");
            teacher.setSchool(SCHOOL_NAME);
            teacher.setAssignedClass(classes[random.nextInt(classes.length)] + " " + sections[random.nextInt(sections.length)]);
            teacher.setLastLogin(LocalDateTime.now().minusHours(random.nextInt(48))); 
            userRepository.save(teacher);
            count++;
        }
    }

    private void generateStudents() {
        String[] boys = {"Rahul", "Amit", "Sumit", "Ajay", "Bijay"};
        String[] girls = {"Pooja", "Riya", "Soni", "Mamata", "Gita"};
        String[] surnames = {"Sahoo", "Nayak", "Mohanty", "Behera", "Das"};
        
        int count = 0;
        while (count < 450) {
            boolean isBoy = random.nextBoolean();
            String fName = isBoy ? boys[random.nextInt(boys.length)] : girls[random.nextInt(girls.length)];
            String fullName = fName + " " + surnames[random.nextInt(surnames.length)];

            // Smart Factors
            double income = 15000 + random.nextInt(200000); // Some low income, some high
            int stress = 1 + random.nextInt(10);
            int feeDelay = random.nextInt(60);
            
            double attendance = 40 + random.nextInt(60);
            double marks = 30 + random.nextInt(70);

            // Risk Logic
            String risk = "Low";
            if (marks < 40 || attendance < 60 || feeDelay > 45 || stress > 8) {
                risk = random.nextBoolean() ? "High" : "Critical";
            } else if (feeDelay > 30 || stress > 6) {
                risk = "Medium";
            }

            Student s = new Student();
            s.setFullName(fullName);
            s.setSchoolName(SCHOOL_NAME);
            s.setCurrentClass((8 + random.nextInt(3)) + "th");
            s.setSection(random.nextBoolean() ? "A" : "B");
            s.setRollNumber(String.valueOf(count + 1));
            s.setGender(isBoy ? "Male" : "Female");
            s.setGuardianName(surnames[random.nextInt(surnames.length)] + " Guardian");
            s.setContactNumber("9876543" + (100 + count));
            
            s.setAttendance(attendance);
            s.setMathScore(marks);
            s.setFamilyIncome(income);
            s.setFeeDelayDays(feeDelay);
            s.setStressScore(stress);
            s.setRiskLevel(risk);

            studentRepository.save(s);
            count++;
        }
        System.out.println("✅ Data Generated.");
    }

    // --- NEW: SCHEME GENERATION ---
    private void generateSchemes() {
        schemeRepository.save(new GovernmentScheme(
            "Pre-Matric Scholarship", 
            "Financial aid for students from minority communities.", 
            "Income < 1 Lakh/Year", 
            "Rs. 10,000 / Year", 
            "https://scholarships.gov.in"
        ));
        schemeRepository.save(new GovernmentScheme(
            "Begum Hazrat Mahal Scholarship", 
            "Scholarship for meritorious girl students.", 
            "Girl Child Only", 
            "Rs. 6,000 / Year", 
            "https://bhmnsmaef.org"
        ));
        schemeRepository.save(new GovernmentScheme(
            "PM YASASWI Scheme", 
            "Scholarship for OBC, EBC and DNT students.", 
            "Income < 2.5 Lakh/Year", 
            "Rs. 75,000 / Year", 
            "https://yet.nta.ac.in"
        ));
        schemeRepository.save(new GovernmentScheme(
            "National Means-cum-Merit", 
            "For students who drop out due to economic weakness.", 
            "Income < 3.5 Lakh & Marks > 55%", 
            "Rs. 12,000 / Year", 
            "https://dsel.education.gov.in/nmmss"
        ));
        System.out.println("✅ Schemes Generated.");
    }
}