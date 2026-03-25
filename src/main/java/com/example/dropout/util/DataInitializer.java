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
    private final GovernmentSchemeRepository schemeRepository;
    private final Random random = new Random();

    public DataInitializer(UserRepository userRepository, StudentRepository studentRepository, GovernmentSchemeRepository schemeRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.schemeRepository = schemeRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) generatePrincipal();
        if (userRepository.count() < 250) generateTeachers();
        if (studentRepository.count() < 2000) generateStudents();
        if (schemeRepository.count() == 0) generateSchemes();
    }

    private void generatePrincipal() {
        User principal = new User();
        principal.setFullName("Amitabh Ray");
        principal.setUsername("principal");
        principal.setPassword("admin123");
        principal.setRole("PRINCIPAL");
        principal.setSchool("Government High School");
        userRepository.save(principal);
    }

    private void generateTeachers() {
        for (int i = 1; i <= 250; i++) {
            User t = new User();
            t.setFullName("Teacher " + i);
            t.setUsername("teacher" + i);
            t.setPassword("pass123");
            t.setRole("TEACHER");
            t.setSchool("Government High School");
            t.setAssignedClass((8 + (i % 5)) + "th " + (i % 2 == 0 ? "A" : "B"));
            userRepository.save(t);
        }
    }

    private void generateStudents() {
        String[] surnames = {"Sahoo", "Nayak", "Mohanty", "Behera", "Das", "Patnaik"};
        for (int i = 1; i <= 2000; i++) {
            Student s = new Student();
            s.setFullName("Student " + i + " " + surnames[random.nextInt(surnames.length)]);
            s.setRollNumber("R-" + i);
            s.setAttendance(40 + random.nextInt(60));
            
            // Generate Trend Data (X-Y Axis Data)
            double t1 = 30 + random.nextInt(60);
            double t2 = t1 + (random.nextInt(20) - 10); // Random growth or decline
            double t3 = t2 + (random.nextInt(20) - 10); // Current Marks
            
            s.setTerm1Score(Math.min(t1, 100));
            s.setTerm2Score(Math.min(t2, 100));
            s.setMathScore(Math.min(t3, 100)); // This is Term 3
            
            s.setFamilyIncome(5000 + random.nextInt(50000));
            s.setStressScore(1 + random.nextInt(10));
            s.setHasInternet(random.nextInt(2));
            s.setRiskLevel(t3 < 40 || s.getAttendance() < 50 ? "Critical" : (t3 < 60 ? "Medium" : "Low"));
            
            studentRepository.save(s);
        }
        System.out.println("✅ 2000 Students with Trend Data Generated.");
    }

    private void generateSchemes() {
        schemeRepository.save(new GovernmentScheme("Pre-Matric Scholarship", "Financial aid for minorities", "Income < 1L", "10,000/yr", "https://scholarships.gov.in"));
        schemeRepository.save(new GovernmentScheme("PM YASASWI", "OBC/EBC Support", "Income < 2.5L", "75,000/yr", "https://yet.nta.ac.in"));
    }
}