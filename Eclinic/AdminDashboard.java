package Eclinic;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AdminDashboard extends Application {
    
    
        private ComboBox<String> diseaseComboBox;
        private DatePicker appointmentDatePicker;
        private ComboBox<String> timeComboBox;
        private Label assignedDoctorLabel;
        private Button confirmButton;
        private BorderPane rootLayout;
        private VBox currentContent;
        
    private Connection connect() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/eclinic", "root", "");
        } catch (SQLException e) {
            return null;
        }
    }
    


   @Override
public void start(Stage primaryStage) {
// Root layout with header, sidebar, and dynamic center
rootLayout = new BorderPane();
rootLayout.setStyle("-fx-background-color: #f8f9fa;");

// Header and Sidebar
VBox header = createModernHeader();
VBox sidebar = createModernSidebar();

// Main center layout – this is your dynamic dashboard content
currentContent = new VBox();
currentContent.getChildren().addAll(
    createDashboardOverview() // ✅ Only the dashboard overview
);

// Set all regions in BorderPane
rootLayout.setTop(header);
rootLayout.setLeft(sidebar);
rootLayout.setCenter(currentContent); // ✅ Center is set only once

// Scene setup
Scene scene = new Scene(rootLayout, 1200, 800);
primaryStage.setScene(scene);
primaryStage.setTitle("E-Clinic Admin Dashboard");
primaryStage.show();

}

    

    
    private void showAddPatientForm() {
    Stage formStage = new Stage();
    formStage.setTitle("Add New Patient");

    GridPane grid = new GridPane();
    grid.setPadding(new Insets(10));
    grid.setVgap(10);
    grid.setHgap(10);

    TextField nameField = new TextField();
    TextField ageField = new TextField();
    TextField contactField = new TextField();

    grid.add(new Label("Name:"), 0, 0);
    grid.add(nameField, 1, 0);
    grid.add(new Label("Age:"), 0, 1);
    grid.add(ageField, 1, 1);
    grid.add(new Label("Contact:"), 0, 2);
    grid.add(contactField, 1, 2);

    Button saveBtn = new Button("Save");
    saveBtn.setOnAction(event -> {
        String name = nameField.getText();
        int age = Integer.parseInt(ageField.getText());
        String contact = contactField.getText();
        savePatientToDB(name, age, contact);
        formStage.close();
    });

    grid.add(saveBtn, 1, 3);

    Scene scene = new Scene(grid, 300, 200);
    formStage.setScene(scene);
    formStage.show();
}

private void savePatientToDB(String name, int age, String contact) {
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/eclinic", "username", "password");
) {
        String sql = "INSERT INTO patients (name, age, contact) VALUES (?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setInt(2, age);
        stmt.setString(3, contact);
        stmt.executeUpdate();
        System.out.println("Patient saved successfully.");
    } catch (SQLException e) {
    }
}


private void showScheduleAppointmentForm() {
    Stage formStage = new Stage();
    formStage.setTitle("Schedule Appointment");

    GridPane grid = new GridPane();
    grid.setPadding(new Insets(10));
    grid.setVgap(10);
    grid.setHgap(10);

    TextField patientIdField = new TextField();
    TextField doctorField = new TextField();
    TextField dateField = new TextField();

    grid.add(new Label("Patient ID:"), 0, 0);
    grid.add(patientIdField, 1, 0);
    grid.add(new Label("Doctor:"), 0, 1);
    grid.add(doctorField, 1, 1);
    grid.add(new Label("Date (YYYY-MM-DD):"), 0, 2);
    grid.add(dateField, 1, 2);

    Button saveBtn = new Button("Schedule");
    saveBtn.setOnAction(event -> {
        int patientId = Integer.parseInt(patientIdField.getText());
        String doctor = doctorField.getText();
        String date = dateField.getText();
        saveAppointmentToDB(patientId, doctor, date);
        formStage.close();
    });

    grid.add(saveBtn, 1, 3);

    Scene scene = new Scene(grid, 300, 200);
    formStage.setScene(scene);
    formStage.show();
}

private void saveAppointmentToDB(int patientId, String doctor, String date) {
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/eclinic")) {
        String sql = "INSERT INTO appointments (patient_id, doctor, appointment_date) VALUES (?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, patientId);
        stmt.setString(2, doctor);
        stmt.setString(3, date);
        stmt.executeUpdate();
        System.out.println("Appointment scheduled successfully.");
    } catch (SQLException e) {
    }
}


private void viewReports() {
    String url = "jdbc:mysql://localhost:3306/eclinic"; 
    String user = "root"; 
    String password = ""; 

    try (Connection conn = DriverManager.getConnection(url, user, password)) {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM reports");

        while (rs.next()) {
            System.out.println("Report ID: " + rs.getInt("id"));
            System.out.println("Patient ID: " + rs.getInt("patient_id"));
            System.out.println("Details: " + rs.getString("details"));
            System.out.println("--------------------------");
        }
    } catch (SQLException e) {
    }
}


