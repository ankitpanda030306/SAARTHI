package com.example.dropout.ui;

import com.example.dropout.entity.GovernmentScheme;
import com.example.dropout.entity.Student;
import com.example.dropout.entity.User;
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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.progressbar.ProgressBarVariant;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import java.util.List;
import java.util.stream.Collectors;

@Route("teacher-dashboard")
@PermitAll
public class DashboardView extends VerticalLayout {

    private final StudentService studentService;
    private final AuthService authService;
    private final SchemeService schemeService; // NEW
    
    private List<Student> myStudents;
    private Grid<Student> grid;

    public DashboardView(StudentService studentService, AuthService authService, SchemeService schemeService) {
        this.studentService = studentService;
        this.authService = authService;
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

        if(authService.isSchoolLocked(currentUser.getSchool())) {
             authService.logout();
             UI.getCurrent().navigate("login");
             return;
        }

        add(createHeader(currentUser));

        Tab tab1 = new Tab("Class Students");
        Tab tab2 = new Tab("Update Academics");
        Tab tab3 = new Tab("Raise Intervention");
        Tabs tabs = new Tabs(tab1, tab2, tab3);
        tabs.setWidthFull();
        tabs.addThemeVariants(com.vaadin.flow.component.tabs.TabsVariant.LUMO_CENTERED);

        myStudents = studentService.findBySchool(currentUser.getSchool());
        if(!"N/A".equals(currentUser.getAssignedClass())) {
            myStudents = myStudents.stream().filter(s -> (s.getCurrentClass() + " " + s.getSection()).equalsIgnoreCase(currentUser.getAssignedClass())).collect(Collectors.toList());
        }

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setPadding(true);
        content.setAlignItems(Alignment.CENTER);

        content.add(createStudentSection(myStudents)); 

        tabs.addSelectedChangeListener(e -> {
            content.removeAll();
            if(e.getSelectedTab().equals(tab1)) content.add(createStudentSection(myStudents));
            else if(e.getSelectedTab().equals(tab2)) content.add(createUpdateSection(myStudents));
            else content.add(createInterventionSection(currentUser));
        });

        add(tabs, content);
    }

    private Component createStudentSection(List<Student> students) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("95%");
        layout.setMaxWidth("1200px");

        long total = students.size();
        long risk = students.stream().filter(s -> !"Low".equals(s.getRiskLevel())).count();
        double avgAtt = students.stream().mapToDouble(Student::getAttendance).average().orElse(0.0);
        double avgMark = students.stream().mapToDouble(Student::getMathScore).average().orElse(0.0);

        HorizontalLayout statsLayout = new HorizontalLayout();
        statsLayout.setWidthFull();
        statsLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout c1 = createCard("Risk Overview");
        ProgressBar bar = new ProgressBar(0, total, risk);
        bar.addThemeVariants(ProgressBarVariant.LUMO_ERROR);
        bar.setHeight("10px");
        Span riskTxt = new Span(risk + " at risk out of " + total);
        riskTxt.getStyle().set("color", "red").set("font-weight", "bold");
        c1.add(bar, riskTxt);

        VerticalLayout c2 = createCard("Class Avg Attendance");
        c2.add(new H2(String.format("%.1f%%", avgAtt)));
        VerticalLayout c3 = createCard("Class Avg Marks");
        c3.add(new H2(String.format("%.1f", avgMark)));

        statsLayout.add(c1, c2, c3);
        layout.add(statsLayout);

        // SEARCH & FILTER
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        
        TextField searchField = new TextField();
        searchField.setPlaceholder("Search Student Name...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setWidth("300px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        
        ComboBox<String> filterBox = new ComboBox<>();
        filterBox.setPlaceholder("Filter by Risk");
        filterBox.setItems("All", "Critical", "High", "Medium", "Low");
        filterBox.setWidth("200px");

        searchField.addValueChangeListener(e -> applyFilter(searchField.getValue(), filterBox.getValue()));
        filterBox.addValueChangeListener(e -> applyFilter(searchField.getValue(), filterBox.getValue()));

        toolbar.add(searchField, filterBox);
        layout.add(toolbar);

        grid = new Grid<>(Student.class, false);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER);
        grid.getStyle().set("background", "var(--lumo-base-color)").set("border-radius", "10px");
        
        grid.addColumn(Student::getFullName).setHeader("Name");
        grid.addColumn(Student::getMathScore).setHeader("Avg Marks");
        grid.addColumn(Student::getAttendance).setHeader("Attendance");
        
