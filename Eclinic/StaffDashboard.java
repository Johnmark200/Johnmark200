package Eclinic;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;

public class StaffDashboard extends Application {
    
    private BorderPane mainLayout;
    private VBox sidebar;
    private VBox contentArea;
    private String currentView = "dashboard";
    
    // Colors for professional design
    private static final String PRIMARY_COLOR = "#2E86AB";
    private static final String SECONDARY_COLOR = "#A23B72";
    private static final String ACCENT_COLOR = "#F18F01";
    private static final String BACKGROUND_COLOR = "#F8F9FA";
    private static final String CARD_COLOR = "#FFFFFF";
    private static final String TEXT_PRIMARY = "#2C3E50";
    private static final String TEXT_SECONDARY = "#6C757D";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Professional Nurse Dashboard - eClinic System");
        
        // Create main layout
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
        
        // Create header
        createHeader();
        
        // Create sidebar
        createSidebar();
        
        // Create content area
        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(30));
        
        // Show dashboard by default
        showDashboard();
        
        // Set up main layout
        mainLayout.setTop(createHeader());
        mainLayout.setLeft(sidebar);
        mainLayout.setCenter(new ScrollPane(contentArea));
        
        Scene scene = new Scene(mainLayout, 1400, 800);
      
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private HBox createHeader() {
        HBox header = new HBox();
        header.setPrefHeight(80);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 30, 0, 30));
        
        // Create gradient background
        LinearGradient gradient = new LinearGradient(
            0, 0, 1, 0, true, null,
            new Stop(0, Color.web(PRIMARY_COLOR)),
            new Stop(1, Color.web(SECONDARY_COLOR))
        );
        
        header.setStyle("-fx-background-color: linear-gradient(to right, " + PRIMARY_COLOR + ", " + SECONDARY_COLOR + ");");
        
        // Drop shadow effect
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.gray(0.5, 0.3));
        shadow.setRadius(10);
        header.setEffect(shadow);
        
        // Title
        Label title = new Label("🏥 eClinic Nurse Dashboard");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold; -fx-font-family: 'Segoe UI';");
        
        // User info
        VBox userInfo = new VBox(5);
        userInfo.setAlignment(Pos.CENTER_RIGHT);
        Label nurseNameLabel = new Label("Nurse: Maria Santos, RN");
        Label shiftLabel = new Label("Day Shift | Room 201-205");
        nurseNameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        shiftLabel.setStyle("-fx-text-fill: #E8F4FD; -fx-font-size: 12px;");
        userInfo.getChildren().addAll(nurseNameLabel, shiftLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        header.getChildren().addAll(title, spacer, userInfo);
        return header;
    }
    
    private void createSidebar() {
        sidebar = new VBox(10);
        sidebar.setPrefWidth(280);
        sidebar.setPadding(new Insets(30, 20, 30, 20));
        sidebar.setStyle("-fx-background-color: white; -fx-border-color: #E9ECEF; -fx-border-width: 0 1 0 0;");
        
        // Sidebar title
        Label sidebarTitle = new Label("Navigation");
        sidebarTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-family: 'Segoe UI';");
        
        // Navigation buttons
        Button dashboardBtn = createNavButton("📊 Dashboard", "dashboard");
        Button patientRegBtn = createNavButton("👥 Patient Registration", "registration");
        Button patientListBtn = createNavButton("📋 Patient List", "patients");
        Button scheduleBtn = createNavButton("📅 My Schedule", "schedule");
        Button assignedPatientsBtn = createNavButton("🩺 Assigned Patients", "assigned");
        
        // Set dashboard as active initially
        dashboardBtn.getStyleClass().add("nav-button-active");
        
        sidebar.getChildren().addAll(
            sidebarTitle,
            new Separator(),
            dashboardBtn,
            patientRegBtn,
            patientListBtn,
            scheduleBtn,
            assignedPatientsBtn
        );
    }
    
    private Button createNavButton(String text, String view) {
        Button btn = new Button(text);
        btn.setPrefWidth(240);
        btn.setPrefHeight(45);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: " + TEXT_PRIMARY + "; " +
            "-fx-font-size: 14px; " +
            "-fx-font-family: 'Segoe UI'; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 12 20 12 20;"
        );
        
        btn.setOnMouseEntered(e -> {
            if (!btn.getStyleClass().contains("nav-button-active")) {
                btn.setStyle(btn.getStyle() + "-fx-background-color: " + BACKGROUND_COLOR + ";");
            }
        });
        
        btn.setOnMouseExited(e -> {
            if (!btn.getStyleClass().contains("nav-button-active")) {
                btn.setStyle(btn.getStyle().replace("-fx-background-color: " + BACKGROUND_COLOR + ";", ""));
            }
        });
        
        btn.setOnAction(e -> {
            // Remove active class from all buttons
            sidebar.getChildren().forEach(node -> {
                if (node instanceof Button) {
                    ((Button) node).getStyleClass().remove("nav-button-active");
                    ((Button) node).setStyle(((Button) node).getStyle().replace("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white;", ""));
                }
            });
            
            // Add active class to clicked button
            btn.getStyleClass().add("nav-button-active");
            btn.setStyle(btn.getStyle() + "-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white;");
            
            currentView = view;
            switchView(view);
        });
        
        return btn;
    }
    
    private void switchView(String view) {
        contentArea.getChildren().clear();
        
        switch (view) {
            case "dashboard":
                showDashboard();
                break;
            case "registration":
                showPatientRegistration();
                break;
            case "patients":
                showPatientList();
                break;
            case "schedule":
                showSchedule();
                break;
            case "assigned":
                showAssignedPatients();
                break;
        }
    }
    
    private void showDashboard() {
        // Welcome section
        VBox welcomeSection = createCard("Welcome Back, Nurse Maria!", "");
        Label welcomeText = new Label("Here's your overview for today");
        welcomeText.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_SECONDARY + ";");
        welcomeSection.getChildren().add(welcomeText);
        
        // Stats cards
        HBox statsRow = new HBox(20);
        statsRow.getChildren().addAll(
            createStatCard("👥 Patients Today", "12", PRIMARY_COLOR),
            createStatCard("⏰ Next Appointment", "2:30 PM", SECONDARY_COLOR),
            createStatCard("🏥 Assigned Rooms", "201-205", ACCENT_COLOR),
            createStatCard("🩺 Pending Reviews", "3", "#E74C3C")
        );
        
        // Quick actions
        VBox quickActions = createCard("Quick Actions", "");
        HBox actionButtons = new HBox(15);
        Button newPatientBtn = createActionButton("+ New Patient", PRIMARY_COLOR);
        Button viewPatientsBtn = createActionButton("📋 View Patients", SECONDARY_COLOR);
        Button checkScheduleBtn = createActionButton("📅 Schedule", ACCENT_COLOR);
        
        newPatientBtn.setOnAction(e -> switchView("registration"));
        viewPatientsBtn.setOnAction(e -> switchView("patients"));
        checkScheduleBtn.setOnAction(e -> switchView("schedule"));
        
        actionButtons.getChildren().addAll(newPatientBtn, viewPatientsBtn, checkScheduleBtn);
        quickActions.getChildren().add(actionButtons);
        
        contentArea.getChildren().addAll(welcomeSection, statsRow, quickActions);
    }
    
    private void showPatientRegistration() {
        VBox registrationCard = createCard("Patient Registration", "Complete patient information for medical record");
        
        // Create form
        GridPane form = new GridPane();
        form.setHgap(20);
        form.setVgap(15);
        form.setPadding(new Insets(20, 0, 0, 0));
        
        // Patient Information Section
        Label personalInfoLabel = new Label("Personal Information");
        personalInfoLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_COLOR + ";");
        form.add(personalInfoLabel, 0, 0, 2, 1);
        
        // Form fields
        TextField nameField = createFormField("Full Name");
        TextField ageField = createFormField("Age");
        ComboBox<String> sexField = new ComboBox<>();
        sexField.getItems().addAll("Male", "Female");
        sexField.setPromptText("Select Sex");
        sexField.setPrefWidth(200);
        
        TextField addressField = createFormField("Address");
        DatePicker birthDatePicker = new DatePicker();
        birthDatePicker.setPrefWidth(200);
        
        // Vital Signs Section
        Label vitalSignsLabel = new Label("Vital Signs & Health Information");
        vitalSignsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_COLOR + ";");
        
        TextField bpField = createFormField("Blood Pressure (e.g., 120/80)");
        TextField tempField = createFormField("Temperature (°C)");
        TextField heightField = createFormField("Height (cm)");
        TextField weightField = createFormField("Weight (kg)");
        TextField heartbeatField = createFormField("Heart Rate (bpm)");
        TextField sugarLevelField = createFormField("Sugar Level (if diabetic)");
        
        // Symptoms and Pain
        Label symptomsLabel = new Label("Symptoms & Pain Assessment");
        symptomsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_COLOR + ";");
        
        TextArea currentSymptomsArea = new TextArea();
        currentSymptomsArea.setPromptText("Current symptoms...");
        currentSymptomsArea.setPrefRowCount(3);
        currentSymptomsArea.setPrefWidth(400);
        
        TextArea previousSymptomsArea = new TextArea();
        previousSymptomsArea.setPromptText("Previous symptoms/medical history...");
        previousSymptomsArea.setPrefRowCount(3);
        previousSymptomsArea.setPrefWidth(400);
        
        ComboBox<String> painLevelField = new ComboBox<>();
        painLevelField.getItems().addAll("0 - No Pain", "1-2 - Mild", "3-4 - Moderate", "5-6 - Severe", "7-8 - Very Severe", "9-10 - Worst Possible");
        painLevelField.setPromptText("Select Pain Level");
        painLevelField.setPrefWidth(200);
        
        // Add fields to form
        int row = 1;
        form.add(new Label("Name:"), 0, row);
        form.add(nameField, 1, row++);
        form.add(new Label("Age:"), 0, row);
        form.add(ageField, 1, row++);
        form.add(new Label("Sex:"), 0, row);
        form.add(sexField, 1, row++);
        form.add(new Label("Address:"), 0, row);
        form.add(addressField, 1, row++);
        form.add(new Label("Birth Date:"), 0, row);
        form.add(birthDatePicker, 1, row++);
        
        form.add(vitalSignsLabel, 0, row++, 2, 1);
        form.add(new Label("Blood Pressure:"), 0, row);
        form.add(bpField, 1, row++);
        form.add(new Label("Temperature:"), 0, row);
        form.add(tempField, 1, row++);
        form.add(new Label("Height:"), 0, row);
        form.add(heightField, 1, row++);
        form.add(new Label("Weight:"), 0, row);
        form.add(weightField, 1, row++);
        form.add(new Label("Heart Rate:"), 0, row);
        form.add(heartbeatField, 1, row++);
        form.add(new Label("Sugar Level:"), 0, row);
        form.add(sugarLevelField, 1, row++);
        
        form.add(symptomsLabel, 0, row++, 2, 1);
        form.add(new Label("Current Symptoms:"), 0, row);
        form.add(currentSymptomsArea, 1, row++);
        form.add(new Label("Previous Symptoms:"), 0, row);
        form.add(previousSymptomsArea, 1, row++);
        form.add(new Label("Pain Level:"), 0, row);
        form.add(painLevelField, 1, row++);
        
        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(30, 0, 0, 0));
        
        Button saveButton = createActionButton("💾 Save & Send to Doctor", PRIMARY_COLOR);
        Button clearButton = createActionButton("🗑️ Clear Form", TEXT_SECONDARY);
        
        saveButton.setOnAction(e -> {
            // Here you would save to database and send to doctor
            showAlert("Success", "Patient registered successfully and sent to doctor for review!", Alert.AlertType.INFORMATION);
            // Clear form after successful save
            clearForm(form);
        });
        
        clearButton.setOnAction(e -> clearForm(form));
        
        buttonBox.getChildren().addAll(saveButton, clearButton);
        
        registrationCard.getChildren().addAll(form, buttonBox);
        contentArea.getChildren().add(registrationCard);
    }
    
    private void showPatientList() {
        VBox patientsCard = createCard("Patient List", "All registered patients");
        
        TableView<Patient> table = new TableView<>();
        table.setPrefHeight(400);
        
        // Create columns
        TableColumn<Patient, String> nameCol = new TableColumn<>("Patient Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(150);
        
        TableColumn<Patient, Integer> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        ageCol.setPrefWidth(60);
        
        TableColumn<Patient, String> sexCol = new TableColumn<>("Sex");
        sexCol.setCellValueFactory(new PropertyValueFactory<>("sex"));
        sexCol.setPrefWidth(70);
        
        TableColumn<Patient, String> symptomsCol = new TableColumn<>("Current Symptoms");
        symptomsCol.setCellValueFactory(new PropertyValueFactory<>("currentSymptoms"));
        symptomsCol.setPrefWidth(200);
        
        TableColumn<Patient, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(120);
        
        TableColumn<Patient, String> painLevelCol = new TableColumn<>("Pain Level");
        painLevelCol.setCellValueFactory(new PropertyValueFactory<>("painLevel"));
        painLevelCol.setPrefWidth(100);
        
        table.getColumns().addAll(nameCol, ageCol, sexCol, symptomsCol, statusCol, painLevelCol);
        
        // Load sample data (replace with database query)
        table.setItems(loadPatientData());
        
        patientsCard.getChildren().add(table);
        contentArea.getChildren().add(patientsCard);
    }
    
    private void showSchedule() {
        VBox scheduleCard = createCard("My Schedule", "Your assigned shifts and duties");
        
        // Today's schedule
        Label todayLabel = new Label("Today's Schedule - " + LocalDate.now().toString());
        todayLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_COLOR + ";");
        
        VBox scheduleItems = new VBox(10);
        scheduleItems.setPadding(new Insets(15, 0, 0, 0));
        
        scheduleItems.getChildren().addAll(
            createScheduleItem("06:00 - 14:00", "Day Shift", "Rooms 201-205", PRIMARY_COLOR),
            createScheduleItem("08:00", "Patient Rounds", "Check vitals for assigned patients", SECONDARY_COLOR),
            createScheduleItem("10:30", "Medication Distribution", "Room 203, 204", ACCENT_COLOR),
            createScheduleItem("14:00", "Shift Report", "Handover to evening shift", TEXT_SECONDARY)
        );
        
        scheduleCard.getChildren().addAll(todayLabel, scheduleItems);
        contentArea.getChildren().add(scheduleCard);
    }
    
    private void showAssignedPatients() {
        VBox assignedCard = createCard("Assigned Patients", "Patients under your care");
        
        // Room assignments
        VBox roomAssignments = new VBox(15);
        
        roomAssignments.getChildren().addAll(
            createRoomCard("Room 201", "John Doe, 45, Male", "Post-surgery recovery", "Stable"),
            createRoomCard("Room 202", "Maria Garcia, 32, Female", "Pneumonia treatment", "Improving"),
            createRoomCard("Room 203", "Robert Chen, 58, Male", "Diabetes management", "Stable"),
            createRoomCard("Room 204", "Sarah Johnson, 67, Female", "Hypertension monitoring", "Critical"),
            createRoomCard("Room 205", "Empty", "Available for new patient", "Available")
        );
        
        assignedCard.getChildren().add(roomAssignments);
        contentArea.getChildren().add(assignedCard);
    }
    
    // Helper methods for UI components
    private VBox createCard(String title, String subtitle) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(25));
        card.setStyle(
            "-fx-background-color: " + CARD_COLOR + "; " +
            "-fx-background-radius: 12; " +
            "-fx-border-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-family: 'Segoe UI';");
        
        if (!subtitle.isEmpty()) {
            Label subtitleLabel = new Label(subtitle);
            subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_SECONDARY + ";");
            card.getChildren().addAll(titleLabel, subtitleLabel);
        } else {
            card.getChildren().add(titleLabel);
        }
        
        return card;
    }
    
    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setPrefWidth(200);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: " + CARD_COLOR + "; " +
            "-fx-background-radius: 12; " +
            "-fx-border-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + "; -fx-font-family: 'Segoe UI';");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-family: 'Segoe UI';");
        
        card.getChildren().addAll(valueLabel, titleLabel);
        return card;
    }
    
    private Button createActionButton(String text, String color) {
        Button btn = new Button(text);
        btn.setPrefHeight(40);
        btn.setPadding(new Insets(10, 20, 10, 20));
        btn.setStyle(
            "-fx-background-color: " + color + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 8; " +
            "-fx-font-family: 'Segoe UI';"
        );
        
        btn.setOnMouseEntered(e -> btn.setStyle(btn.getStyle() + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);"));
        btn.setOnMouseExited(e -> btn.setStyle(btn.getStyle().replace("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);", "")));
        
        return btn;
    }
    
    private TextField createFormField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(300);
        field.setPrefHeight(35);
        field.setStyle("-fx-background-radius: 6; -fx-border-radius: 6; -fx-border-color: #DDD;");
        return field;
    }
    
    private HBox createScheduleItem(String time, String task, String details, String color) {
        HBox item = new HBox(15);
        item.setPadding(new Insets(15));
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle(
            "-fx-background-color: " + CARD_COLOR + "; " +
            "-fx-background-radius: 8; " +
            "-fx-border-left-color: " + color + "; " +
            "-fx-border-left-width: 4; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);"
        );
        
        Label timeLabel = new Label(time);
        timeLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + color + "; -fx-min-width: 80;");
        
        VBox taskInfo = new VBox(3);
        Label taskLabel = new Label(task);
        taskLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
        Label detailsLabel = new Label(details);
        detailsLabel.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 12px;");
        taskInfo.getChildren().addAll(taskLabel, detailsLabel);
        
        item.getChildren().addAll(timeLabel, taskInfo);
        return item;
    }
    
    private HBox createRoomCard(String room, String patient, String condition, String status) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: " + CARD_COLOR + "; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);"
        );
        
        VBox roomInfo = new VBox(5);
        Label roomLabel = new Label(room);
        roomLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_COLOR + ";");
        Label patientLabel = new Label(patient);
        patientLabel.setStyle("-fx-text-fill: " + TEXT_PRIMARY + ";");
        Label conditionLabel = new Label(condition);
        conditionLabel.setStyle("-fx-text-fill: " + TEXT_SECONDARY + "; -fx-font-size: 12px;");
        roomInfo.getChildren().addAll(roomLabel, patientLabel, conditionLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label statusLabel = new Label(status);
        String statusColor = status.equals("Critical") ? "#E74C3C" : 
                           status.equals("Stable") ? "#27AE60" : 
                           status.equals("Improving") ? "#F39C12" : TEXT_SECONDARY;
        statusLabel.setStyle("-fx-text-fill: " + statusColor + "; -fx-font-weight: bold;");
        
        card.getChildren().addAll(roomInfo, spacer, statusLabel);
        return card;
    }
    
    private void clearForm(GridPane form) {
        form.getChildren().forEach(node -> {
            if (node instanceof TextField) {
                ((TextField) node).clear();
            } else if (node instanceof TextArea) {
                ((TextArea) node).clear();
            } else if (node instanceof ComboBox) {
                ((ComboBox<?>) node).getSelectionModel().clearSelection();
            } else if (node instanceof DatePicker) {
                ((DatePicker) node).setValue(null);
            }
        });
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private ObservableList<Patient> loadPatientData() {
        ObservableList<Patient> list = FXCollections.observableArrayList();
        
        // Sample data - replace with actual database query
        list.addAll(
            new Patient("John Doe", 45, "Male", "Chest pain, shortness of breath", "Under Review", "5-6 Severe"),
            new Patient("Maria Garcia", 32, "Female", "Fever, cough", "Diagnosed", "2-3 Mild"),
            new Patient("Robert Chen", 58, "Male", "High blood sugar", "Treatment", "1-2 Mild"),
            new Patient("Sarah Johnson", 67, "Female", "High blood pressure", "Critical", "7-8 Very Severe")
        );
        
        return list;
    }
    
    // Patient class for TableView
    public static class Patient {
        private String name;
        private int age;
        private String sex;
        private String currentSymptoms;
        private String status;
        private String painLevel;
        
        public Patient(String name, int age, String sex, String currentSymptoms, String status, String painLevel) {
            this.name = name;
            this.age = age;
            this.sex = sex;
            this.currentSymptoms = currentSymptoms;
            this.status = status;
            this.painLevel = painLevel;
        }
        
        // Getters
        public String getName() { return name; }
        public int getAge() { return age; }
        public String getSex() { return sex; }
        public String getCurrentSymptoms() { return currentSymptoms; }
        public String getStatus() { return status; }
        public String getPainLevel() { return painLevel; }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}