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
    private final SchemeService schemeService;
    
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
        
        grid.addColumn(Student::getFullName).setHeader("Name").setFlexGrow(2);

        // X-Y Axis Trend Graph
        grid.addComponentColumn(s -> {
            HorizontalLayout chartArea = new HorizontalLayout();
            chartArea.setAlignItems(Alignment.BASELINE);
            chartArea.setSpacing(false);
            chartArea.getStyle().set("border-bottom", "1px solid black").set("border-left", "1px solid black").set("padding", "2px");
            chartArea.setHeight("30px");
            chartArea.setWidth("60px");

            chartArea.add(createPlotPoint(s.getTerm1Score()), createPlotPoint(s.getTerm2Score()), createPlotPoint(s.getMathScore()));
            
            String trendColor = s.getMathScore() >= s.getTerm2Score() ? "green" : "red";
            Span arrow = new Span(s.getMathScore() >= s.getTerm2Score() ? "↑" : "↓");
            arrow.getStyle().set("color", trendColor).set("font-weight", "bold").set("margin-left", "5px");

            HorizontalLayout wrapper = new HorizontalLayout(chartArea, arrow);
            wrapper.setAlignItems(Alignment.CENTER);
            return wrapper;
        }).setHeader("Academic Trend").setWidth("150px").setFlexGrow(0);

        // Risk Button
        grid.addColumn(new ComponentRenderer<>(s -> {
            Button btn = new Button(s.getRiskLevel());
            String r = s.getRiskLevel();
            if ("Critical".equals(r) || "High".equals(r)) btn.addThemeVariants(ButtonVariant.LUMO_ERROR);
            else if ("Medium".equals(r)) btn.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
            else btn.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
            
            btn.addClickListener(ev -> showAnalysis(s));
            return btn;
        })).setHeader("Risk Status (Click for Action)").setFlexGrow(1);

        grid.setItems(students);
        layout.add(grid);
        return layout;
    }

    private Span createPlotPoint(double score) {
        Span dot = new Span();
        dot.getStyle().set("width", "5px").set("height", "5px").set("background", "#2196F3").set("border-radius", "50%");
        dot.getStyle().set("margin-bottom", (score * 0.25) + "px"); 
        return dot;
    }

    // --- DETAILED DIAGNOSTIC POPUP ---
    private void showAnalysis(Student s) {
        Dialog d = new Dialog(); 
        d.setHeaderTitle("Diagnostic Analysis: " + s.getFullName());
        d.setWidth("650px");

        VerticalLayout v = new VerticalLayout();
        v.setPadding(false);

        // 1. Risk Reason 
        Span boldPrefix = new Span("AI Risk Assessment: ");
        boldPrefix.getStyle().set("font-weight", "bold");
        Span reasonText = new Span(studentService.getRiskReason(s));
        
        Div reasonCard = new Div(boldPrefix, reasonText);
        reasonCard.getStyle().set("background", "#fff4f4").set("padding", "15px").set("border-left", "5px solid red").set("border-radius", "4px");
        v.add(reasonCard);

        // 2. Comparative Meters (Past vs Present)
        v.add(new H4("Trajectory Metrics (Past vs Present)"));
        
        // Generating consistent pseudo-past data for the demo so we don't have to alter the database
        double pastAtt = Math.max(0, Math.min(100, s.getAttendance() + ((s.getId() % 30) - 15)));
        double pastEcon = Math.max(0, Math.min(100, s.getEconomicStability() - (s.getId() % 10)));
        double pastWell = Math.max(0, Math.min(100, s.getWellBeingScore() + ((s.getId() % 40) - 20)));

        v.add(createComparativeMeter("Attendance Consistency", s.getAttendance(), pastAtt, "#4CAF50"));
        v.add(createComparativeMeter("Economic Stability Index", s.getEconomicStability(), pastEcon, "#9C27B0"));
        v.add(createComparativeMeter("Psychological Well-being", s.getWellBeingScore(), pastWell, "#F44336"));

        // 3. Government Schemes
        v.add(new Hr(), new H4("🎓 Eligible Interventions"));
        List<GovernmentScheme> schemes = schemeService.getRecommendedSchemes(s);
        
        if (schemes.isEmpty()) {
            v.add(new Span("No specific financial schemes applicable at this time."));
        } else {
            for (GovernmentScheme gs : schemes) {
                Span schemeName = new Span(gs.getSchemeName() + ": ");
                schemeName.getStyle().set("font-weight", "bold");
                Span schemeBenefit = new Span(gs.getBenefit());
                
                Div schemeCard = new Div(schemeName, schemeBenefit);
                schemeCard.getStyle().set("margin-bottom", "8px");
                v.add(schemeCard);
            }
        }
        
        HorizontalLayout actions = new HorizontalLayout();
        Button sms = new Button("SMS Parent", VaadinIcon.COMMENT.create());
        sms.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        sms.addClickListener(e -> {
            studentService.sendSmsToParent(s, "Predictive Alert: Meeting required for " + s.getFullName());
            Notification.show("SMS Dispatched").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });
        
        actions.add(sms, new Button("Close", e -> d.close()));
        v.add(new Hr(), actions);

        d.add(v);
        d.open();
        // Inside your showAnalysis(Student s) method
Button whatsappBtn = new Button("WhatsApp Guardian", VaadinIcon.PHONE.create());
whatsappBtn.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_TERTIARY);