private void showManageStaffForm() {
    Stage formStage = new Stage();
    formStage.setTitle("Manage Staff");

    GridPane grid = new GridPane();
    grid.setPadding(new Insets(10));
    grid.setVgap(10);
    grid.setHgap(10);

    TextField nameField = new TextField();
    TextField roleField = new TextField();

    grid.add(new Label("Name:"), 0, 0);
    grid.add(nameField, 1, 0);
    grid.add(new Label("Role:"), 0, 1);
    grid.add(roleField, 1, 1);

    Button saveBtn = new Button("Save");
    saveBtn.setOnAction(event -> {
        String name = nameField.getText();
        String role = roleField.getText();
        saveStaffToDB(name, role);
        formStage.close();
    });

    grid.add(saveBtn, 1, 2);

    Scene scene = new Scene(grid, 300, 150);
    formStage.setScene(scene);
    formStage.show();
}




   
    
    private VBox createModernHeader() {
        VBox header = new VBox();
        header.setPrefHeight(80);

        Region gradientRegion = new Region();
        gradientRegion.setStyle("-fx-background-color: linear-gradient(to right, #667eea, #764ba2);");
        gradientRegion.setPrefHeight(80);

        HBox headerContent = new HBox();
        headerContent.setPadding(new Insets(15, 30, 15, 30));
        headerContent.setAlignment(Pos.CENTER_LEFT);
        headerContent.setSpacing(20);

        Label title = new Label("E-CLINIC ADMIN");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox userInfo = new VBox(2);
        userInfo.setAlignment(Pos.CENTER_RIGHT);
        Label welcomeLabel = new Label("Welcome, Administrator");
        welcomeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        Label dateLabel = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(255,255,255,0.8);");

        userInfo.getChildren().addAll(welcomeLabel, dateLabel);
        headerContent.getChildren().addAll(title, spacer, userInfo);

        StackPane headerStack = new StackPane(gradientRegion, headerContent);
        header.getChildren().add(headerStack);

        return header;
    }
    
     
      
         private Button createSidebarButton(String text, boolean active) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(12));
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
        return button;
    }

    private void resetSidebarButtons(VBox sidebar) {
       for (Node node : sidebar.getChildren()) {
            if (node instanceof Button) {
                node.setStyle("-fx-background-color: transparent; -fx-text-fill: white;");
            }
        }
    }
    
    
     private void updateMainContent(javafx.scene.Node content) {
        rootLayout.setCenter(content);
    }

    
   private Node createClinicStatsChart() {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Disease");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Number of Patients");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Disease Diagnosis Statistics");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Current Diagnoses");

        String sql = "SELECT disease, COUNT(*) AS count FROM patients GROUP BY disease";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String disease = rs.getString("disease");
                int count = rs.getInt("count");
                series.getData().add(new XYChart.Data<>(disease, count));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        chart.getData().add(series);
        return chart;
    }
    
   
   
   private Node createStaffStatusSection() {
       
    TableView<Staff> table = new TableView<>();

    TableColumn<Staff, String> nameCol = new TableColumn<>("Name");
    nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

    TableColumn<Staff, String> roleCol = new TableColumn<>("Role");
    roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

    TableColumn<Staff, String> statusCol = new TableColumn<>("Status");
    statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

    table.getColumns().addAll(nameCol, roleCol, statusCol);

    ObservableList<Staff> staffList = FXCollections.observableArrayList();

    String sql = "SELECT name, role, status FROM staff";

    try (Connection conn = connect();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            String name = rs.getString("name");
            String role = rs.getString("role");
            String status = rs.getString("status");

           staffList.add(new Staff(0, name, role, status));
        }

    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Database Error", "Could not load staff data: " + e.getMessage());
    }

    table.setItems(staffList);
    return table;
}

    
    private VBox createModernSidebar() {
        VBox sidebar = new VBox(5);
        sidebar.setPrefWidth(280);
        sidebar.setPadding(new Insets(20, 15, 20, 15));
        sidebar.setStyle("-fx-background-color: #2c3e50;");
       
        // Dashboard button
        Button dashboardBtn = createSidebarButton("📊 Dashboard", true);
        dashboardBtn.setOnAction(e -> {
            resetSidebarButtons(sidebar);
            dashboardBtn.getStyleClass().add("sidebar-button-active");
            updateMainContent(createDashboardOverview());
        });
        
        // Section separator
        Label managementLabel = new Label("MANAGEMENT");
        managementLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 15 0 5 10;");
        
        Button patientsBtn = createSidebarButton("👥 Patient Records");
        patientsBtn.setOnAction(e -> {
            resetSidebarButtons(sidebar);
            patientsBtn.getStyleClass().add("sidebar-button-active");
            updateMainContent(createPatientRecordSection());
        });
        
        Button doctorsBtn = createSidebarButton("⚕️ Doctor Management");
        doctorsBtn.setOnAction(e -> {
            resetSidebarButtons(sidebar);
            doctorsBtn.getStyleClass().add("sidebar-button-active");
            updateMainContent(createDoctorInfoSection());
        });
        
        Button appointmentsBtn = createSidebarButton("📅 Appointments");
        appointmentsBtn.setOnAction(e -> {
            resetSidebarButtons(sidebar);
            appointmentsBtn.getStyleClass().add("sidebar-button-active");
            updateMainContent(createDiseaseDoctorAssignmentSection());
        });
        
        // Analytics section
        Label analyticsLabel = new Label("ANALYTICS");
        analyticsLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 15 0 5 10;");
        
        Button statisticsBtn = createSidebarButton("📈 Statistics");
        statisticsBtn.setOnAction(e -> {
            resetSidebarButtons(sidebar);
            statisticsBtn.getStyleClass().add("sidebar-button-active");
            updateMainContent(createClinicStatsChart());
        });
        
        Button staffBtn = createSidebarButton("👨‍💼 Staff Status");
        staffBtn.setOnAction(e -> {
            resetSidebarButtons(sidebar);
            staffBtn.getStyleClass().add("sidebar-button-active");
            updateMainContent(createStaffStatusSection());
        });
        
        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        // System info
        VBox systemInfo = new VBox(5);
        systemInfo.setPadding(new Insets(15));
        systemInfo.setStyle("-fx-background-color: rgba(52, 73, 94, 0.5); -fx-background-radius: 8;");
        
        Label systemLabel = new Label("System Status");
        systemLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
        
        Label statusLabel = new Label("🟢 Online");
        statusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 11px;");
        
        Label dbLabel = new Label("Database: Connected");
        dbLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 10px;");
        
        systemInfo.getChildren().addAll(systemLabel, statusLabel, dbLabel);
        
        sidebar.getChildren().addAll(
            dashboardBtn,
            managementLabel,
            patientsBtn, doctorsBtn, appointmentsBtn,
            analyticsLabel,
            statisticsBtn, staffBtn,
            spacer,
            systemInfo
        );
        
        return sidebar;
    }
    

    
    
    
    private Button createSidebarButton(String text) {
        return createSidebarButton(text, false);
    }
    
   
   private void updateMainContent(VBox newContent) {
    if (currentContent != null) {
        BorderPane parent = (BorderPane) currentContent.getParent(); // ✅ Cast to correct parent type
        parent.setCenter(newContent); // ✅ Replace the center content
    }
    currentContent = newContent;
}


