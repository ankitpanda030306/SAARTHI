package com.example.dropout.service;

import com.example.dropout.entity.Student;
import com.example.dropout.entity.TeacherComplaint;
import com.example.dropout.repository.StudentRepository;
import com.example.dropout.repository.TeacherComplaintRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final TeacherComplaintRepository complaintRepository;

    public StudentService(StudentRepository studentRepository, TeacherComplaintRepository complaintRepository) {
        this.studentRepository = studentRepository;
        this.complaintRepository = complaintRepository;
    }

    public List<Student> findAllStudents() { return studentRepository.findAll(); }
    public List<Student> findBySchool(String schoolName) { return studentRepository.findBySchoolName(schoolName); }
    public void saveStudent(Student s) { studentRepository.save(s); }

    // --- COMPLAINTS & INTERVENTION ---
    
    public void submitComplaint(String teacher, String student, String message) {
        if(student == null || message == null) return;
        complaintRepository.save(new TeacherComplaint(teacher, student, message));
    }

    public List<TeacherComplaint> getUnreadComplaints() { 
        return complaintRepository.findByIsReadFalse(); 
    }

    // THIS WAS MISSING -> FIXING THE ERROR
    public void markComplaintAsRead(TeacherComplaint c) {
        c.setRead(true);
        complaintRepository.save(c);
    }

    public List<TeacherComplaint> getAllComplaints() {
        return complaintRepository.findAll();
    }
    
    // --- SMS & COUNSELING STUBS ---
    public void sendSmsToParent(Student s, String message) {
        System.out.println("SMS sent to " + s.getContactNumber() + ": " + message);
    }

    public void scheduleCounseling(Student s, LocalDateTime date) {
        System.out.println("Counseling scheduled for " + s.getFullName() + " on " + date);
    }

    // --- ACADEMIC & RISK LOGIC ---
    public void updateStudentAcademic(Student s, String examType, double marks, double attendance) {
        s.setMathScore(marks);
        s.setAttendance(attendance);

        if ("Half Yearly".equals(examType)) {
            if (marks < 40) s.setFailedHalfYearly(true);
            else s.setFailedHalfYearly(false);
        }

        String newRisk = "Low";
        // Logic: Failed both Half Yearly and Annual -> CRITICAL
        if ("Annual".equals(examType) && marks < 40 && s.isFailedHalfYearly()) {
            newRisk = "Critical";
        } else if (marks < 35 || attendance < 50 || s.getStressScore() > 8) {
            newRisk = "Critical";
        } else if (marks < 50 || attendance < 70 || s.getFeeDelayDays() > 45) {
            newRisk = "High";
        } else if (marks < 60 || s.getFeeDelayDays() > 30) {
            newRisk = "Medium";
        }

        s.setRiskLevel(newRisk);
        studentRepository.save(s);
    }

    public String getRiskReason(Student s) {
        if ("Low".equals(s.getRiskLevel())) return "Student is performing well.";
        StringBuilder reason = new StringBuilder();
        
        if (s.isFailedHalfYearly() && s.getMathScore() < 40) reason.append("• Critical: Failed both Half Yearly & Annual. ");
        if (s.getMathScore() < 40) reason.append("• Failing Marks. ");
        if (s.getAttendance() < 60) reason.append("• Low Attendance. ");
        if (s.getFeeDelayDays() > 30) reason.append("• Fees overdue. ");
        if (s.getStressScore() > 7) reason.append("• High Stress. ");
        return reason.toString();
    }

    public String getSuggestedIntervention(Student s) {
        if (s.isFailedHalfYearly() && s.getMathScore() < 40) return "Urgent Parent Meeting (Retain Year)";
        if (s.getFeeDelayDays() > 30) return "Discuss Scholarship";
        if (s.getStressScore() > 7) return "Counselor Meeting";
        if (s.getMathScore() < 40) return "Remedial Classes";
        if (s.getAttendance() < 60) return "Home Visit";
        return "Monitor Weekly";
    }
}