package com.example.dropout.ui;

import com.example.dropout.entity.GovernmentScheme;
import com.example.dropout.entity.Student;
import com.example.dropout.entity.TeacherComplaint;
import com.example.dropout.entity.User;
import com.example.dropout.repository.UserRepository;
import com.example.dropout.service.AuthService;
import com.example.dropout.service.SchemeService;
import com.example.dropout.service.StudentService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import jakarta.annotation.security.PermitAll;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Route("principal-dashboard")
@PermitAll
public class PrincipalDashboard extends VerticalLayout {

    private final StudentService studentService;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final SchemeService schemeService; // NEW
    
    private Grid<Student> studentGrid;
    private List<Student> allStudents;
    private Grid<User> teacherGrid;
    private List<User> allTeachers;

    public PrincipalDashboard(StudentService studentService, AuthService authService, UserRepository userRepository, SchemeService schemeService) {
        this.studentService = studentService;
        this.authService = authService;
        this.userRepository = userRepository;
        this.schemeService = schemeService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background-color", "var(--lumo-contrast-5pct)");

        User currentUser = authService.getCurrentUser();
        if (currentUser == null) {
            UI.getCurrent().navigate("login");
            return;
        }

        // --- 1. HEADER ---
        add(createHeader(currentUser));

        // --- 2. TABS ---
        Tab tab1 = new Tab("Students Overview");
        Tab tab2 = new Tab("Teachers Directory");
        Tab tab3 = new Tab("Intervention Log");
        Tab tab4 = new Tab("Govt Schemes Library"); // NEW TAB
        Tabs tabs = new Tabs(tab1, tab2, tab3, tab4);
        tabs.setWidthFull();
        tabs.addThemeVariants(com.vaadin.flow.component.tabs.TabsVariant.LUMO_CENTERED);

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setPadding(true);
        content.setAlignItems(Alignment.CENTER);

        // Load Default Tab
        content.add(createStudentSection(currentUser));

        tabs.addSelectedChangeListener(event -> {
            content.removeAll();
            if (event.getSelectedTab().equals(tab1)) content.add(createStudentSection(currentUser));
            else if (event.getSelectedTab().equals(tab2)) content.add(createTeacherSection(currentUser));
            else if (event.getSelectedTab().equals(tab3)) content.add(createInterventionSection());
            else content.add(createSchemeSection()); // NEW SECTION
        });

        add(tabs, content);
    }