private void saveStaffToDB(String name, String role) {
    String url = "jdbc:mysql://localhost:3306/eclinic"; 
    String user = "root";
    String password = ""; 
    try (Connection conn = DriverManager.getConnection(url, user, password)) {
        String sql = "INSERT INTO staff (name, role) VALUES (?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setString(2, role);
        stmt.executeUpdate();
        System.out.println("Staff added successfully.");
    } catch (SQLException e) {
    }
}

    

        private VBox createDashboardOverview() {
            VBox dashboard = new VBox(25);
            dashboard.setPadding(new Insets(10));

            // Welcome section
            VBox welcomeSection = new VBox(10);
            Label welcomeTitle = new Label("Dashboard Overview");
            welcomeTitle.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            Label welcomeSubtitle = new Label("Manage your clinic operations efficiently");
            welcomeSubtitle.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

            welcomeSection.getChildren().addAll(welcomeTitle, welcomeSubtitle);

            // Stats cards
            HBox statsCards = new HBox(20);
            statsCards.setAlignment(Pos.CENTER);

            // Create stat cards
            VBox patientsCard = createStatCard("Total Patients", getTotalPatients(), "#3498db", "👥");
            VBox doctorsCard = createStatCard("Active Doctors", getTotalDoctors(), "#2ecc71", "⚕️");
            VBox appointmentsCard = createStatCard("Today's Appointments", getTodayAppointments(), "#e74c3c", "📅");
            statsCards.getChildren().addAll(patientsCard, doctorsCard, appointmentsCard);
         
             VBox quickActions = new VBox(15);
             Label actionsTitle = new Label("Quick Actions");
             actionsTitle.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

             HBox actionButtons = new HBox(15);
             actionButtons.setAlignment(Pos.CENTER_LEFT);

             // Create buttons
             Button addPatientBtn = createActionButton("Add New Patient", "#3498db");
             Button scheduleBtn = createActionButton("Schedule Appointment", "#2ecc71");
             Button viewReportsBtn = createActionButton("View Reports", "#e67e22");
             Button manageStaffBtn = createActionButton("Manage Staff", "#9b59b6");

            
             addPatientBtn.setOnAction(e -> {
                 System.out.println("Clicked: Add New Patient");
                 showAddPatientForm(); // Your actual logic
             });

             scheduleBtn.setOnAction(e -> {
                 System.out.println("Clicked: Schedule Appointment");
                 showScheduleAppointmentForm();
             });

             viewReportsBtn.setOnAction(e -> {
                 System.out.println("Clicked: View Reports");
                 viewReports();
             });

             manageStaffBtn.setOnAction(e -> {
                 System.out.println("Clicked: Manage Staff");
                 showManageStaffForm();
             });
             
             
            

             // Add to layout
             actionButtons.getChildren().addAll(addPatientBtn, scheduleBtn, viewReportsBtn, manageStaffBtn);
             quickActions.getChildren().addAll(actionsTitle, actionButtons);


            dashboard.getChildren().addAll(welcomeSection, statsCards, quickActions);
            return dashboard;
        }

        private VBox createStatCard(String label, String value, String color, String icon) {
            VBox card = new VBox(8);
            card.setPrefWidth(200);
            card.setPrefHeight(120);
            card.setPadding(new Insets(20));
            card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle(String.format(
                "-fx-background-color: white; " +
                "-fx-background-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2); " +
            "-fx-border-color: %s; " +
            "-fx-border-width: 0 0 3 0;", color
        ));
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle(String.format(
            "-fx-font-family: 'Segoe UI'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: %s;", color
        ));
        
        header.getChildren().addAll(iconLabel, valueLabel);
        
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; -fx-text-fill: #7f8c8d;");
        
        card.getChildren().addAll(header, labelText);
        return card;
    }
    
    private Button createActionButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefWidth(180);
        button.setPrefHeight(45);
        button.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 13px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-cursor: hand;", color
        ));
        
        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0, 0, 2);"));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle().replace("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0, 0, 2);", "")));
        
        return button;
    }
    
    // Database helper methods
    private String getTotalPatients() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM patients")) {
            if (rs.next()) {
                return String.valueOf(rs.getInt(1));
            }
        } catch (SQLException e) {
        }
        return "0";
    }
    
    private String getTotalDoctors() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM doctors")) {
            if (rs.next()) {
                return String.valueOf(rs.getInt(1));
            }
        } catch (SQLException e) {
        }
        return "0";
    }
    
    private String getTodayAppointments() {
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM appointments WHERE DATE(appointment_date) = CURDATE()")) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return String.valueOf(rs.getInt(1));
            }
        } catch (SQLException e) {
          
        }
        return "0";
    }
    
    
    
    
    
    private VBox createPatientRecordSection() {
        VBox section = new VBox(20);
        section.setPadding(new Insets(10));
        
        // Header
        Label headerLabel = new Label("Patient Records Management");
        headerLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Form section
        VBox formSection = createModernForm("Patient Information", createPatientForm());
        
        // Table section
        VBox tableSection = createPatientTable();
        
        section.getChildren().addAll(headerLabel, formSection, tableSection);
        return section;
    }
    
    private VBox createModernForm(String title, VBox formContent) {
        VBox formWrapper = new VBox(15);
        formWrapper.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);");
        formWrapper.setPadding(new Insets(25));
        
        Label formTitle = new Label(title);
        formTitle.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        formWrapper.getChildren().addAll(formTitle, formContent);
        return formWrapper;
    }
    
    private VBox createPatientForm() {
        VBox form = new VBox(15);
        
        // Create form fields with modern styling
        HBox row1 = new HBox(15);
        TextField nameField = createStyledTextField("Patient Name");
        TextField ageField = createStyledTextField("Age");
        row1.getChildren().addAll(nameField, ageField);
        
        HBox row2 = new HBox(15);
        TextField heartRateField = createStyledTextField("Heart Rate");
        TextField bloodTypeField = createStyledTextField("Blood Type");
        row2.getChildren().addAll(heartRateField, bloodTypeField);
        
        TextField reasonField = createStyledTextField("Reason for Admission");
        
        HBox row3 = new HBox(15);
        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Admission Date");
        datePicker.setStyle(getTextFieldStyle());
        
        ComboBox<String> timeCombo = new ComboBox<>();
        timeCombo.setPromptText("Admission Time");
        timeCombo.setStyle(getTextFieldStyle());
        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < 60; m += 30) {
                timeCombo.getItems().add(String.format("%02d:%02d", h, m));
            }
        }
        row3.getChildren().addAll(datePicker, timeCombo);
        
        // Buttons
        HBox buttonRow = new HBox(10);
        buttonRow.setAlignment(Pos.CENTER_LEFT);
        
        Button saveBtn = createStyledButton("Save Patient", "#2ecc71");
        Button updateBtn = createStyledButton("Update Selected", "#3498db");
        Button clearBtn = createStyledButton("Clear Form", "#95a5a6");
        
        // Add button actions
        saveBtn.setOnAction(e -> savePatient(nameField, ageField, heartRateField, bloodTypeField, reasonField, datePicker, timeCombo));
        
        buttonRow.getChildren().addAll(saveBtn, updateBtn, clearBtn);
        
        form.getChildren().addAll(row1, row2, reasonField, row3, buttonRow);
        return form;
    }
    
    private TextField createStyledTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.setStyle(getTextFieldStyle());
        field.setPrefHeight(40);
        return field;
    }
    
    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-text-fill: white; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-font-size: 13px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 6; " +
            "-fx-cursor: hand; " +
            "-fx-padding: 8 16 8 16;", color
        ));
        
        button.setOnMouseEntered(e -> {
            button.setStyle(button.getStyle() + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 1);");
        });
        button.setOnMouseExited(e -> {
            button.setStyle(button.getStyle().replace("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 1);", ""));
        });
        
        return button;
    }
    
    private String getTextFieldStyle() {
        return "-fx-background-color: #f8f9fa; " +
               "-fx-border-color: #dee2e6; " +
               "-fx-border-radius: 6; " +
               "-fx-background-radius: 6; " +
               "-fx-padding: 8 12 8 12; " +
               "-fx-font-family: 'Segoe UI'; " +
               "-fx-font-size: 13px;";
    }
    
    private VBox createPatientTable() {
        VBox tableSection = new VBox(15);
        tableSection.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);");
        tableSection.setPadding(new Insets(25));
        
        Label tableTitle = new Label("Patient Records");
        tableTitle.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        TableView<Patient> table = new TableView<>();
        table.setStyle("-fx-background-color: transparent;");
        
        // Configure table columns
        TableColumn<Patient, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);
        
        TableColumn<Patient, Integer> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        ageCol.setPrefWidth(80);
        
        TableColumn<Patient, String> heartRateCol = new TableColumn<>("Heart Rate");
        heartRateCol.setCellValueFactory(new PropertyValueFactory<>("heartRate"));
        heartRateCol.setPrefWidth(100);
        
        TableColumn<Patient, String> bloodTypeCol = new TableColumn<>("Blood Type");
        bloodTypeCol.setCellValueFactory(new PropertyValueFactory<>("bloodType"));
        bloodTypeCol.setPrefWidth(100);
        
        TableColumn<Patient, String> reasonCol = new TableColumn<>("Reason");
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));
        reasonCol.setPrefWidth(200);
        
        TableColumn<Patient, String> admissionCol = new TableColumn<>("Admission Date");
        admissionCol.setCellValueFactory(cellData -> {
            LocalDateTime dt = cellData.getValue().getAdmissionDateTime();
            String formatted = dt != null ? dt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")) : "";
            return new ReadOnlyStringWrapper(formatted);
        });
        admissionCol.setPrefWidth(150);
        
        table.getColumns().addAll(nameCol, ageCol, heartRateCol, bloodTypeCol, reasonCol, admissionCol);
        
        // Load data
        loadPatients(table);
        
        Button refreshBtn = createStyledButton("Refresh Data", "#6c757d");
        refreshBtn.setOnAction(e -> loadPatients(table));
        
        tableSection.getChildren().addAll(tableTitle, table, refreshBtn);
        return tableSection;
    }
    
    private void savePatient(TextField nameField, TextField ageField, TextField heartRateField, 
                           TextField bloodTypeField, TextField reasonField, DatePicker datePicker, ComboBox<String> timeCombo) {
        // Validation
        if (nameField.getText().trim().isEmpty() || ageField.getText().trim().isEmpty()) {
            showModernAlert("Validation Error", "Name and Age are required fields.", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            int age = Integer.parseInt(ageField.getText().trim());
            int heartRate = heartRateField.getText().trim().isEmpty() ? 0 : Integer.parseInt(heartRateField.getText().trim());
            
            LocalDate date = datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now();
            String timeStr = timeCombo.getValue() != null ? timeCombo.getValue() : "00:00";
            String[] timeParts = timeStr.split(":");
            LocalTime time = LocalTime.of(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
            LocalDateTime admissionDateTime = LocalDateTime.of(date, time);
            
            // Save to database
            try (Connection conn = connect();
                 PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO patients (name, age, heart_rate, blood_type, reason, admission_date) VALUES (?, ?, ?, ?, ?, ?)")) {
                
                stmt.setString(1, nameField.getText().trim());
                stmt.setInt(2, age);
                stmt.setString(3, String.valueOf(heartRate));
                stmt.setString(4, bloodTypeField.getText().trim());
                stmt.setString(5, reasonField.getText().trim());
                stmt.setTimestamp(6, Timestamp.valueOf(admissionDateTime));
                
                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    showModernAlert("Success", "Patient record saved successfully!", Alert.AlertType.INFORMATION);
                    // Clear form
                    nameField.clear();
                    ageField.clear();
                    heartRateField.clear();
                    bloodTypeField.clear();
                    reasonField.clear();
                    datePicker.setValue(null);
                    timeCombo.setValue(null);
                } else {
                    showModernAlert("Error", "Failed to save patient record.", Alert.AlertType.ERROR);
                }
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                showModernAlert("Database Error", "Error connecting to database: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
            
        } catch (NumberFormatException ex) {
            showModernAlert("Validation Error", "Age and Heart Rate must be valid numbers.", Alert.AlertType.WARNING);
        }
    }
    
    private void showModernAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-family: 'Segoe UI'; -fx-background-color: white;");
        
        alert.showAndWait();
    }
    
    
    
    // Implement other sections with similar modern styling...
   private VBox createDoctorInfoSection() {
    VBox section = new VBox(20);
    section.setPadding(new Insets(20));
    section.setStyle("-fx-background-color: #f4f6f7;");

    Label headerLabel = new Label("Doctor Management");
    headerLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

    // Form Inputs
    TextField nameField = new TextField();
    nameField.setPromptText("Doctor Name");

    TextField specialtyField = new TextField();
    specialtyField.setPromptText("Specialization");

    TextField contactField = new TextField();
    contactField.setPromptText("Contact Number");

    HBox form = new HBox(10, nameField, specialtyField, contactField);
    form.setAlignment(Pos.CENTER_LEFT);

    // Buttons
    Button addButton = new Button("Add");
    Button updateButton = new Button("Update");
    Button deleteButton = new Button("Delete");

    HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton);
    buttonBox.setAlignment(Pos.CENTER_LEFT);

    // TableView
    TableView<Doctor> doctorTable = new TableView<>();
    TableColumn<Doctor, String> nameCol = new TableColumn<>("Name");
    nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

    TableColumn<Doctor, String> specialtyCol = new TableColumn<>("Specialization");
    specialtyCol.setCellValueFactory(new PropertyValueFactory<>("specialization"));

    TableColumn<Doctor, String> contactCol = new TableColumn<>("Contact");
    contactCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

    doctorTable.getColumns().addAll(nameCol, specialtyCol, contactCol);
    doctorTable.setPrefHeight(200);

    // ✅ Fetch data safely from DB
    ObservableList<Doctor> doctorData = FXCollections.observableArrayList();
    try {
        doctorData.addAll(Doctor.fetchAllFromDatabase());
    } catch (Exception ex) {
        showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load doctors from database:\n" + ex.getMessage());
    }
    doctorTable.setItems(doctorData);

    // ➕ Add button action
    addButton.setOnAction(e -> {
        Doctor doctor = new Doctor(
            nameField.getText(),
            specialtyField.getText(),
            contactField.getText()
        );
        doctorData.add(doctor);
        nameField.clear();
        specialtyField.clear();
        contactField.clear();
    });

    // ❌ Delete button action
    deleteButton.setOnAction(e -> {
        Doctor selected = doctorTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            doctorData.remove(selected);
        }
    });

    // ✏️ Update button action
    updateButton.setOnAction(e -> {
        Doctor selected = doctorTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setName(nameField.getText());
            selected.setSpecialty(specialtyField.getText());
            selected.setContact(contactField.getText());
            doctorTable.refresh();
        }
    });

    // 🖱️ Click-to-fill
    doctorTable.setOnMouseClicked(e -> {
        Doctor selected = doctorTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            nameField.setText(selected.getName());
            specialtyField.setText(selected.getSpecialty());
            contactField.setText(selected.getContact());
        }
    });

    section.getChildren().addAll(headerLabel, form, buttonBox, doctorTable);
    return section;
}
       


