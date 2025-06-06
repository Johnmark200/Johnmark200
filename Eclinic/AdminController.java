package Eclinic;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AdminController implements Initializable {

    @FXML
    private ComboBox<String> diseaseComboBox;

    @FXML
    private ComboBox<String> timeComboBox;

    @FXML
    private DatePicker appointmentDatePicker;

    @FXML
    private Label assignedDoctorLabel;

    @FXML
    private Button checkDoctorButton;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("AdminController initialized.");
        loadSpecializations();
        populateTimeComboBox();

        confirmButton.setDisable(true);
        assignedDoctorLabel.setText("Assigned Doctor: None");

        checkDoctorButton.setOnAction(e -> checkAvailableDoctor());
        confirmButton.setOnAction(e -> confirmAppointment());
        cancelButton.setOnAction(e -> resetForm());
    }

    private void loadSpecializations() {
        String query = "SELECT DISTINCT specialization FROM doctors";

        Connection conn = connect();
        if (conn == null) {
            showAlert(AlertType.ERROR, "Database Error", "Could not connect to the database.");
            return;
        }

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            diseaseComboBox.getItems().clear();

            while (rs.next()) {
                String spec = rs.getString("specialization");
                if (spec != null && !spec.trim().isEmpty()) {
                    diseaseComboBox.getItems().add(spec.trim());
                    System.out.println("Loaded specialization: " + spec);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "SQL Error", "Failed to load specializations:\n" + e.getMessage());
        }
    }

    private void populateTimeComboBox() {
        timeComboBox.getItems().clear();
        for (int hour = 1; hour <= 24; hour++) {
            timeComboBox.getItems().add(String.format("%02d:00", hour));
        }
    }

    private void checkAvailableDoctor() {
        String specialization = diseaseComboBox.getValue();
        if (specialization == null || specialization.trim().isEmpty()) {
            showAlert(AlertType.WARNING, "Input Required", "Please select a doctor specialization.");
            return;
        }
        if (appointmentDatePicker.getValue() == null) {
            showAlert(AlertType.WARNING, "Input Required", "Please select an appointment date.");
            return;
        }
        if (timeComboBox.getValue() == null) {
            showAlert(AlertType.WARNING, "Input Required", "Please select an appointment time.");
            return;
        }

        String dummyDoctorName = "Smith";
        assignedDoctorLabel.setText("Assigned Doctor: Dr. " + dummyDoctorName);
        confirmButton.setDisable(false);
    }

    private void confirmAppointment() {
        String doctorText = assignedDoctorLabel.getText();
        if (doctorText.equals("Assigned Doctor: None")) {
            showAlert(AlertType.WARNING, "No Doctor Assigned", "Please check availability first.");
            return;
        }

        String doctorName = doctorText.replace("Assigned Doctor: Dr. ", "");
        String specialization = diseaseComboBox.getValue();
        java.time.LocalDate date = appointmentDatePicker.getValue();
        String time = timeComboBox.getValue();

        if (bookAppointment(doctorName, specialization, date.toString(), time)) {
            showAlert(AlertType.INFORMATION, "Booking Confirmed",
                "Appointment booked with Dr. " + doctorName);
            resetForm();
        } else {
            showAlert(AlertType.ERROR, "Booking Failed", "Failed to book the appointment.");
        }
    }

    private void resetForm() {
        diseaseComboBox.setValue(null);
        appointmentDatePicker.setValue(null);
        timeComboBox.setValue(null);
        assignedDoctorLabel.setText("Assigned Doctor: None");
        confirmButton.setDisable(true);
    }

    private Connection connect() {
        String url = "jdbc:mysql://localhost:3306/project";
        String user = "root";
        String password = "";

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean bookAppointment(String doctorName, String specialization, String date, String time) {
        String sql = "INSERT INTO appointments (doctor_name, specialization, date, time) VALUES (?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, doctorName);
            pstmt.setString(2, specialization);
            pstmt.setString(3, date);
            pstmt.setString(4, time);

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
