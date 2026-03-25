package com.example.dropout.ui;

import com.example.dropout.entity.User;
import com.example.dropout.service.AuthService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("login")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    public LoginView(AuthService authService) {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        
        // Set a light background color for the whole page
        getStyle().set("background-color", "#f5f5f5");

        // Create a Card Layout for the Login Box
        VerticalLayout loginCard = new VerticalLayout();
        loginCard.setWidth("400px");
        loginCard.setAlignItems(Alignment.CENTER);
        loginCard.setPadding(true);
        loginCard.setSpacing(true);
        loginCard.getStyle()
                .set("background", "white")
                .set("border-radius", "10px")
                .set("box-shadow", "0 4px 12px rgba(0,0,0,0.1)");

        // 1. Logo (Make sure 'logo.png' is in src/main/resources/META-INF/resources/images/)
        // If you don't have the image yet, it will just show the alt text "SAARTHI Logo"
        Image logo = new Image("images/logo.png", "SAARTHI Logo");
        logo.setHeight("100px");

        // 2. App Name & Slogan
        H1 appName = new H1("SAARTHI");
        appName.getStyle().set("color", "#2c3e50"); // Dark Blue
        
        H4 slogan = new H4("Nipping dropouts in the bud.");
        slogan.getStyle().set("color", "#7f8c8d"); // Grey

        // 3. Login Form
        LoginForm loginForm = new LoginForm();
        loginForm.addLoginListener(e -> {
            try {
                User user = authService.authenticate(e.getUsername(), e.getPassword());
                
                // Redirect based on Role
                if ("PRINCIPAL".equals(user.getRole())) {
                    UI.getCurrent().navigate("principal-dashboard");
                } else if ("TEACHER".equals(user.getRole())) {
                    UI.getCurrent().navigate("teacher-dashboard");
                }
                
            } catch (AuthService.AuthException ex) {
                loginForm.setError(true);
                Notification.show(ex.getMessage(), 3000, Notification.Position.BOTTOM_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        // Add everything to the card
        loginCard.add(logo, appName, slogan, loginForm);

        // Add card to the main view
        add(loginCard);
    }
}