    // --- SECTION 1: STUDENTS ---
    private Component createStudentSection(User user) {
        allStudents = studentService.findBySchool(user.getSchool());
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("95%");
        layout.setMaxWidth("1200px");

        layout.add(createStatsRow(allStudents));

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(JustifyContentMode.BETWEEN);
        toolbar.setAlignItems(Alignment.END);

        TextField searchField = new TextField();
        searchField.setPlaceholder("Search Student...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        
        ComboBox<String> filterBox = new ComboBox<>();
        filterBox.setPlaceholder("Filter Risk");
        filterBox.setItems("All", "Critical", "High", "Medium", "Low");

        searchField.addValueChangeListener(e -> filterStudents(searchField.getValue(), filterBox.getValue()));
        filterBox.addValueChangeListener(e -> filterStudents(searchField.getValue(), filterBox.getValue()));

        HorizontalLayout searchLayout = new HorizontalLayout(searchField, filterBox);

        Button addBtn = new Button("New Admission", VaadinIcon.PLUS.create());
        addBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addBtn.addClickListener(e -> Notification.show("Admission Form Opened"));

        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        Button uploadBtn = new Button("Upload CSV", VaadinIcon.UPLOAD.create());
        upload.setUploadButton(uploadBtn);
        upload.setDropAllowed(false);
        upload.setHeight("min-content");

        Anchor downloadLink = new Anchor(new StreamResource("report.csv", () -> 
            new ByteArrayInputStream("Name,Risk\nRahul,High".getBytes(StandardCharsets.UTF_8))), "");
        downloadLink.getElement().setAttribute("download", true);
        Button downloadBtn = new Button("Download Report", VaadinIcon.DOWNLOAD.create());
        downloadLink.add(downloadBtn);

        HorizontalLayout actionsLayout = new HorizontalLayout(addBtn, upload, downloadLink);
        
        toolbar.add(searchLayout, actionsLayout);
        layout.add(toolbar);

        studentGrid = new Grid<>(Student.class, false);
        studentGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        studentGrid.getStyle().set("background", "var(--lumo-base-color)").set("border-radius", "10px");
        
        studentGrid.addColumn(Student::getFullName).setHeader("Name");
        studentGrid.addColumn(Student::getCurrentClass).setHeader("Class");
        studentGrid.addColumn(Student::getRollNumber).setHeader("Roll No");

        studentGrid.addColumn(new ComponentRenderer<>(student -> {
            Button riskBtn = new Button(student.getRiskLevel());
            String risk = student.getRiskLevel();
            if ("Critical".equals(risk) || "High".equals(risk)) riskBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
            else if ("Medium".equals(risk)) riskBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            else riskBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            riskBtn.addClickListener(e -> showAnalysis(student));
            return riskBtn;
        })).setHeader("Risk Analysis");

        studentGrid.setItems(allStudents);
        layout.add(studentGrid);
        return layout;
    }

    private void filterStudents(String searchTerm, String riskFilter) {
        List<Student> filtered = allStudents.stream()
            .filter(s -> searchTerm == null || s.getFullName().toLowerCase().contains(searchTerm.toLowerCase()))
            .filter(s -> riskFilter == null || "All".equals(riskFilter) || s.getRiskLevel().equalsIgnoreCase(riskFilter))
            .collect(Collectors.toList());
        studentGrid.setItems(filtered);
    }

    // --- SECTION 2: TEACHERS ---
    private Component createTeacherSection(User user) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("95%");
        layout.setMaxWidth("1200px");

        TextField searchField = new TextField();
        searchField.setPlaceholder("Search Teacher...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> {
             List<User> filtered = allTeachers.stream()
                .filter(t -> t.getFullName().toLowerCase().contains(e.getValue().toLowerCase()))
                .collect(Collectors.toList());
             teacherGrid.setItems(filtered);
        });
        layout.add(searchField);

        teacherGrid = new Grid<>(User.class, false);
        teacherGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        teacherGrid.getStyle().set("background", "var(--lumo-base-color)").set("border-radius", "10px");

        teacherGrid.addComponentColumn(u -> new Avatar(u.getFullName())).setWidth("60px").setFlexGrow(0);
        teacherGrid.addColumn(User::getFullName).setHeader("Teacher Name");
        teacherGrid.addColumn(User::getAssignedClass).setHeader("Class Assigned");
        
        teacherGrid.addColumn(u -> {
            if (u.getLastLogin() == null) return "Never";
            return u.getLastLogin().format(DateTimeFormatter.ofPattern("dd MMM, hh:mm a"));
        }).setHeader("Last Login");

        teacherGrid.addComponentColumn(u -> {
            Span badge = new Span();
            boolean isActive = false;
            if (u.getLastLogin() != null) {
                isActive = u.getLastLogin().toLocalDate().equals(java.time.LocalDate.now());
            }
            badge.setText(isActive ? "Active Now" : "Inactive");
            badge.getElement().getThemeList().add(isActive ? "badge success" : "badge contrast");
            return badge;
        }).setHeader("Status");

        allTeachers = userRepository.findAll().stream()
                .filter(u -> "TEACHER".equals(u.getRole()) && user.getSchool().equals(u.getSchool()))
                .collect(Collectors.toList());
        
