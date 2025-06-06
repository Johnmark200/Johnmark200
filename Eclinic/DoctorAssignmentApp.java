package Eclinic;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DoctorAssignmentApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        Label headerLabel = new Label("Disease-Doctor Assignment Section");

        // Disease ComboBox
        Label diseaseLabel = new Label("Select Disease:");
        ComboBox<String> diseaseComboBox = new ComboBox<>();
        diseaseComboBox.getItems().addAll("Cardiology", "Dermatology", "Orthopedics");

        // DatePicker
        Label dateLabel = new Label("Select Date:");
        DatePicker appointmentDatePicker = new DatePicker();

        // Time ComboBox
        Label timeLabel = new Label("Select Time:");
        ComboBox<String> timeComboBox = new ComboBox<>();
        timeComboBox.getItems().addAll("09:00", "10:00", "11:00", "12:00");

        // Assigned Doctor Label
        Label assignedDoctorLabel = new Label("Assigned Doctor: None");

        // Check Available Doctor Button
        Button checkDoctorButton = new Button("Check Available Doctor");

        // Confirm & Cancel Buttons
        Button confirmButton = new Button("Confirm and Book");
        Button cancelButton = new Button("Cancel");
        confirmButton.setDisable(true); // initially disabled

        // Action: Check Available Doctor
        checkDoctorButton.setOnAction(e -> {
            String selectedDisease = diseaseComboBox.getValue();
            LocalDate selectedDate = appointmentDatePicker.getValue();
            String selectedTime = timeComboBox.getValue();

            if (selectedDisease == null || selectedDate == null || selectedTime == null) {
                new Alert(Alert.AlertType.WARNING, "Please select all fields.").showAndWait();
                return;
            }

            Doctor availableDoctor = getAvailableDoctorForDisease(selectedDisease, selectedDate, selectedTime);
            if (availableDoctor != null) {
                assignedDoctorLabel.setText("Assigned Doctor: Dr. " + availableDoctor.getName());
                confirmButton.setDisable(false);
            } else {
                new Alert(Alert.AlertType.INFORMATION,
                        "No available doctors for " + selectedDisease +
                                " at " + selectedTime + ". Please choose another time.")
                        .showAndWait();
                assignedDoctorLabel.setText("Assigned Doctor: None");
                confirmButton.setDisable(true);
            }
        });

        // Confirm Button Action
        confirmButton.setOnAction(e -> {
            new Alert(Alert.AlertType.INFORMATION, "Appointment successfully booked with "
                    + assignedDoctorLabel.getText().replace("Assigned Doctor: ", ""))
                    .showAndWait();
            diseaseComboBox.setValue(null);
            appointmentDatePicker.setValue(null);
            timeComboBox.setValue(null);
            assignedDoctorLabel.setText("Assigned Doctor: None");
            confirmButton.setDisable(true);
        });

        // Cancel Button Action
        cancelButton.setOnAction(e -> {
            diseaseComboBox.setValue(null);
            appointmentDatePicker.setValue(null);
            timeComboBox.setValue(null);
            assignedDoctorLabel.setText("Assigned Doctor: None");
            confirmButton.setDisable(true);
        });

        box.getChildren().addAll(
                headerLabel,
                diseaseLabel, diseaseComboBox,
                dateLabel, appointmentDatePicker,
                timeLabel, timeComboBox,
                checkDoctorButton,
                assignedDoctorLabel,
                confirmButton, cancelButton
        );

        Scene scene = new Scene(box, 350, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Eclinic Doctor Assignment");
        primaryStage.show();
    }

    // Mock Doctor class
    public class Doctor {
        private String name;
        private String specialization;

        public Doctor(String name, String specialization) {
            this.name = name;
            this.specialization = specialization;
        }

        public String getName() {
            return name;
        }

        public String getSpecialization() {
            return specialization;
        }
    }

    // Mock method to fetch available doctor
    public Doctor getAvailableDoctorForDisease(String disease, LocalDate date, String time) {
        List<Doctor> doctors = new ArrayList<>();
        doctors.add(new Doctor("Sarah Smith", "Cardiology"));
        doctors.add(new Doctor("James Brown", "Dermatology"));
        doctors.add(new Doctor("Nancy Lee", "Cardiology"));

        for (Doctor doc : doctors) {
            if (doc.getSpecialization().equalsIgnoreCase(disease)) {
                return doc;
            }
        }

        return null; // no doctor found
    }
}