whatsappBtn.addClickListener(e -> {
    // 1. Sanitize the phone number (remove spaces/dashes)
    String phone = s.getContactNumber().replaceAll("[^0-9]", "");
    if (!phone.startsWith("91")) phone = "91" + phone; // Add India code if missing

    // 2. Encode the message for a URL
    String message = "Hello, this is " + s.getSchoolName() + 
                     ". We would like to discuss the academic progress of " + s.getFullName() + ".";
    
    // 3. Create the direct link
    String waUrl = "https://wa.me/" + phone + "?text=" + message.replace(" ", "%20");

    // 4. Open in a new tab
    UI.getCurrent().getPage().open(waUrl, "_blank");
    
});
    }

    // --- NEW: COMPARATIVE DUAL-METER COMPONENT ---
    private VerticalLayout createComparativeMeter(String label, double presentVal, double pastVal, String color) {
        double diff = presentVal - pastVal;
        String trendStr = diff >= 0 ? "(+" + (int)diff + "% ↑)" : "(" + (int)diff + "% ↓)";
        String trendColor = diff >= 0 ? "green" : "red";

        // Header Layout (Title + Trend + Past Value text)
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(JustifyContentMode.BETWEEN);
        header.setAlignItems(Alignment.BASELINE);

        Span titleAndTrend = new Span();
        Span title = new Span(label + ": " + (int)presentVal + "% ");
        title.getStyle().set("font-weight", "bold");
        Span trend = new Span(trendStr);
        trend.getStyle().set("color", trendColor).set("font-size", "0.9em").set("font-weight", "bold");
        titleAndTrend.add(title, trend);

        Span pastLabel = new Span("Past: " + (int)pastVal + "%");
        pastLabel.getStyle().set("color", "gray").set("font-size", "0.8em");
        header.add(titleAndTrend, pastLabel);

        // Thick Bar for Present
        ProgressBar presentBar = new ProgressBar();
        presentBar.setValue(Math.max(0, Math.min(presentVal / 100.0, 1.0)));
        presentBar.getElement().getStyle().set("--lumo-primary-color", color);
        presentBar.setHeight("8px");

        // Thin Faded Bar for Past
        ProgressBar pastBar = new ProgressBar();
        pastBar.setValue(Math.max(0, Math.min(pastVal / 100.0, 1.0)));
        pastBar.getElement().getStyle().set("--lumo-primary-color", "gray");
        pastBar.setHeight("3px");

        VerticalLayout layout = new VerticalLayout(header, presentBar, pastBar);
        layout.setSpacing(false);
        layout.setPadding(false);
        layout.getStyle().set("margin-bottom", "15px");
        return layout;
    }

    // --- ORIGINAL METHODS RESTORED ---
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
}