        teacherGrid.setItems(allTeachers);
        layout.add(teacherGrid);
        return layout;
    }

    // --- SECTION 3: INTERVENTION ---
    private Component createInterventionSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("95%");
        layout.setMaxWidth("1200px");

        // Fetch all complaints (both pending and resolved so we can see history)
        List<TeacherComplaint> logs = studentService.getAllComplaints(); 
        
        if(logs.isEmpty()) {
            layout.add(new H4("No Intervention Records Found."));
        } else {
            Grid<TeacherComplaint> logGrid = new Grid<>(TeacherComplaint.class, false);
            logGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
            logGrid.getStyle().set("background", "var(--lumo-base-color)").set("border-radius", "10px");

            // Columns
            logGrid.addColumn(c -> c.getTimestamp().format(DateTimeFormatter.ofPattern("dd MMM, hh:mm a")))
                   .setHeader("Date/Time").setAutoWidth(true);
            logGrid.addColumn(TeacherComplaint::getTeacherName).setHeader("Teacher");
            logGrid.addColumn(TeacherComplaint::getStudentName).setHeader("Student");
            logGrid.addColumn(TeacherComplaint::getComplaintText).setHeader("Issue/Intervention").setWidth("300px");

            // Status Column (Badge)
            logGrid.addComponentColumn(c -> {
                Span s = new Span(c.isRead() ? "Resolved" : "Pending");
                s.getElement().getThemeList().add(c.isRead() ? "badge success" : "badge error");
                return s;
            }).setHeader("Status");

            // ACTION COLUMN: The "Mark Resolved" Button
            logGrid.addComponentColumn(c -> {
                if (c.isRead()) {
                    return new Span("✅ Done");
                } else {
                    Button resolveBtn = new Button("Mark Resolved");
                    resolveBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
                    resolveBtn.addClickListener(e -> {
                        studentService.markComplaintAsRead(c); // Updates DB
                        Notification.show("Issue Marked as Resolved!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        
                        // Refresh Grid to show Green Badge
                        logGrid.getDataProvider().refreshItem(c); 
                    });
                    return resolveBtn;
                }
            }).setHeader("Action");

            logGrid.setItems(logs);
            layout.add(logGrid);
        }
        return layout;
    }

    // --- SECTION 4: SCHEMES (NEW) ---
    private Component createSchemeSection() {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("95%");
        layout.setMaxWidth("1200px");
        layout.add(new H3("Government Scheme Library"));

        Grid<GovernmentScheme> grid = new Grid<>(GovernmentScheme.class, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.getStyle().set("background", "white").set("border-radius", "10px");

        grid.addColumn(GovernmentScheme::getSchemeName).setHeader("Scheme Name").setAutoWidth(true);
        grid.addColumn(GovernmentScheme::getEligibilityCriteria).setHeader("Eligibility");
        grid.addColumn(GovernmentScheme::getBenefit).setHeader("Benefit");
        
        grid.addComponentColumn(s -> {
            Anchor link = new Anchor(s.getApplicationLink(), "View Portal");
            link.setTarget("_blank");
            return link;
        }).setHeader("Apply Link");

        grid.setItems(schemeService.findAll());
        layout.add(grid);
        return layout;
    }

    // --- HELPER METHODS ---
    private HorizontalLayout createStatsRow(List<Student> students) {
        long total = students.size();
        long highRisk = students.stream().filter(s -> "High".equals(s.getRiskLevel()) || "Critical".equals(s.getRiskLevel())).count();
        double avgAtt = students.stream().mapToDouble(Student::getAttendance).average().orElse(0.0);
        double avgMark = students.stream().mapToDouble(Student::getMathScore).average().orElse(0.0);

        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        
        VerticalLayout riskCard = createCard("Risk Meter");
        ProgressBar riskBar = new ProgressBar(0, total, highRisk);
        riskBar.addThemeVariants(ProgressBarVariant.LUMO_ERROR);
        riskBar.setHeight("10px");
        Span riskText = new Span(highRisk + " at risk out of " + total);
        riskText.getStyle().set("font-size", "0.9em").set("color", "red").set("font-weight", "bold");
        riskCard.add(riskBar, riskText);

        layout.add(riskCard);
        layout.add(createCard("Avg Attendance", new H2(String.format("%.1f%%", avgAtt))));
        layout.add(createCard("Avg Marks", new H2(String.format("%.1f", avgMark))));
        return layout;
    }

    private VerticalLayout createCard(String title, Component... components) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("30%");
        card.setPadding(true);
        card.setAlignItems(Alignment.CENTER);
        card.getStyle().set("background", "var(--lumo-base-color)").set("border-radius", "12px").set("box-shadow", "0 2px 8px rgba(0,0,0,0.05)");
        card.add(new Span(title));
        for(Component c : components) card.add(c);
        return card;
    }

    private HorizontalLayout createHeader(User user) {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.setAlignItems(Alignment.CENTER);
        header.getStyle().set("background", "var(--lumo-base-color)").set("box-shadow", "0 2px 4px rgba(0,0,0,0.05)");
        Image logo = new Image("images/logo.png", "SAARTHI");
        logo.setHeight("50px");
        VerticalLayout t = new VerticalLayout();
        t.setSpacing(false);
        t.add(new H3(user.getSchool()), new Span("Principal Dashboard"));
        MenuBar menu = new MenuBar();
        menu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        MenuItem item = menu.addItem(new Avatar(user.getFullName()));
        boolean isLocked = authService.isSchoolLocked(user.getSchool());
        MenuItem lockItem = item.getSubMenu().addItem(isLocked ? "Unlock Portal" : "Lock Portal");
        lockItem.addClickListener(e -> {
            if(authService.isSchoolLocked(user.getSchool())) {
                authService.unlockSchool(user.getSchool());
                lockItem.setText("Lock Portal");
                Notification.show("Portal Unlocked").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            } else {
                authService.lockSchool(user.getSchool());
                lockItem.setText("Unlock Portal");
                Notification.show("Portal Locked").addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        item.getSubMenu().addItem("Logout", e -> { authService.logout(); UI.getCurrent().navigate("login"); });
        header.add(logo, t, menu);
        header.setFlexGrow(1, t);
        return header;
    }

    // --- UPDATED SHOW ANALYSIS (SMS + COUNSELING + SCHEMES) ---
    private void showAnalysis(Student s) {
        Dialog d = new Dialog(); 
        d.setHeaderTitle("Risk Analysis: " + s.getFullName());
        d.setWidth("500px");

        VerticalLayout v = new VerticalLayout();
        v.setPadding(false);
        v.add(new H4("Risk Level: " + s.getRiskLevel()));
        v.add(new Paragraph(studentService.getRiskReason(s)));
        
        // Buttons
        Button smsBtn = new Button("SMS Parent", VaadinIcon.COMMENT.create());
        smsBtn.addClickListener(e -> {
            studentService.sendSmsToParent(s, "URGENT: Meeting request regarding child's performance.");
            Notification.show("SMS Sent").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        Button counselBtn = new Button("Schedule Counseling", VaadinIcon.CALENDAR.create());
        counselBtn.addClickListener(e -> {
            Dialog scheduleDialog = new Dialog();
            DateTimePicker picker = new DateTimePicker("Select Date");
            Button confirm = new Button("Confirm", ev -> {
                studentService.scheduleCounseling(s, picker.getValue());
                Notification.show("Scheduled").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                scheduleDialog.close();
            });
            scheduleDialog.add(new VerticalLayout(picker, confirm));
            scheduleDialog.open();
        });

        v.add(new HorizontalLayout(smsBtn, counselBtn));
        v.add(new Hr());

        // SMART SCHEMES
        v.add(new H4("🎓 Recommended Govt Schemes"));
        List<GovernmentScheme> schemes = schemeService.getRecommendedSchemes(s);

        if (schemes.isEmpty()) {
            v.add(new Span("No specific schemes found."));
        } else {
            for (GovernmentScheme scheme : schemes) {
                VerticalLayout card = new VerticalLayout();
                card.getStyle().set("background", "#f0fdf4").set("border", "1px solid #22c55e").set("border-radius", "8px").set("padding", "10px");
                H5 name = new H5(scheme.getSchemeName());
                name.getStyle().set("margin", "0");
                Span benefit = new Span("Benefit: " + scheme.getBenefit());
                benefit.getStyle().set("font-size", "0.9em").set("color", "green");
                Anchor link = new Anchor(scheme.getApplicationLink(), "Apply Now");
                link.setTarget("_blank");
                card.add(name, benefit, link);
                v.add(card);
            }
        }

        v.add(new Button("Close", e->d.close()));
        d.add(v);
        d.open();
    }
}