        grid.addColumn(new ComponentRenderer<>(student -> {
            Button riskBtn = new Button(student.getRiskLevel());
            String r = student.getRiskLevel();
            if ("Critical".equals(r) || "High".equals(r)) riskBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
            else if ("Medium".equals(r)) riskBtn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            else riskBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            riskBtn.addClickListener(ev -> showAnalysis(student));
            return riskBtn;
        })).setHeader("Risk Status");

        grid.setItems(students);
        layout.add(grid);
        return layout;
    }

    private void applyFilter(String searchTerm, String riskFilter) {
        List<Student> filtered = myStudents.stream()
            .filter(s -> searchTerm == null || s.getFullName().toLowerCase().contains(searchTerm.toLowerCase()))
            .filter(s -> riskFilter == null || "All".equals(riskFilter) || s.getRiskLevel().equalsIgnoreCase(riskFilter))
            .collect(Collectors.toList());
        grid.setItems(filtered);
    }

    private Component createUpdateSection(List<Student> students) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("600px");
        layout.getStyle().set("background", "var(--lumo-base-color)").set("padding", "30px").set("border-radius", "10px");
        layout.setAlignItems(Alignment.CENTER);
        H3 title = new H3("Update Student Records");
        ComboBox<Student> studentBox = new ComboBox<>("Select Student");
        studentBox.setItems(students);
        studentBox.setItemLabelGenerator(Student::getFullName);
        studentBox.setWidthFull();
        ComboBox<String> examType = new ComboBox<>("Exam Type");
        examType.setItems("Periodic", "Half Yearly", "Annual");
        examType.setWidthFull();
        NumberField marks = new NumberField("Marks Obtained");
        marks.setWidthFull();
        NumberField att = new NumberField("Current Attendance %");
        att.setWidthFull();
        studentBox.addValueChangeListener(e -> {
            if(e.getValue() != null) {
                marks.setValue(e.getValue().getMathScore());
                att.setValue(e.getValue().getAttendance());
            }
        });
        Button update = new Button("Update & Calculate Risk", e -> {
            if(studentBox.getValue() != null && examType.getValue() != null) {
                studentService.updateStudentAcademic(studentBox.getValue(), examType.getValue(), marks.getValue(), att.getValue());
                Notification.show("Records Updated!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                marks.clear(); att.clear(); studentBox.clear(); examType.clear();
            } else {
                Notification.show("Please select Student and Exam Type").addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        update.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        update.setWidthFull();
        layout.add(title, studentBox, examType, marks, att, update);
        return layout;
    }

    private Component createInterventionSection(User teacher) {
        VerticalLayout layout = new VerticalLayout();
        layout.setWidth("600px");
        layout.getStyle().set("background", "var(--lumo-base-color)").set("padding", "30px").set("border-radius", "10px");
        H3 title = new H3("Raise Intervention Alert");
        ComboBox<Student> studentBox = new ComboBox<>("Student Name");
        studentBox.setItems(myStudents);
        studentBox.setItemLabelGenerator(Student::getFullName);
        studentBox.setWidthFull();
        TextArea reason = new TextArea("Observation / Complaint");
        reason.setMinHeight("150px");
        reason.setWidthFull();
        Button send = new Button("Send to Principal", e -> {
            studentService.submitComplaint(teacher.getFullName(), studentBox.getValue().getFullName(), reason.getValue());
            Notification.show("Alert Sent!").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        send.addThemeVariants(ButtonVariant.LUMO_ERROR);
        send.setWidthFull();
        layout.add(title, studentBox, reason, send);
        return layout;
    }

    private VerticalLayout createCard(String title) {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("30%");
        card.setPadding(true);
        card.setAlignItems(Alignment.CENTER);
        card.getStyle().set("background", "var(--lumo-base-color)").set("border-radius", "12px").set("box-shadow", "0 2px 8px rgba(0,0,0,0.05)");
        card.add(new Span(title));
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
        t.add(new H3(user.getSchool()), new Span("Teacher Dashboard | " + user.getAssignedClass()));
        MenuBar menu = new MenuBar();
        menu.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
        MenuItem item = menu.addItem(new Avatar(user.getFullName()));
        item.getSubMenu().addItem("Principal: Mr. Ray (+91 9876543210)");
        item.getSubMenu().addItem("Logout", e->{authService.logout(); UI.getCurrent().navigate("login");});
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