// Helper method to show alerts
private void showAlert(Alert.AlertType type, String title, String message) {
    Alert alert = new Alert(type);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    
    // Style the alert dialog
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.setStyle("-fx-font-family: 'Segoe UI'; -fx-background-color: white;");
    
    alert.showAndWait();
}

        
        
        
        
   
  private VBox createDiseaseDoctorAssignmentSection() {
    VBox box = new VBox(20);
    box.setPadding(new Insets(20));
    
    // Header
    Label headerLabel = new Label("Disease-Doctor Assignment");
    headerLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
    
    // Create form in a styled container
    VBox formContainer = new VBox(20);
    formContainer.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);");
    formContainer.setPadding(new Insets(30));
    
    Label formTitle = new Label("Schedule Appointment");
    formTitle.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
    
    // Specialization selection
    VBox specSection = new VBox(8);
    Label diseaseLabel = new Label("Select Doctor Specialization:");
    diseaseLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
    
    diseaseComboBox = new ComboBox<>();
    diseaseComboBox.setPromptText("Choose Specialization");
    diseaseComboBox.setEditable(true);
    diseaseComboBox.setStyle(getTextFieldStyle());
    diseaseComboBox.setPrefHeight(40);
    diseaseComboBox.setMaxWidth(Double.MAX_VALUE);
    
    loadSpecializations(diseaseComboBox);
    specSection.getChildren().addAll(diseaseLabel, diseaseComboBox);
    
    // Date and time selection
    HBox dateTimeRow = new HBox(15);
    
    VBox dateSection = new VBox(8);
    Label dateLabel = new Label("Select Date:");
    dateLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
    
    appointmentDatePicker = new DatePicker();
    appointmentDatePicker.setPromptText("Choose Date");
    appointmentDatePicker.setStyle(getTextFieldStyle());
    appointmentDatePicker.setPrefHeight(40);
    appointmentDatePicker.setValue(LocalDate.now()); // Default to today
    
    dateSection.getChildren().addAll(dateLabel, appointmentDatePicker);
    
    VBox timeSection = new VBox(8);
    Label timeLabel = new Label("Select Time:");
    timeLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
    
    timeComboBox = new ComboBox<>();
    timeComboBox.setPromptText("Choose Time");
    timeComboBox.setStyle(getTextFieldStyle());
    timeComboBox.setPrefHeight(40);
    populateTimeSlots();
    
    timeSection.getChildren().addAll(timeLabel, timeComboBox);
    
    dateTimeRow.getChildren().addAll(dateSection, timeSection);
    HBox.setHgrow(dateSection, Priority.ALWAYS);
    HBox.setHgrow(timeSection, Priority.ALWAYS);
    
    // Doctor assignment display
    VBox doctorSection = new VBox(8);
    Label doctorTitle = new Label("Available Doctor:");
    doctorTitle.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
    
    assignedDoctorLabel = new Label("No doctor assigned yet");
    assignedDoctorLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-text-fill: #7f8c8d; -fx-padding: 10; -fx-background-color: #f8f9fa; -fx-background-radius: 6;");
    
    doctorSection.getChildren().addAll(doctorTitle, assignedDoctorLabel);
    
    // Buttons
    HBox buttonRow = new HBox(15);
    buttonRow.setAlignment(Pos.CENTER_LEFT);
    
    Button checkDoctorButton = createStyledButton("Check Available Doctor", "#3498db");
    confirmButton = createStyledButton("Confirm Appointment", "#2ecc71");
    Button cancelButton = createStyledButton("Clear Form", "#95a5a6");
    
    confirmButton.setDisable(true);
    
    buttonRow.getChildren().addAll(checkDoctorButton, confirmButton, cancelButton);
    
    // Event handlers
    checkDoctorButton.setOnAction(e -> checkAvailableDoctor());
    confirmButton.setOnAction(e -> confirmAppointment());
    cancelButton.setOnAction(e -> resetAppointmentForm());
    
    // Update available times when specialization changes
    diseaseComboBox.setOnAction(e -> updateAvailableTimes());
    
    formContainer.getChildren().addAll(
        formTitle, 
        specSection, 
        dateTimeRow, 
        doctorSection, 
        buttonRow
    );
    
    // Appointments table
    VBox tableSection = createAppointmentsTable();
    
    box.getChildren().addAll(headerLabel, formContainer, tableSection);
    return box;
}

