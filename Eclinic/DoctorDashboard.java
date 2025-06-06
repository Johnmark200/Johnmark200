package Eclinic;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import static javafx.application.Application.launch;
import javafx.scene.Node;

public class DoctorDashboard extends Application {
    private String name;
    private String email;
    private String condition;

    private BorderPane rootLayout;
    private VBox currentContent;
    private String currentDoctorName = "Dr. John Doe"; // Replace with real logic
    private VBox sidebar; // Declare sidebar


    @Override
    public void start(Stage primaryStage) {
        rootLayout = new BorderPane();
        rootLayout.setStyle("-fx-background-color: #f8f9fa;");

        // Header
        VBox header = createGradientHeader();

        // Sidebar
        VBox sidebar = createModernSidebar();

        // Main content
        currentContent = new VBox();
        showDashboardOverview();

        rootLayout.setTop(header);
        rootLayout.setLeft(sidebar);
        rootLayout.setCenter(currentContent);

        Scene scene = new Scene(rootLayout, 1300, 850);
        primaryStage.setScene(scene);
        primaryStage.setTitle("E-Clinic Doctor Dashboard");
        primaryStage.show();
    }

    private VBox createGradientHeader() {
        VBox header = new VBox();
        header.setPadding(new Insets(0, 0, 0, 0));
        header.setStyle("-fx-background-color: linear-gradient(to right, #4f8cff, #7b61ff); -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 10, 0, 0, 2);");

        HBox topRow = new HBox(20);
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.setPadding(new Insets(24, 40, 12, 40));

        Label title = new Label("E-Clinic Doctor Portal");
        title.setFont(Font.font("Segoe UI", 28));
        title.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label doctorInfo = new Label("Welcome, " + currentDoctorName);
        doctorInfo.setFont(Font.font("Segoe UI", 16));
        doctorInfo.setTextFill(Color.WHITE);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-radius: 20; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 8 24;");
        logoutBtn.setOnAction(e -> System.exit(0));

        topRow.getChildren().addAll(title, spacer, doctorInfo, logoutBtn);
        header.getChildren().add(topRow);
        return header;
    }

    private VBox createModernSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(40, 0, 40, 0));
        sidebar.setStyle("-fx-background-color: #232946; -fx-min-width: 240; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.10), 10, 0, 0, 2);");
        sidebar.setAlignment(Pos.TOP_CENTER);

        sidebar.getChildren().addAll(
            createSidebarButton("🏠 Dashboard", this::showDashboardOverview, true),
            createSidebarButton("📅 My Appointments", this::showMyAppointments, false),
            createSidebarButton("👥 My Patients", this::showMyPatients, false),
            createSidebarButton("🩺 Consultation", this::showConsultationForm, false),
            createSidebarButton("💊 Prescriptions", this::showPrescriptionManagement, false),
            createSidebarButton("⏰ My Schedule", this::showMySchedule, false),
            createSidebarButton("📈 Reports", this::showDoctorReports, false),
            createSidebarButton("👤 Profile", this::showDoctorProfile, false)
        );
        return sidebar;
    }

    private Button createSidebarButton(String text, Runnable action, boolean active) {
        Button button = new Button(text);
        button.setFont(Font.font("Segoe UI", 16));
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPrefWidth(220);
        button.setPadding(new Insets(12, 20, 12, 24));
        button.setStyle("-fx-background-color: " + (active ? "#4f8cff" : "transparent") + ";" +
                "-fx-text-fill: " + (active ? "white" : "#e0e6f6") + ";" +
                "-fx-background-radius: 16; -fx-cursor: hand;");
        button.setOnMouseEntered(e -> button.setStyle(button.getStyle() + "-fx-background-color: #7b61ff;"));
        button.setOnMouseExited(e -> button.setStyle(button.getStyle().replace("-fx-background-color: #7b61ff;", "")));
        button.setOnAction(e -> {
            setSidebarActive(text);
            action.run();
        });
        return button;
    }

    
    private void showMyPatients() {
    currentContent.getChildren().clear();

    VBox patientsView = new VBox(20);
    patientsView.setPadding(new Insets(20));

    Label title = new Label("My Patients");
    title.setFont(Font.font("Segoe UI", 24));
    title.setTextFill(Color.web("#232946"));

    TableView<Patient> patientsTable = new TableView<>();
    patientsTable.setItems(fetchPatientsFromDatabase());

    TableColumn<Patient, String> nameCol = new TableColumn<>("Name");
    nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

    TableColumn<Patient, String> emailCol = new TableColumn<>("Email");
    emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

    TableColumn<Patient, String> conditionCol = new TableColumn<>("Condition");
    conditionCol.setCellValueFactory(new PropertyValueFactory<>("condition"));

    patientsTable.getColumns().addAll(nameCol, emailCol, conditionCol);

    patientsView.getChildren().addAll(title, patientsTable);
    currentContent.getChildren().add(patientsView);
}

    
    private ObservableList<Patient> fetchPatientsFromDatabase() {
    ObservableList<Patient> patientsList = FXCollections.observableArrayList();

    int doctorId = getDoctorId(currentDoctorName);
    if (doctorId == -1) return patientsList;

    String query = "SELECT name, email, condition FROM patients WHERE doctor_id = ?";
    try (Connection conn = connect();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setInt(1, doctorId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String name = rs.getString("name");
            String email = rs.getString("email");
            String condition = rs.getString("condition");

            patientsList.add(new Patient(name, email, condition));
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return patientsList;
}

    private int getDoctorId(String doctorName) {
    int doctorId = -1;
    String query = "SELECT doctor_id FROM doctor_records WHERE full_name = ?";

    try (Connection conn = connect();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, doctorName);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            doctorId = rs.getInt("doctor_id");
        }

    } catch (SQLException e) {
    }

    return doctorId;
}

    
    private void setSidebarActive(String text) {
        for (Node node : sidebar.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                boolean isActive = btn.getText().contains(text);
                btn.setStyle("-fx-background-color: " + (isActive ? "#4f8cff" : "transparent") + ";" +
                        "-fx-text-fill: " + (isActive ? "white" : "#e0e6f6") + ";" +
                        "-fx-background-radius: 16; -fx-cursor: hand;");
            }
        }
    }

    private void showDoctorProfile() {
    // TODO: implement profile display logic
    System.out.println("Doctor profile feature is not implemented yet.");
}

    
    // Prescription.java
