package com.example.dropout.ui;

import com.example.dropout.entity.Student;
import com.example.dropout.service.StudentService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.html.H2;
import jakarta.annotation.security.PermitAll;

@Route("students")
@PermitAll
public class StudentView extends VerticalLayout {

    public StudentView(StudentService studentService) {
        setSizeFull();
        setPadding(true);

        H2 title = new H2("Full Student Database (Raw Data)");

        Grid<Student> grid = new Grid<>(Student.class, false);
        grid.addColumn(Student::getFullName).setHeader("Name");
        grid.addColumn(Student::getCurrentClass).setHeader("Class");
        
        // Smart Data Columns
        grid.addColumn(Student::getFeeDelayDays).setHeader("Fee Delay");
        grid.addColumn(Student::getStressScore).setHeader("Stress Score");
        grid.addColumn(Student::getFamilyIncome).setHeader("Income");
        
        grid.addColumn(Student::getRiskLevel).setHeader("Risk Level");

        grid.setItems(studentService.findAllStudents());

        add(title, grid);
    }
}