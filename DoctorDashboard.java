    package Eclinic;

import static Eclinic.DBConnection.getConnection;
import java.sql.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class DoctorDashboard extends Application {

    private final List<Button> sidebarButtons = new ArrayList<>();
    private BorderPane root;
    private String loggedInUsername;

    public DoctorDashboard(String username) {
        this.loggedInUsername = username;
    }

    public void launchDashboard() {
        start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();
        VBox sidebar = createModernSidebar();
        root.setLeft(sidebar);

        VBox currentContent = new VBox();
        currentContent.setPadding(new Insets(20));
        root.setCenter(currentContent);

        showDashboardOverview();

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Doctor Dashboard");
        primaryStage.show();
    }

    private VBox createModernSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(40, 0, 40, 0));
        sidebar.setStyle("-fx-background-color: #232946; -fx-min-width: 240;");
        sidebar.setAlignment(Pos.TOP_CENTER);

        sidebarButtons.clear();

        sidebarButtons.add(createSidebarButton("\ud83c\udfe0 Dashboard", this::showDashboardOverview));
        sidebarButtons.add(createSidebarButton("\ud83d\uddd5\ufe0f My Appointments", () -> displayAppointments()));
        sidebarButtons.add(createSidebarButton("\ud83d\udc65 My Patients", this::showMyPatients));
        sidebarButtons.add(createSidebarButton("\ud83e\udda5 Consultation", this::showConsultationForm));
        sidebarButtons.add(createSidebarButton("\ud83d\udc8a Prescriptions", this::showPrescriptionManagement));
        sidebarButtons.add(createSidebarButton("\u23f0 My Schedule", this::showMySchedule));
        sidebarButtons.add(createSidebarButton("\ud83d\udc64 Profile", this::showDoctorProfile));
        sidebarButtons.add(createSidebarButton("\ud83d\udeaa Logout", this::logout));

        sidebar.getChildren().addAll(sidebarButtons);
        return sidebar;
    }

    private Button createSidebarButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setFont(Font.font("Segoe UI", 16));
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPrefWidth(220);
        button.setPadding(new Insets(12, 20, 12, 24));
        button.setStyle(getSidebarButtonStyle(false));

        button.setOnMouseEntered(e -> {
            if (!button.getStyle().contains("#4f8cff")) {
                button.setStyle(getSidebarButtonStyle(false) + "-fx-background-color: #7b61ff;");
            }
        });

        button.setOnMouseExited(e -> {
            if (!button.getStyle().contains("#4f8cff")) {
                button.setStyle(getSidebarButtonStyle(false));
            }
        });

        button.setOnAction(e -> {
            setSidebarActive(text);
            action.run();
        });

        return button;
    }

    private String getSidebarButtonStyle(boolean active) {
        return "-fx-background-color: " + (active ? "#4f8cff" : "transparent") + ";" +
               "-fx-text-fill: " + (active ? "white" : "#e0e6f6") + ";" +
               "-fx-background-radius: 16; -fx-cursor: hand;";
    }

    private void setSidebarActive(String label) {
        for (Button btn : sidebarButtons) {
            boolean isActive = btn.getText().contains(label);
            btn.setStyle(getSidebarButtonStyle(isActive));
        }
    }

    private void showDashboardOverview() {
        VBox overview = new VBox(20);
        overview.setPadding(new Insets(20));
        overview.setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Doctor Dashboard Overview");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));
        title.setTextFill(Color.web("#232946"));

        String sql = "SELECT full_name, specialization, availability FROM doctor_record WHERE username = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, loggedInUsername);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("full_name");
                String specialization = rs.getString("specialization");
                String availability = rs.getString("availability");

                Label nameLabel = new Label("\ud83d\udc68\u200d\u2695\ufe0f Name: " + name);
                Label specLabel = new Label("\ud83e\udde0 Specialization: " + specialization);
                Label availLabel = new Label("\ud83d\udfe2 Availability: " + availability);

                nameLabel.setFont(Font.font("Segoe UI", 16));
                specLabel.setFont(Font.font("Segoe UI", 16));
                availLabel.setFont(Font.font("Segoe UI", 16));

                overview.getChildren().addAll(title, nameLabel, specLabel, availLabel);
            } else {
                overview.getChildren().add(new Label("\u26a0\ufe0f No data found for doctor: " + loggedInUsername));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            overview.getChildren().add(new Label("\u26a0\ufe0f Error fetching doctor details: " + e.getMessage()));
        }

        root.setCenter(overview);
    }

  private List<String> showMyAppointments(String doctorUsername) {
    List<String> appointments = new ArrayList<>();

    String sql = "SELECT patient_id, patient_name, doctor_id, doctor_name, specialization, " +
                 "appointment_date, appointment_time, reason, status " +
                 "FROM appointments WHERE doctor_name = ?";

    try (Connection conn = connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, doctorUsername); // Make sure doctor_name matches the logged-in name

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            int patientId = rs.getInt("patient_id");
            String patientName = rs.getString("patient_name");
            int doctorId = rs.getInt("doctor_id");
            String doctorName = rs.getString("doctor_name");
            String specialization = rs.getString("specialization");
            String appointmentDate = rs.getString("appointment_date");
            String appointmentTime = rs.getString("appointment_time");
            String reason = rs.getString("reason");
            String status = rs.getString("status");

            // Create a nice multiline display for each appointment
                    String info = String.format(
              "🆔 Patient ID: %d\n" +
              "👤 Name: %s\n" +
              "🩺 Doctor ID: %d | 👨‍⚕️ %s (%s)\n" +
              "📅 Date: %s at %s\n" +
              "📋 Reason: %s\n" +
              "📌 Status: %s",
                patientId, patientName, doctorId, doctorName, specialization,
                appointmentDate, appointmentTime,
                reason != null ? reason : "N/A",
                status != null ? status : "N/A"
            );

            appointments.add(info);
        }

    } catch (SQLException e) {
        System.err.println("⚠️ Error loading appointments: " + e.getMessage());
    }

    return appointments;
}

