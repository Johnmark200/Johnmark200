package Eclinic;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Eclinic.Patient;


public class PatientDashboard extends Application {
    private VBox mainContent;
    private Label currentPageTitle;
    
      // Database connection parameters
    private final String DB_URL = "jdbc:mysql://localhost:3306/eclinic";
    private final String DB_USER = "root";
    private final String DB_PASS = "";
    
    private String name;
    private String id;
    
    public PatientDashboard() {
        // Empty or initialization logic here
    }
    
     public PatientDashboard(String name, String id) {
        this.name = name;
        this.id = id;
    }
    
    
    
    private TableView<Patient> patientsTable;
    
    
    

    @Override
public void start(Stage primaryStage) {
   BorderPane layout = new BorderPane();
    VBox sidebar = createSidebar();
    mainContent = new VBox(); // ← initialize the field, not local var
    VBox headerContainer = createHeader();

    mainContent.setPadding(new Insets(10));
    
    showWelcomeView();

    layout.setTop(headerContainer);
    layout.setLeft(sidebar);
    layout.setCenter(mainContent);

    Scene scene = new Scene(layout, 1200, 800);
   
    primaryStage.setScene(scene);
    primaryStage.setTitle("E-Clinic - Patient Dashboard");
    primaryStage.setResizable(true);
    primaryStage.show();
}