private void populateTimeSlots() {
    timeComboBox.getItems().clear();
    // Add time slots from 8 AM to 6 PM
    for (int hour = 8; hour <= 18; hour++) {
        timeComboBox.getItems().add(String.format("%02d:00", hour));
        if (hour < 18) {
            timeComboBox.getItems().add(String.format("%02d:30", hour));
        }
    }
}

private void updateAvailableTimes() {
    String selectedSpec = diseaseComboBox.getValue();
    if (selectedSpec != null && !selectedSpec.trim().isEmpty()) {
        List<String> availableTimes = getAvailableTimes(selectedSpec);
        timeComboBox.getItems().clear();
        if (!availableTimes.isEmpty()) {
            timeComboBox.getItems().addAll(availableTimes);
        } else {
            populateTimeSlots(); // Use default time slots if no specific times found
        }
    }
}

private void checkAvailableDoctor() {
    String selectedSpec = diseaseComboBox.getValue();
    LocalDate selectedDate = appointmentDatePicker.getValue();
    String selectedTime = timeComboBox.getValue();
    
    if (selectedSpec == null || selectedSpec.trim().isEmpty()) {
        showAlert(Alert.AlertType.WARNING, "Input Required", "Please select a specialization.");
        return;
    }
    
    if (selectedDate == null) {
        showAlert(Alert.AlertType.WARNING, "Input Required", "Please select a date.");
        return;
    }
    
    if (selectedTime == null || selectedTime.trim().isEmpty()) {
        showAlert(Alert.AlertType.WARNING, "Input Required", "Please select a time.");
        return;
    }
    
    // Check if date is not in the past
    if (selectedDate.isBefore(LocalDate.now())) {
        showAlert(Alert.AlertType.WARNING, "Invalid Date", "Please select a future date.");
        return;
    }
    
    
    
    
    Doctor availableDoctor = doctorService.getAvailableDoctorBySpecialization(selectedSpec, selectedDate, selectedTime);
    if (availableDoctor != null) {
        assignedDoctorLabel.setText("✅ Dr. " + availableDoctor.getName() + " (" + availableDoctor.getSpecialization() + ")");
        assignedDoctorLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-text-fill: #2ecc71; -fx-padding: 10; -fx-background-color: #d4edda; -fx-background-radius: 6;");
        confirmButton.setDisable(false);
    } else {
        assignedDoctorLabel.setText("❌ No available doctors for " + selectedSpec + " at " + selectedTime);
        assignedDoctorLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-text-fill: #e74c3c; -fx-padding: 10; -fx-background-color: #f8d7da; -fx-background-radius: 6;");
        confirmButton.setDisable(true);
    }
}