private void displayAppointments() {
    VBox appointmentView = new VBox(15);
    appointmentView.setPadding(new Insets(20));

    Label title = new Label("📅 My Appointments");
    title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
    appointmentView.getChildren().add(title);

    List<String> appointments = showMyAppointments(loggedInUsername);

    if (appointments.isEmpty()) {
        Label noData = new Label("⚠️ No appointments found.");
        noData.setFont(Font.font("Segoe UI", 14));
        appointmentView.getChildren().add(noData);
    } else {
        for (String app : appointments) {
            Label appLabel = new Label(app);
            appLabel.setFont(Font.font("Segoe UI", 14));
            appLabel.setWrapText(true);
            appLabel.setStyle("-fx-background-color: #f1f2f6; -fx-padding: 10; -fx-background-radius: 10;");
            appointmentView.getChildren().add(appLabel);
        }
    }

    root.setCenter(appointmentView);
}


private void showMyPatients() {
    ScrollPane scrollPane = new ScrollPane(); // Allow scrolling if many patients
    VBox patientBox = new VBox(15); // space between cards
    patientBox.setPadding(new Insets(20));

    String sql = "SELECT * FROM patients";

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            String firstName = rs.getString("first_name");
            String middleName = rs.getString("middle_name");
            String lastName = rs.getString("last_name");
            String fullName = firstName + " " + middleName + " " + lastName;
            String gender = rs.getString("gender");
            int age = rs.getInt("age");
            String address = rs.getString("address");
            String phone = rs.getString("phone_number");
            String email = rs.getString("email");
            String disease = rs.getString("disease");
            String assignedDoctor = rs.getString("assigned_doctor");
            String room = rs.getString("room");
            String diagnosis = rs.getString("diagnosis");
            Timestamp admissionDateTime = rs.getTimestamp("admission_datetime");
            Date dob = rs.getDate("dob");
            String contact = rs.getString("contact");
            String bloodGroup = rs.getString("blood_group");
            String bloodType = rs.getString("blood_type");
            int heartRate = rs.getInt("heart_rate");
            String allergies = rs.getString("allergies");
            String medications = rs.getString("medications");
            String surgeries = rs.getString("surgeries");
            String chronicConditions = rs.getString("chronic_conditions");
            String city = rs.getString("city");
            String state = rs.getString("state");
            String zip = rs.getString("zip");
            String country = rs.getString("country");
            String insuranceNumber = rs.getString("insurance_number");
            String policyNumber = rs.getString("policy_number");
            String paymentMethod = rs.getString("payment_method");
            String reason = rs.getString("reason");

            // Create label for display
            Label infoLabel = new Label("👤 " + fullName + " (" + gender + ", " + age + ")\n" +
                "📍 " + address + ", " + city + ", " + state + " " + zip + ", " + country + "\n" +
                "☎️ " + phone + " | ✉️ " + email + " | Emergency: " + contact + "\n" +
                "🏥 Room: " + room + " | Doctor: " + assignedDoctor + " | Disease: " + disease + "\n" +
                "🩺 Diagnosis: " + diagnosis + " on " + rs.getDate("date_of_diagnosis") + "\n" +
                "💉 Blood: " + bloodType + " (" + bloodGroup + ") | HR: " + heartRate + " bpm\n" +
                "⚠️ Allergies: " + allergies + " | Medications: " + medications + "\n" +
                "🧾 Surgeries: " + surgeries + " | Chronic: " + chronicConditions + "\n" +
                "🗓️ Admitted: " + admissionDateTime + " | Registered: " + rs.getDate("registration_date") + "\n" +
                "📄 Insurance: " + insuranceNumber + " | Policy: " + policyNumber + "\n" +
                "💳 Payment: " + paymentMethod + " | Reason: " + reason);

            infoLabel.setWrapText(true);
            infoLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

            VBox card = new VBox(infoLabel);
            card.setPadding(new Insets(15));
            card.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #2c3e50, #4ca1af);" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 4);" +
                "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                "-fx-border-radius: 15;" +
                "-fx-border-width: 1px;"
            );
            card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #3d566e, #5ab1cf);" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 12, 0, 0, 4);" +
                "-fx-border-color: rgba(255, 255, 255, 0.3);" +
                "-fx-border-radius: 15;" +
                "-fx-border-width: 1px;"
            ));
            card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #2c3e50, #4ca1af);" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 4);" +
                "-fx-border-color: rgba(255, 255, 255, 0.2);" +
                "-fx-border-radius: 15;" +
                "-fx-border-width: 1px;"
            ));

            patientBox.getChildren().add(card);
        }

    } catch (SQLException e) {
        e.printStackTrace();
        patientBox.getChildren().add(new Label("❌ Error fetching patients: " + e.getMessage()));
    }

    scrollPane.setContent(patientBox);
    scrollPane.setFitToWidth(true);
    scrollPane.setStyle("-fx-background: transparent;");

    root.setCenter(scrollPane);
}