    private VBox createHeader() {
        VBox headerContainer = new VBox();
        
        String patientName = "Unknown";
        String patientID = "N/A";

        
         String sql = "SELECT name, patient_id FROM patient WHERE patient_id = ?";
         
        
        // Main header with gradient background
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setPadding(new Insets(20, 30, 20, 30));
        headerBox.setStyle("-fx-background-color: linear-gradient(to right, #667eea 0%, #764ba2 100%);");
        
        // Add shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.3));
        shadow.setOffsetY(2);
        headerBox.setEffect(shadow);

        Label header = new Label("E-CLINIC");
        header.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        header.setTextFill(Color.WHITE);
        
        Label subtitle = new Label("Patient Dashboard");
        subtitle.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 16));
        subtitle.setTextFill(Color.color(1, 1, 1, 0.8));
        
        VBox titleBox = new VBox(5);
        titleBox.getChildren().addAll(header, subtitle);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // User info section
      Label userInfo = new Label("👤 " + patientName + " | Patient ID: " + patientID);
        userInfo.setFont(Font.font("Segoe UI", 14));
        userInfo.setTextFill(Color.WHITE);
        
        headerBox.getChildren().addAll(titleBox, spacer, userInfo);
        
        // Breadcrumb/Current page indicator
        HBox breadcrumbBox = new HBox();
        breadcrumbBox.setPadding(new Insets(10, 30, 10, 30));
        breadcrumbBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e9ecef; -fx-border-width: 0 0 1 0;");
        
        currentPageTitle = new Label("Dashboard Overview");
        currentPageTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 18));
        currentPageTitle.setTextFill(Color.color(0.2, 0.2, 0.2));
        
        breadcrumbBox.getChildren().add(currentPageTitle);
        
        headerContainer.getChildren().addAll(headerBox, breadcrumbBox);
        return headerContainer;
        
        
    }

    public PatientDashboard PatientDashboard (String targetPatientID) {
    String sql = "SELECT name, patient_id FROM patient WHERE patient_id = ?";
    String dbUrl = "jdbc:mysql://localhost:3306/eclinic";
    String dbUser = "root";
    String dbPass = "";

    try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, targetPatientID);

        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                String name = rs.getString("name");
                String id = rs.getString("patient_id");
                return new PatientDashboard(name, id);
            }
        }

    } catch (SQLException e) {
        System.err.println("❌ Database error in fetchPatientInfo(): " + e.getMessage());
    }

    return null; // Return null if not found or on error
}

    
    
    private VBox createSidebar() {
        VBox sidebar = new VBox(8);
        sidebar.setPadding(new Insets(25, 0, 25, 0));
        sidebar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e9ecef; -fx-border-width: 0 1 0 0;");
        sidebar.setPrefWidth(280);

        Label menuTitle = new Label("Navigation");
        menuTitle.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 16));
        menuTitle.setTextFill(Color.color(0.4, 0.4, 0.4));
        menuTitle.setPadding(new Insets(0, 20, 15, 20));

        Button overviewBtn = createModernSidebarButton("🏠", "Dashboard Overview");
        Button viewDoctorsBtn = createModernSidebarButton("👨‍⚕️", "Medical Staff");
        Button viewDiagnosisBtn = createModernSidebarButton("📋", "My Diagnosis");
        Button viewRoomBtn = createModernSidebarButton("🏨", "Room Information");
        Button searchDoctorBtn = createModernSidebarButton("🔍", "Find Doctor");
        Button appealDischargeBtn = createModernSidebarButton("📤", "Discharge Appeal");

        // Add hover effects and actions
        overviewBtn.setOnAction(e -> {
            showWelcomeView();
            updateActiveButton(overviewBtn, sidebar);
        });
        
        viewDoctorsBtn.setOnAction(e -> {
            showDoctorAndNurseView();
            updateActiveButton(viewDoctorsBtn, sidebar);
        });
        
        viewDiagnosisBtn.setOnAction(e -> {
            showDiagnosisView();
            updateActiveButton(viewDiagnosisBtn, sidebar);
        });
        
        viewRoomBtn.setOnAction(e -> {
            showRoomInfoView();
            updateActiveButton(viewRoomBtn, sidebar);
        });
        
        searchDoctorBtn.setOnAction(e -> {
            showSearchDoctorView();
            updateActiveButton(searchDoctorBtn, sidebar);
        });
        
        appealDischargeBtn.setOnAction(e -> {
            showAppealDischargeView();
            updateActiveButton(appealDischargeBtn, sidebar);
        });

        sidebar.getChildren().addAll(menuTitle, overviewBtn, viewDoctorsBtn, viewDiagnosisBtn, 
                                   viewRoomBtn, searchDoctorBtn, appealDischargeBtn);

        // Set initial active button
        updateActiveButton(overviewBtn, sidebar);
        
        return sidebar;
    }

    
    
    
    private Button createModernSidebarButton(String icon, String text) {
        Button button = new Button();
        
        HBox content = new HBox(12);
        content.setAlignment(Pos.CENTER_LEFT);
        content.setPadding(new Insets(12, 20, 12, 20));
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(16));
        
        Label textLabel = new Label(text);
        textLabel.setFont(Font.font("Segoe UI", 14));
        
        content.getChildren().addAll(iconLabel, textLabel);
        button.setGraphic(content);
        button.setText("");
        
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-border-color: transparent; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8;"
        );
        
        // Hover effect
        button.setOnMouseEntered(e -> {
            if (!button.getStyle().contains("#667eea")) {
                button.setStyle(
                    "-fx-background-color: #f8f9fa; " +
                    "-fx-border-color: transparent; " +
                    "-fx-cursor: hand; " +
                    "-fx-background-radius: 8; " +
                    "-fx-border-radius: 8;"
                );
            }
        });
        
        button.setOnMouseExited(e -> {
            if (!button.getStyle().contains("#667eea")) {
                button.setStyle(
                    "-fx-background-color: transparent; " +
                    "-fx-border-color: transparent; " +
                    "-fx-cursor: hand; " +
                    "-fx-background-radius: 8; " +
                    "-fx-border-radius: 8;"
                );
            }
        });
        
        return button;
    }

    private void updateActiveButton(Button activeButton, VBox sidebar) {
        // Reset all buttons
        sidebar.getChildren().stream()
                .filter(node -> node instanceof Button)
                .map(node -> (Button) node)
                .forEach(btn -> {
                    btn.setStyle(
                        "-fx-background-color: transparent; " +
                        "-fx-border-color: transparent; " +
                        "-fx-cursor: hand; " +
                        "-fx-background-radius: 8; " +
                        "-fx-border-radius: 8;"
                    );
                    if (btn.getGraphic() instanceof HBox) {
                        HBox content = (HBox) btn.getGraphic();
                        content.getChildren().stream()
                                .filter(node -> node instanceof Label && !(((Label) node).getText().matches("[\uD83C-\uDBFF\uDC00-\uDFFF]+")))
                                .map(node -> (Label) node)
                                .forEach(label -> label.setTextFill(Color.color(0.3, 0.3, 0.3)));
                    }
                });
        
        // Set active button style
        activeButton.setStyle(
            "-fx-background-color: linear-gradient(to right, #667eea, #764ba2); " +
            "-fx-border-color: transparent; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 8; " +
            "-fx-border-radius: 8;"
        );
        
        if (activeButton.getGraphic() instanceof HBox) {
            HBox content = (HBox) activeButton.getGraphic();
            content.getChildren().stream()
                    .filter(node -> node instanceof Label)
                    .map(node -> (Label) node)
                    .forEach(label -> label.setTextFill(Color.WHITE));
        }
    }

    private StackPane createMainContent() {
        StackPane content = new StackPane();
        content.setStyle("-fx-background-color: #f8f9fa;");
        content.setPadding(new Insets(30));
        return content;
    }

    private void showWelcomeView() {
    currentPageTitle.setText("Dashboard Overview");

    VBox welcomeLayout = new VBox(25);
    welcomeLayout.setAlignment(Pos.TOP_CENTER);
    welcomeLayout.setPadding(new Insets(30, 50, 30, 50));

    // Welcome Header Card
    VBox welcomeCard = createCard("Welcome Back to E-Clinic!", 
                                  "Here’s a summary of your current health status and updates.");
    
    // Statistics Cards (Appointment, Room, Health Status)
    HBox statsRow = new HBox(20);
    statsRow.setAlignment(Pos.CENTER);
    statsRow.setPadding(new Insets(20, 0, 0, 0));

    VBox appointmentCard = createStatsCard("📅", "Next Appointment", "Tomorrow at 2:00 PM\nDr. Smith - Cardiology");
    VBox roomCard = createStatsCard("🏨", "Current Room", "Room 204-A\nGeneral Ward");
    VBox statusCard = createStatsCard("❤️", "Health Status", "Stable\nLast Checked: Today");

    statsRow.getChildren().addAll(appointmentCard, roomCard, statusCard);

    // Quick Actions Section
    VBox quickActions = createCard("Quick Actions", "");
    HBox actionButtons = new HBox(20);
    actionButtons.setAlignment(Pos.CENTER);
    actionButtons.setPadding(new Insets(10));

    Button viewDiagnosisBtn = createActionButton("📋 View Diagnosis");
    Button findDoctorBtn = createActionButton("🔍 Search Doctor");
    Button appealBtn = createActionButton("📤 Appeal for Discharge");

    actionButtons.getChildren().addAll(viewDiagnosisBtn, findDoctorBtn, appealBtn);
    quickActions.getChildren().add(actionButtons);

    // Add all elements to the main layout
    welcomeLayout.getChildren().addAll(welcomeCard, statsRow, quickActions);
    mainContent.getChildren().setAll(welcomeLayout);
}

    private VBox createCard(String title, String description) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        card.setPadding(new Insets(25));
        card.setMaxWidth(800);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.color(0.2, 0.2, 0.2));
        
        if (!description.isEmpty()) {
            Label descLabel = new Label(description);
            descLabel.setFont(Font.font("Segoe UI", 16));
            descLabel.setTextFill(Color.color(0.5, 0.5, 0.5));
            card.getChildren().addAll(titleLabel, descLabel);
        } else {
            card.getChildren().add(titleLabel);
        }
        
        return card;
    }

    private VBox createStatsCard(String icon, String title, String value) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-radius: 12; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        card.setPadding(new Insets(20));
        card.setPrefWidth(220);
        card.setPrefHeight(120);
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(24));
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        titleLabel.setTextFill(Color.color(0.4, 0.4, 0.4));
        
        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        valueLabel.setTextFill(Color.color(0.2, 0.2, 0.2));
        valueLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        
        card.getChildren().addAll(iconLabel, titleLabel, valueLabel);
        return card;
    }

    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        button.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea, #764ba2); " +
                       "-fx-text-fill: white; -fx-background-radius: 25; -fx-border-radius: 25; " +
                       "-fx-cursor: hand; -fx-padding: 12 24 12 24;");
        
        button.setOnMouseEntered(e -> button.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #5a67d8, #6b46c1); " +
            "-fx-text-fill: white; -fx-background-radius: 25; -fx-border-radius: 25; " +
            "-fx-cursor: hand; -fx-padding: 12 24 12 24;"
        ));
        
        button.setOnMouseExited(e -> button.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #667eea, #764ba2); " +
            "-fx-text-fill: white; -fx-background-radius: 25; -fx-border-radius: 25; " +
            "-fx-cursor: hand; -fx-padding: 12 24 12 24;"
        ));
        
        return button;
    }

    private void showDoctorAndNurseView() {
        currentPageTitle.setText("Medical Staff Directory");
        
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        
        // Create a professional table-like view
        VBox staffCard = createCard("Your Medical Team", "Healthcare professionals assigned to your care");
        
        // Staff list
        VBox staffList = new VBox(15);
        staffList.setPadding(new Insets(20, 0, 0, 0));
        
        HBox doctor1 = createStaffRow("👨‍⚕️", "Dr. Sarah Johnson", "Cardiologist", "15 years experience");
        HBox nurse1 = createStaffRow("👩‍⚕️", "Nurse Mary Wilson", "Registered Nurse", "Cardiology Ward");
        HBox doctor2 = createStaffRow("👨‍⚕️", "Dr. Michael Chen", "General Physician", "On-call Doctor");
        
        staffList.getChildren().addAll(doctor1, nurse1, doctor2);
        staffCard.getChildren().add(staffList);
        
        layout.getChildren().add(staffCard);
        mainContent.getChildren().setAll(layout);
    }

    private HBox createStaffRow(String icon, String name, String role, String info) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15));
        row.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-border-radius: 8;");
        
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font(20));
        
        VBox details = new VBox(3);
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        nameLabel.setTextFill(Color.color(0.2, 0.2, 0.2));
        
        Label roleLabel = new Label(role);
        roleLabel.setFont(Font.font("Segoe UI", 14));
        roleLabel.setTextFill(Color.color(0.4, 0.4, 0.4));
        
        Label infoLabel = new Label(info);
        infoLabel.setFont(Font.font("Segoe UI", 12));
        infoLabel.setTextFill(Color.color(0.6, 0.6, 0.6));
        
        details.getChildren().addAll(nameLabel, roleLabel, infoLabel);
        row.getChildren().addAll(iconLabel, details);
        
        return row;
    }

    private void showDiagnosisView() {
        currentPageTitle.setText("My Diagnosis");
        
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        
        VBox diagnosisCard = createCard("Medical Diagnosis & Treatment Plan", 
                                      "Your current diagnosis and prescribed treatment");
        
        // Diagnosis content
        VBox diagnosisContent = new VBox(15);
        diagnosisContent.setPadding(new Insets(20, 0, 0, 0));
        
        // Current diagnosis
        VBox currentDiagnosis = new VBox(8);
        Label diagnosisTitle = new Label("Current Diagnosis:");
        diagnosisTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        diagnosisTitle.setTextFill(Color.color(0.3, 0.3, 0.3));
        
        Label diagnosisText = new Label("Acute Myocardial Infarction (Heart Attack)\nStable condition, responding well to treatment");
        diagnosisText.setFont(Font.font("Segoe UI", 14));
        diagnosisText.setTextFill(Color.color(0.2, 0.2, 0.2));
        diagnosisText.setWrapText(true);
        
        currentDiagnosis.getChildren().addAll(diagnosisTitle, diagnosisText);
        
        // Treatment plan
        VBox treatmentPlan = new VBox(8);
        Label treatmentTitle = new Label("Treatment Plan:");
        treatmentTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        treatmentTitle.setTextFill(Color.color(0.3, 0.3, 0.3));
        
        Label treatmentText = new Label("• Cardiac monitoring\n• Medication: Beta-blockers, ACE inhibitors\n• Daily physiotherapy\n• Low-sodium diet");
        treatmentText.setFont(Font.font("Segoe UI", 14));
        treatmentText.setTextFill(Color.color(0.2, 0.2, 0.2));
        treatmentText.setWrapText(true);
        
        treatmentPlan.getChildren().addAll(treatmentTitle, treatmentText);
        
        diagnosisContent.getChildren().addAll(currentDiagnosis, new Separator(), treatmentPlan);
        diagnosisCard.getChildren().add(diagnosisContent);
        
        layout.getChildren().add(diagnosisCard);
        mainContent.getChildren().setAll(layout);
    }

    private void showRoomInfoView() {
        currentPageTitle.setText("Room Information");
        
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        
        VBox roomCard = createCard("Your Room Assignment", "Current accommodation details");
        
        // Room details grid
        GridPane roomGrid = new GridPane();
        roomGrid.setHgap(30);
        roomGrid.setVgap(15);
        roomGrid.setPadding(new Insets(20, 0, 0, 0));
        
        addRoomDetail(roomGrid, 0, "Room Number:", "204-A");
        addRoomDetail(roomGrid, 1, "Ward:", "Cardiology Ward");
        addRoomDetail(roomGrid, 2, "Room Type:", "Semi-Private");
        addRoomDetail(roomGrid, 3, "Bed Number:", "Bed 2");
        addRoomDetail(roomGrid, 4, "Admission Date:", "May 25, 2025");
        addRoomDetail(roomGrid, 5, "Expected Discharge:", "June 2, 2025");
        
        roomCard.getChildren().add(roomGrid);
        layout.getChildren().add(roomCard);
        mainContent.getChildren().setAll(layout);
    }

    private void addRoomDetail(GridPane grid, int row, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        labelNode.setTextFill(Color.color(0.4, 0.4, 0.4));
        
        Label valueNode = new Label(value);
        valueNode.setFont(Font.font("Segoe UI", 16));
        valueNode.setTextFill(Color.color(0.2, 0.2, 0.2));
        
        grid.add(labelNode, 0, row);
        grid.add(valueNode, 1, row);
    }

    private void showSearchDoctorView() {
        currentPageTitle.setText("Find Doctor");
        
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        
        VBox searchCard = createCard("Doctor Search", "Find healthcare professionals by name or specialization");
        
        // Search section
        VBox searchSection = new VBox(15);
        searchSection.setPadding(new Insets(20, 0, 0, 0));
        
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        
        TextField searchField = new TextField();
        searchField.setPromptText("Enter doctor name or specialization...");
        searchField.setPrefWidth(400);
        searchField.setStyle("-fx-font-size: 14; -fx-padding: 12; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e0e0e0;");
        
        Button searchBtn = new Button("🔍 Search");
        searchBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea, #764ba2); " +
                          "-fx-text-fill: white; -fx-background-radius: 8; -fx-border-radius: 8; " +
                          "-fx-cursor: hand; -fx-padding: 12 20 12 20; -fx-font-size: 14;");
        
        searchBox.getChildren().addAll(searchField, searchBtn);
        
        // Sample results
        VBox resultsSection = new VBox(10);
        Label resultsTitle = new Label("Available Doctors:");
        resultsTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        resultsTitle.setTextFill(Color.color(0.3, 0.3, 0.3));
        
        VBox doctorsList = new VBox(10);
        HBox doctor1 = createDoctorSearchResult("Dr. Emily Davis", "Neurologist", "Available Today");
        HBox doctor2 = createDoctorSearchResult("Dr. James Wilson", "Orthopedic Surgeon", "Available Tomorrow");
        HBox doctor3 = createDoctorSearchResult("Dr. Lisa Chen", "Dermatologist", "Available Mon-Fri");
        
        doctorsList.getChildren().addAll(doctor1, doctor2, doctor3);
        resultsSection.getChildren().addAll(resultsTitle, doctorsList);
        
        searchSection.getChildren().addAll(searchBox, new Separator(), resultsSection);
        searchCard.getChildren().add(searchSection);
        
        layout.getChildren().add(searchCard);
        mainContent.getChildren().setAll(layout);
    }

    private HBox createDoctorSearchResult(String name, String specialization, String availability) {
        HBox result = new HBox(15);
        result.setAlignment(Pos.CENTER_LEFT);
        result.setPadding(new Insets(15));
        result.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-border-radius: 8;");
        
        VBox details = new VBox(3);
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        nameLabel.setTextFill(Color.color(0.2, 0.2, 0.2));
        
        Label specLabel = new Label(specialization);
        specLabel.setFont(Font.font("Segoe UI", 14));
        specLabel.setTextFill(Color.color(0.4, 0.4, 0.4));
        
        Label availLabel = new Label(availability);
        availLabel.setFont(Font.font("Segoe UI", 12));
        availLabel.setTextFill(Color.color(0.1, 0.7, 0.1));
        
        details.getChildren().addAll(nameLabel, specLabel, availLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button contactBtn = new Button("Contact");
        contactBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; " +
                           "-fx-background-radius: 20; -fx-border-radius: 20; -fx-cursor: hand; " +
                           "-fx-padding: 8 16 8 16; -fx-font-size: 12;");
        
        result.getChildren().addAll(details, spacer, contactBtn);
        return result;
    }

    private void showAppealDischargeView() {
        currentPageTitle.setText("Discharge Appeal");
        
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        
        VBox appealCard = createCard("Appeal for Discharge", 
                                   "Submit your request to appeal the discharge decision");
        
        // Appeal form
        VBox formSection = new VBox(20);
        formSection.setPadding(new Insets(20, 0, 0, 0));
        
        // Reason selection
        VBox reasonSection = new VBox(10);
        Label reasonLabel = new Label("Reason for Appeal:");
        reasonLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        reasonLabel.setTextFill(Color.color(0.3, 0.3, 0.3));
        
        ComboBox<String> reasonCombo = new ComboBox<>();
        reasonCombo.getItems().addAll(
            "Medical condition not fully recovered",
            "Need additional treatment time",
            "Family concerns about home care",
            "Insurance coverage issues",
            "Other (please specify in message)"
        );
        reasonCombo.setPromptText("Select reason for appeal");
        reasonCombo.setPrefWidth(400);
        reasonCombo.setStyle("-fx-font-size: 14; -fx-background-radius: 8; -fx-border-radius: 8;");
        
        reasonSection.getChildren().addAll(reasonLabel, reasonCombo);
        
        // Message area
        VBox messageSection = new VBox(10);
        Label messageLabel = new Label("Additional Details:");
        messageLabel.setFont(Font.font("Segoe UI", FontWeight.SEMI_BOLD, 14));
        messageLabel.setTextFill(Color.color(0.3, 0.3, 0.3));
        
        TextArea appealText = new TextArea();
        appealText.setPromptText("Please provide detailed information about your appeal...");
        appealText.setPrefRowCount(6);
        appealText.setPrefWidth(600);
        appealText.setWrapText(true);
        appealText.setStyle("-fx-font-size: 14; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #e0e0e0;");
        
        messageSection.getChildren().addAll(messageLabel, appealText);
        
        // Submit section
        HBox submitSection = new HBox(15);
        submitSection.setAlignment(Pos.CENTER_LEFT);
        
        Button submitBtn = new Button("📤 Submit Appeal");
        submitBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea, #764ba2); " +
                          "-fx-text-fill: white; -fx-background-radius: 25; -fx-border-radius: 25; " +
                          "-fx-cursor: hand; -fx-padding: 15 30 15 30; -fx-font-size: 16; -fx-font-weight: bold;");
        
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #667eea; " +
                          "-fx-border-color: #667eea; -fx-border-width: 2; -fx-background-radius: 25; " +
                          "-fx-border-radius: 25; -fx-cursor: hand; -fx-padding: 15 30 15 30; -fx-font-size: 16;");
        
        // Add hover effects
        submitBtn.setOnMouseEntered(e -> submitBtn.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #5a67d8, #6b46c1); " +
            "-fx-text-fill: white; -fx-background-radius: 25; -fx-border-radius: 25; " +
            "-fx-cursor: hand; -fx-padding: 15 30 15 30; -fx-font-size: 16; -fx-font-weight: bold;"
        ));
        
        submitBtn.setOnMouseExited(e -> submitBtn.setStyle(
            "-fx-background-color: linear-gradient(to bottom, #667eea, #764ba2); " +
            "-fx-text-fill: white; -fx-background-radius: 25; -fx-border-radius: 25; " +
            "-fx-cursor: hand; -fx-padding: 15 30 15 30; -fx-font-size: 16; -fx-font-weight: bold;"
        ));
        
        submitSection.getChildren().addAll(submitBtn, cancelBtn);
        
        formSection.getChildren().addAll(reasonSection, messageSection, submitSection);
        appealCard.getChildren().add(formSection);
        
        layout.getChildren().add(appealCard);
        mainContent.getChildren().setAll(layout);
    }
    

    public static void main(String[] args) {
        launch(args);
    }
}