private DoctorService doctorService = new DoctorService();
private void confirmAppointment() {
    String doctorText = assignedDoctorLabel.getText();
    if (doctorText.contains("❌") || doctorText.contains("No doctor assigned")) {
        showAlert(Alert.AlertType.WARNING, "No Doctor Assigned", "Please check and assign a doctor first.");
        return;
    }
    
    // Extract doctor name from the label
    String doctorName = doctorText.replace("✅ Dr. ", "").split(" \\(")[0];
    
    boolean booked = bookAppointment(
        doctorName, 
        diseaseComboBox.getValue(), 
        appointmentDatePicker.getValue(), 
        timeComboBox.getValue()
    );
    
    if (booked) {
        showAlert(Alert.AlertType.INFORMATION, "Success", 
                 "Appointment successfully booked with Dr. " + doctorName);
        resetAppointmentForm();
        refreshAppointmentsTable(); // Refresh the appointments table
    } else {
        showAlert(Alert.AlertType.ERROR, "Booking Failed", 
                 "Failed to book the appointment. Please try again.");
    }
}

private void resetAppointmentForm() {
    diseaseComboBox.setValue(null);
    appointmentDatePicker.setValue(LocalDate.now());
    timeComboBox.setValue(null);
    assignedDoctorLabel.setText("No doctor assigned yet");
    assignedDoctorLabel.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-text-fill: #7f8c8d; -fx-padding: 10; -fx-background-color: #f8f9fa; -fx-background-radius: 6;");
    confirmButton.setDisable(true);
}