public class Prescription {
    private String patientName;
    private String medication;
    private String dosage;

    public Prescription(String patientName, String medication, String dosage) {
        this.patientName = patientName;
        this.medication = medication;
        this.dosage = dosage;
    }

    // Getters
    public String getPatientName() { return patientName; }
    public String getMedication() { return medication; }
    public String getDosage() { return dosage; }
}

    
  // Patients.java

public class Patient {
    private String name;
    private String gender;
    private String contact;

    public Patient(String name, String gender, String contact) {
        this.name = name;
        this.gender = gender;
        this.contact = contact;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getContact() {
        return contact;
    }
}

    
   // Appointment.java
public class Appointment {
    private String patientName;
    private String date;
    private String time;
    private String status;
    private String doctorName;

    public Appointment(String patientName, String date, String time, String status, String doctorName) {
        this.patientName = patientName;
        this.date = date;
        this.time = time;
        this.status = status;
        this.doctorName = doctorName;
    }

    // Overloaded constructor for 4 parameters
    public Appointment(String patientName, String date, String time, String status) {
        this(patientName, date, time, status, ""); // Default doctor name
    }

    // Getters
    public String getPatientName() { return patientName; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }
    public String getDoctorName() { return doctorName; }
}


    
    
 private Connection connect() {
    String url = "jdbc:mysql://localhost:3306/eclinic"; // Adjust as necessary
    String user = "root"; // Your DB username
    String password = ""; // Your DB password

    try {
        return DriverManager.getConnection(url, user, password);
    } catch (SQLException e) {
        return null;
    }
}


private void handleEmergency() {
    // Logic for handling emergency situations
    System.out.println("Emergency button clicked!");
}

    
    
