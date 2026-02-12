package com.example.dropout.ui;

import com.example.dropout.entity.User;
import com.example.dropout.repository.UserRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import java.time.LocalDateTime;

@Route("signup")
@AnonymousAllowed
public class SignupView extends VerticalLayout { // CHECK THIS: Matches file name 'SignupView.java'

    public SignupView(UserRepository userRepository) {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-color", "#f5f5f5");

        VerticalLayout card = new VerticalLayout();
        card.setWidth("400px");
        card.setPadding(true);
        card.setAlignItems(Alignment.CENTER);
        card.getStyle().set("background", "white")
                       .set("border-radius", "10px")
                       .set("box-shadow", "0 4px 12px rgba(0,0,0,0.1)");

        H2 title = new H2("SAARTHI Registration");
        title.getStyle().set("color", "#2c3e50");

        TextField fullName = new TextField("Full Name");
        fullName.setWidthFull();
        
        TextField username = new TextField("Username");
        username.setWidthFull();
        
        PasswordField password = new PasswordField("Password");
        password.setWidthFull();
        
        ComboBox<String> roleSelect = new ComboBox<>("Role");
        roleSelect.setItems("PRINCIPAL", "TEACHER");
        roleSelect.setWidthFull();
        
        TextField schoolName = new TextField("School Name");
        schoolName.setWidthFull();
        
        TextField assignedClass = new TextField("Class (if Teacher)");
        assignedClass.setWidthFull();

        Button registerBtn = new Button("Create Account");
        registerBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerBtn.setWidthFull();

        registerBtn.addClickListener(e -> {
            if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || roleSelect.isEmpty() || schoolName.isEmpty()) {
                Notification.show("Please fill all required fields").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            if (userRepository.findByUsername(username.getValue()).isPresent()) {
                Notification.show("Username already exists!").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            User newUser = new User();
            newUser.setUsername(username.getValue());
            newUser.setPassword(password.getValue());
            newUser.setRole(roleSelect.getValue());
            newUser.setSchool(schoolName.getValue());
            newUser.setFullName(fullName.getValue());
            
            if (assignedClass.isEmpty() || "PRINCIPAL".equals(roleSelect.getValue())) {
                newUser.setAssignedClass("N/A");
            } else {
                newUser.setAssignedClass(assignedClass.getValue());
            }
            
            newUser.setLastLogin(LocalDateTime.now());

            userRepository.save(newUser);
            
            Notification.show("Account Created Successfully!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            UI.getCurrent().navigate("login");
        });

        Button loginLink = new Button("Back to Login", e -> UI.getCurrent().navigate("login"));
        loginLink.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        card.add(title, fullName, username, password, roleSelect, schoolName, assignedClass, registerBtn, loginLink);
        add(card);
    }
}