// Fixed booking method with proper error handling
private boolean bookAppointment(String doctorName, String specialization, LocalDate date, String time) {
    String checkSQL = "SELECT COUNT(*) FROM appointments WHERE doctor = ? AND appointment_date = ? AND appointment_time = ?";
    String insertSQL = "INSERT INTO appointments (doctor, specialization, appointment_date, appointment_time, status, created_at) VALUES (?, ?, ?, ?, 'SCHEDULED', NOW())";
    
    try (Connection conn = connect()) {
        if (conn == null) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Cannot connect to database.");
            return false;
        }
        
        // Check if the time slot is already taken
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {
            checkStmt.setString(1, doctorName);
            checkStmt.setDate(2, java.sql.Date.valueOf(date));
            checkStmt.setString(3, time);
            
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                showAlert(Alert.AlertType.WARNING, "Time Slot Taken", 
                         "This time slot is already booked. Please choose another time.");
                return false;
            }
        }
        
        // Insert the new appointment
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
            insertStmt.setString(1, doctorName);
            insertStmt.setString(2, specialization);
            insertStmt.setDate(3, java.sql.Date.valueOf(date));
            insertStmt.setString(4, time);
            
            int rows = insertStmt.executeUpdate();
            return rows > 0;
        }
        
    } catch (SQLException e) {
        System.err.println("Error booking appointment: " + e.getMessage());
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Database Error", 
                 "An error occurred while booking the appointment: " + e.getMessage());
        return false;
    }
}

