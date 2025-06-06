package Eclinic;

import javafx.scene.control.Alert;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;




public class DoctorService {


    // Establish connection to MySQL database
    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/eclinic";
        String user = "root";
        String password = "";
        return DriverManager.getConnection(url, user, password);
    }

    // 🔍 Find available doctor by specialization, date, and time
    public Doctor getAvailableDoctorBySpecialization(String specialization, LocalDate date, String time) {
        String sql = "SELECT * FROM doctors d " +
                "WHERE d.specialization = ? " +
                "AND FIND_IN_SET(?, d.availability) > 0 " +
                "AND d.id NOT IN ( " +
                "    SELECT a.doctor_id FROM appointments a " +
                "    WHERE a.appointment_date = ? AND a.appointment_time = ? " +
                ") " +
                "LIMIT 1";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, specialization);
            stmt.setString(2, time);
            stmt.setDate(3, java.sql.Date.valueOf(date));
            stmt.setString(4, time);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Doctor(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getString("availability"),
                        rs.getDate("date_joined").toLocalDate(),
                        rs.getString("contact")
                );
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error finding available doctor.\n" + ex.getMessage());
        }

        return null;
    }

    // ➕ Add a new doctor
    public boolean addDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctors (name, specialization, availability, date_joined, contact) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, doctor.getName());
            stmt.setString(2, doctor.getSpecialization());
            stmt.setString(3, doctor.getAvailability());
            stmt.setDate(4, java.sql.Date.valueOf(doctor.getDateJoined()));
            stmt.setString(5, doctor.getContact());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error adding doctor: " + e.getMessage());
        }

        return false;
    }

    // ✏️ Update an existing doctor
    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE doctors SET name = ?, specialization = ?, availability = ?, date_joined = ?, contact = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, doctor.getName());
            stmt.setString(2, doctor.getSpecialization());
            stmt.setString(3, doctor.getAvailability());
            stmt.setDate(4, java.sql.Date.valueOf(doctor.getDateJoined()));
            stmt.setString(5, doctor.getContact());
            stmt.setInt(6, doctor.getId());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error updating doctor: " + e.getMessage());
        }

        return false;
    }

    // ❌ Delete a doctor by ID
    public boolean deleteDoctor(int doctorId) {
        String sql = "DELETE FROM doctors WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, doctorId);
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error deleting doctor: " + e.getMessage());
        }

        return false;
    }

    // Optional: 🗂️ Fetch all doctors
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT * FROM doctors";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Doctor doctor = new Doctor(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("specialization"),
                        rs.getString("availability"),
                        rs.getDate("date_joined").toLocalDate(),
                        rs.getString("contact")
                );
                doctors.add(doctor);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error loading doctors: " + e.getMessage());
        }

        return doctors;
    }

    // Helper: show alert messages
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