    // --- Dashboard Overview ---
    private void showDashboardOverview() {
        currentContent.getChildren().clear();
        VBox dashboard = new VBox(30);
        dashboard.setPadding(new Insets(40));
        dashboard.setAlignment(Pos.TOP_LEFT);

        Label welcome = new Label("Welcome, " + currentDoctorName + " 👋");
        welcome.setFont(Font.font("Segoe UI", 28));
        welcome.setTextFill(Color.web("#232946"));

        // Cards
        HBox cards = new HBox(30);
        cards.setAlignment(Pos.CENTER_LEFT);

        cards.getChildren().addAll(
            createStatCard("Next Appointment", "10:30 AM", "#4f8cff"),
            createStatCard("Room Info", "Room 203", "#7b61ff"),
            createStatCard("Health Status", "Stable", "#2ecc71")
        );

        // Quick Actions
        HBox quickActions = new HBox(20);
        quickActions.setAlignment(Pos.CENTER_LEFT);

        Button newConsultationBtn = createGradientButton("New Consultation", "#4f8cff", "#7b61ff");
        newConsultationBtn.setOnAction(e -> showConsultationForm());

        Button viewScheduleBtn = createGradientButton("View Schedule", "#7b61ff", "#4f8cff");
        viewScheduleBtn.setOnAction(e -> showMySchedule());

        Button emergencyBtn = createGradientButton("Emergency Protocol", "#e74c3c", "#ff5858");
        emergencyBtn.setOnAction(e -> handleEmergency());

        quickActions.getChildren().addAll(newConsultationBtn, viewScheduleBtn, emergencyBtn);

        dashboard.getChildren().addAll(welcome, cards, new Label("Quick Actions:"), quickActions);
        currentContent.getChildren().add(dashboard);
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(24));
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(240);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 18; " +
                "-fx-effect: dropshadow(gaussian, rgba(79,140,255,0.08), 12, 0, 0, 4);");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", 16));
        titleLabel.setTextFill(Color.web("#232946"));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Segoe UI", 28));
        valueLabel.setTextFill(Color.web(color));

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private Button createGradientButton(String text, String color1, String color2) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", 15));
        btn.setStyle("-fx-background-radius: 20; -fx-background-color: linear-gradient(to right, " + color1 + ", " + color2 + "); -fx-text-fill: white; -fx-padding: 10 28;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-radius: 20; -fx-background-color: linear-gradient(to right, " + color2 + ", " + color1 + "); -fx-text-fill: white; -fx-padding: 10 28;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-radius: 20; -fx-background-color: linear-gradient(to right, " + color1 + ", " + color2 + "); -fx-text-fill: white; -fx-padding: 10 28;"));
        return btn;
    }

    
    
    
    
    // --- My Appointments ---
    private void showMyAppointments() {
        currentContent.getChildren().clear();
        VBox appointmentsView = new VBox(20);
        appointmentsView.setPadding(new Insets(20));

        Label title = new Label("My Appointments");
        title.setFont(Font.font("Segoe UI", 24));
        title.setTextFill(Color.web("#232946"));

        TableView<Appointment> appointmentsTable = createAppointmentsTable();

        appointmentsView.getChildren().addAll(title, appointmentsTable);
        currentContent.getChildren().add(appointmentsView);
    }

    private TableView<Appointment> createAppointmentsTable() {
        TableView<Appointment> table = new TableView<>();
        table.setItems(getMyAppointments());

        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        TableColumn<Appointment, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        table.getColumns().addAll(patientCol, timeCol, statusCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private ObservableList<Appointment> getMyAppointments() {
        ObservableList<Appointment> list = FXCollections.observableArrayList();
        String sql = "SELECT patient_name, time, status FROM appointments WHERE doctor_name=? AND date=?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, currentDoctorName);
            stmt.setString(2, LocalDate.now().toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Appointment(rs.getString(1), LocalDate.now().toString(), rs.getString(2), rs.getString(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    

    private TableView<Patient> createPatientsTable() {
    TableView<Patient> table = new TableView<>();
    table.setItems(getMyPatients());

    TableColumn<Patient, String> nameCol = new TableColumn<>("Name");
    nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
    TableColumn<Patient, String> genderCol = new TableColumn<>("Gender");
    genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
    TableColumn<Patient, String> contactCol = new TableColumn<>("Contact");
    contactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));

    table.getColumns().addAll(nameCol, genderCol, contactCol);
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    return table;
}


    private ObservableList<Patient> getMyPatients() {
    ObservableList<Patient> list = FXCollections.observableArrayList();
    String sql = "SELECT name, gender, contact FROM patients WHERE doctor_name=?";
    try (Connection conn = connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, currentDoctorName);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            // Ensure the constructor matches the parameters
            list.add(new Patient(rs.getString("name"), rs.getString("gender"), rs.getString("contact")));
        }
    } catch (SQLException e) {
    }
    return list;
}


    // --- Consultation Form ---
    private void showConsultationForm() {
        currentContent.getChildren().clear();
        VBox consultationView = new VBox(20);
        consultationView.setPadding(new Insets(20));

        Label title = new Label("Patient Consultation");
        title.setFont(Font.font("Segoe UI", 24));
        title.setTextFill(Color.web("#232946"));

        // Form fields
        TextField patientNameField = new TextField();
        TextArea diagnosisArea = new TextArea();
        TextArea treatmentArea = new TextArea();
        TextArea prescriptionArea = new TextArea();
        Button saveBtn = new Button("Save Consultation");

        saveBtn.setOnAction(e -> {
            String patientName = patientNameField.getText();
            String diagnosis = diagnosisArea.getText();
            String treatment = treatmentArea.getText();
            String prescription = prescriptionArea.getText();
            // Save consultation logic here
        });

        consultationView.getChildren().addAll(title, new Label("Patient Name:"), patientNameField,
                new Label("Diagnosis:"), diagnosisArea,
                new Label("Treatment:"), treatmentArea,
                new Label("Prescription:"), prescriptionArea,
                saveBtn);
        currentContent.getChildren().add(consultationView);
    }

    // --- Prescription Management ---
    private void showPrescriptionManagement() {
        currentContent.getChildren().clear();
        VBox prescriptionView = new VBox(20);
        prescriptionView.setPadding(new Insets(20));

        Label title = new Label("Prescription Management");
        title.setFont(Font.font("Segoe UI", 24));
        title.setTextFill(Color.web("#232946"));

        // Prescription table
        TableView<Prescription> prescriptionTable = createPrescriptionTable();

        prescriptionView.getChildren().addAll(title, prescriptionTable);
        currentContent.getChildren().add(prescriptionView);
    }

    private TableView<Prescription> createPrescriptionTable() {
        TableView<Prescription> table = new TableView<>();
        table.setItems(getPrescriptions());

        TableColumn<Prescription, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        TableColumn<Prescription, String> medicationCol = new TableColumn<>("Medication");
        medicationCol.setCellValueFactory(new PropertyValueFactory<>("medication"));
        TableColumn<Prescription, String> dosageCol = new TableColumn<>("Dosage");
        dosageCol.setCellValueFactory(new PropertyValueFactory<>("dosage"));

        table.getColumns().addAll(patientCol, medicationCol, dosageCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private ObservableList<Prescription> getPrescriptions() {
        ObservableList<Prescription> list = FXCollections.observableArrayList();
        String sql = "SELECT patient_name, medication, dosage FROM prescriptions WHERE doctor_name=?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, currentDoctorName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Prescription(rs.getString(1), rs.getString(2), rs.getString(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- My Schedule ---
    private void showMySchedule() {
        currentContent.getChildren().clear();
        VBox scheduleView = new VBox(20);
        scheduleView.setPadding(new Insets(20));

        Label title = new Label("My Schedule");
        title.setFont(Font.font("Segoe UI", 24));
        title.setTextFill(Color.web("#232946"));

        TableView<Appointment> scheduleTable = createScheduleTable();

        scheduleView.getChildren().addAll(title, scheduleTable);
        currentContent.getChildren().add(scheduleView);
    }

    private TableView<Appointment> createScheduleTable() {
        TableView<Appointment> table = new TableView<>();
        table.setItems(getMySchedule());

        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<Appointment, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));

        table.getColumns().addAll(dateCol, timeCol, patientCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private ObservableList<Appointment> getMySchedule() {
        ObservableList<Appointment> list = FXCollections.observableArrayList();
        String sql = "SELECT date, time, patient_name FROM appointments WHERE doctor_name=?";
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, currentDoctorName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Appointment(rs.getString(3), rs.getString(1), rs.getString(2), "Scheduled"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- Reports ---
    private void showDoctorReports() {
        currentContent.getChildren().clear();
        VBox reportsView = new VBox(20);
        reportsView.setPadding(new Insets(20));

        Label title = new Label("Doctor Reports");
        title.setFont(Font.font("Segoe UI", 24));
        title.setTextFill(Color.web("#232946"));

        // Add report generation logic here

        reportsView.getChildren().addAll(title);
        currentContent.getChildren().add(reportsView);
    }

      public static void main(String[] args) {
        launch(args);
    }
}