// Create appointments table to show scheduled appointments
private VBox createAppointmentsTable() {
    VBox tableSection = new VBox(15);
    tableSection.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0, 0, 2);");
    tableSection.setPadding(new Insets(25));
    
    Label tableTitle = new Label("Scheduled Appointments");
    tableTitle.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
    
    TableView<Appointment> appointmentsTable = new TableView<>();
    appointmentsTable.setStyle("-fx-background-color: transparent;");
    
    // Create columns
    TableColumn<Appointment, String> doctorCol = new TableColumn<>("Doctor");
    doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctor"));
    doctorCol.setPrefWidth(150);
    
    TableColumn<Appointment, String> specCol = new TableColumn<>("Specialization");
    specCol.setCellValueFactory(new PropertyValueFactory<>("specialization"));
    specCol.setPrefWidth(150);
    
    TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
    dateCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
    dateCol.setPrefWidth(120);
    
    TableColumn<Appointment, String> timeCol = new TableColumn<>("Time");
    timeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
    timeCol.setPrefWidth(100);
    
    TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
    statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
    statusCol.setPrefWidth(100);
    
    appointmentsTable.getColumns().addAll(doctorCol, specCol, dateCol, timeCol, statusCol);
    
    // Load appointments data
    loadAppointments(appointmentsTable);
    
    Button refreshBtn = createStyledButton("Refresh", "#6c757d");
    refreshBtn.setOnAction(e -> loadAppointments(appointmentsTable));
    
    tableSection.getChildren().addAll(tableTitle, appointmentsTable, refreshBtn);
    return tableSection;
}

private void loadAppointments(TableView<Appointment> table) {
    ObservableList<Appointment> appointments = FXCollections.observableArrayList();
    
    String sql = "SELECT doctor, specialization, appointment_date, appointment_time, status FROM appointments ORDER BY appointment_date DESC, appointment_time DESC LIMIT 20";
    
    try (Connection conn = connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        while (rs.next()) {
            Appointment appointment = new Appointment(
                rs.getString("doctor"),
                rs.getString("specialization"),
                rs.getDate("appointment_date").toString(),
                rs.getString("appointment_time"),
                rs.getString("status")
            );
            appointments.add(appointment);
        }
        
        table.setItems(appointments);
        
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Database Error", 
                 "Failed to load appointments: " + e.getMessage());
    }
}

private void refreshAppointmentsTable() {
   
    
}

// Improved load patients method
private void loadPatients(TableView<Patient> table) {
    ObservableList<Patient> patients = FXCollections.observableArrayList();

    String sql = "SELECT name, age, heart_rate, blood_type, reason, admission_datetime FROM patients ORDER BY admission_datetime DESC";

    try (Connection conn = connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            Patient patient = new Patient(
                rs.getString("name"),
                rs.getInt("age"),
                rs.getInt("heart_rate"),
                rs.getString("blood_type"),
                rs.getString("reason"),
                rs.getTimestamp("admission_datetime").toLocalDateTime()
            );
            patients.add(patient);
        }

        table.setItems(patients);

    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Database Error", 
                 "Failed to load patients: " + e.getMessage());
    }
}

// Fixed specializations loading
private void loadSpecializations(ComboBox<String> comboBox) {
    comboBox.getItems().clear();

    String sql = "SELECT DISTINCT specialization FROM doctors WHERE specialization IS NOT NULL AND specialization != ''";

    try (Connection conn = connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            String specialization = rs.getString("specialization");
            if (!comboBox.getItems().contains(specialization)) {
                comboBox.getItems().add(specialization);
            }
        }

        if (comboBox.getItems().isEmpty()) {
            comboBox.getItems().addAll(
                "General Medicine", "Cardiology", "Neurology",
                "Orthopedics", "Pediatrics", "Dermatology"
            );
        }

    } catch (SQLException e) {
        e.printStackTrace();
        comboBox.getItems().addAll(
            "General Medicine", "Cardiology", "Neurology",
            "Orthopedics", "Pediatrics", "Dermatology"
        );
    }
}
// Fixed get available times method
private List<String> getAvailableTimes(String specialization) {
    List<String> times = new ArrayList<>();

    String sql = "SELECT DISTINCT availability FROM doctors WHERE specialization = ? AND availability IS NOT NULL";

    try (Connection conn = connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, specialization);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String availability = rs.getString("availability");
            if (availability != null && !availability.trim().isEmpty()) {
                String[] timeSlots = availability.split(",");
                for (String time : timeSlots) {
                    String trimmedTime = time.trim();
                    if (!times.contains(trimmedTime)) {
                        times.add(trimmedTime);
                    }
                }
            }
        }

    } catch (SQLException e) {
    }
    return times;
}




  public static void main(String[] args) {
    launch(args);
}
}

