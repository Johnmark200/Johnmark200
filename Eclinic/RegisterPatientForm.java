package Eclinic;

import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.sql.*;

public class RegisterPatientForm {

    // This method receives patient data and inserts it into the database
    public void savePatientToDatabase(
            String firstName, String middleName, String lastName, Date dob, String gender,
            String contact, String email, String bloodGroup, String knownAllergies,
            String currentMedications, String pastSurgeries, String chronicConditions,
            String addressLine1, String city, String state, String zipCode, String country,
            String insuranceProvider, String policyNumber, String paymentMethod, boolean consent
    ) {
        // Basic validation
        if (!consent || firstName == null || firstName.isEmpty() ||
            contact == null || contact.isEmpty() || paymentMethod == null) {
            showAlert(AlertType.ERROR, "Missing Required Fields",
                    "Please fill all required fields and accept the consent.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/eclinic", "root", "");
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO patients (first_name, middle_name, last_name, dob, gender, contact, email,  regiter" +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
             )) {

            ps.setString(1, firstName);
            ps.setString(2, middleName);
            ps.setString(3, lastName);
            ps.setDate(4, dob);
            ps.setString(5, gender);
            ps.setString(6, contact);
            ps.setString(7, email);
            ps.setString(8, bloodGroup);
            ps.setString(9, knownAllergies);
            ps.setString(10, currentMedications);
            ps.setString(11, pastSurgeries);
            ps.setString(12, chronicConditions);
            ps.setString(13, addressLine1);
            ps.setString(14, city);
            ps.setString(15, state);
            ps.setString(16, zipCode);
            ps.setString(17, country);
            ps.setString(18, insuranceProvider);
            ps.setString(19, policyNumber);
            ps.setString(20, paymentMethod);

            ps.executeUpdate();

            showAlert(AlertType.INFORMATION, "Success", "Patient registered successfully!");
            printReceipt(firstName, middleName, lastName, paymentMethod);

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(AlertType.ERROR, "Database Error", "Failed to register patient: " + ex.getMessage());
        }
    }

    // Receipt printer
 public static void printReceipt(String firstName, String middleName, String lastName, String paymentMethod) {
        String clinicName = "E-ClinicSys";
        String clinicAddress = "6220, Sta, Catalina, Philippines";

        String patientFullName = firstName + " " + (middleName != null ? middleName + " " : "") + lastName;
        String receiptText = "=====" + clinicName + " =====\n"
                + "Address: " + clinicAddress + "\n"
                + "----------------------------------------\n"
                + "Patient Name: " + patientFullName + "\n"
                + "Payment Method: " + paymentMethod + "\n"
                + "--------------------------------------\n"
                + "Status: Successful \n"
                + "===================";

        TextArea receiptArea = new TextArea(receiptText);
        receiptArea.setEditable(false);
        receiptArea.setWrapText(true);
        receiptArea.setPrefWidth(400);
        receiptArea.setPrefHeight(250);

        Alert receiptAlert = new Alert(AlertType.INFORMATION);
        receiptAlert.setTitle("Patient Registration Receipt");
        receiptAlert.getDialogPane().setContent(receiptArea);
        receiptAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        receiptAlert.showAndWait();
    }
public void showForm(Stage Stage) {
    // Your full form-building code goes here
    // Use the same fields and layout
    // Inside submitBtn.setOnAction, call savePatientToDatabase(...) with collected form data
}

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