private void showConsultationForm() {
    VBox mainLayout = new VBox(20);
    mainLayout.setPadding(new Insets(30));

    Label title = new Label("🩺 Select a Patient for Consultation");
    title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
    mainLayout.getChildren().add(title);

    ListView<String> patientList = new ListView<>();
    Map<String, Integer> patientMap = new HashMap<>();

    String sql = "SELECT id, first_name, middle_name, last_name FROM patients";

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/eclinic", "root", "");
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("first_name") + " " + rs.getString("middle_name") + " " + rs.getString("last_name");
            patientMap.put(name, id);
            patientList.getItems().add(name);
        }

    } catch (SQLException e) {
        mainLayout.getChildren().add(new Label("❌ Error loading patient list: " + e.getMessage()));
        e.printStackTrace();
    }

    patientList.setPrefHeight(300);

    patientList.setOnMouseClicked(event -> {
        String selected = patientList.getSelectionModel().getSelectedItem();
        if (selected != null && patientMap.containsKey(selected)) {
            int selectedId = patientMap.get(selected);
            showPatientDetails(selectedId);
        }
    });

    mainLayout.getChildren().add(patientList);
    root.setCenter(mainLayout);
}

private void showPatientDetails(int patientId) {
    VBox detailLayout = new VBox(15);
    detailLayout.setPadding(new Insets(20));

    String sql = "SELECT first_name, middle_name, last_name, age, gender, phone_number, email, " +
            "chronic_conditions, surgeries, allergies, medications, disease, diagnosis, date_of_diagnosis " +
            "FROM patients WHERE id = ?";

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/eclinic", "root", "");
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, patientId);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            detailLayout.getChildren().add(sectionTitle("Patient Demographics"));
            detailLayout.getChildren().addAll(
                labeledDisplay("Full Name:", rs.getString("first_name") + " " + rs.getString("middle_name") + " " + rs.getString("last_name")),
                labeledDisplay("Age:", rs.getString("age")),
                labeledDisplay("Gender:", rs.getString("gender")),
                labeledDisplay("Phone Number:", rs.getString("phone_number")),
                labeledDisplay("Email:", rs.getString("email"))
            );

            detailLayout.getChildren().add(sectionTitle("Medical History"));
            detailLayout.getChildren().addAll(
                labeledDisplay("Chronic Conditions:", rs.getString("chronic_conditions")),
                labeledDisplay("Surgeries:", rs.getString("surgeries")),
                labeledDisplay("Allergies:", rs.getString("allergies")),
                labeledDisplay("Medications:", rs.getString("medications"))
            );

            detailLayout.getChildren().add(sectionTitle("Current Symptoms"));
            detailLayout.getChildren().add(labeledDisplay("Disease:", rs.getString("disease")));

            detailLayout.getChildren().add(sectionTitle("Diagnosis"));
            detailLayout.getChildren().addAll(
                labeledDisplay("Diagnosis:", rs.getString("diagnosis")),
                labeledDisplay("Date of Diagnosis:", rs.getString("date_of_diagnosis"))
            );
        }

    } catch (SQLException e) {
        detailLayout.getChildren().add(new Label("❌ Error loading patient details: " + e.getMessage()));
        e.printStackTrace();
    }

    ScrollPane scrollPane = new ScrollPane(detailLayout);
    scrollPane.setFitToWidth(true);
    root.setCenter(scrollPane);
}

private VBox labeledDisplay(String label, String value) {
    Label lbl = new Label(label);
    Label val = new Label(value);
    val.setStyle("-fx-font-weight: bold;");
    return new VBox(3, lbl, val);
}

private Label sectionTitle(String title) {
    Label lbl = new Label("📌 " + title);
    lbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    return lbl;
}


    private void showPrescriptionManagement() {
        root.setCenter(new Label("\ud83d\udc8a Prescription Management View Placeholder"));
    }

    private void showMySchedule() {
        root.setCenter(new Label("\u23f0 My Schedule View Placeholder"));
    }

  

    private void showDoctorProfile() {
        root.setCenter(new Label("\ud83d\udc64 Doctor Profile View Placeholder"));
    }

    private void logout() {
        System.out.println("Logging out...");
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    private Connection connect() {
        try {
            String url = "jdbc:mysql://localhost:3306/eclinic";
            String user = "root";
            String password = "";
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
