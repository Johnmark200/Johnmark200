package Eclinic;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDateTime;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Patient {

   
    private SimpleStringProperty name;
    private SimpleIntegerProperty age;
    private SimpleStringProperty disease;
    private SimpleStringProperty roomNumber;
    private SimpleStringProperty status;
    private SimpleIntegerProperty heartRate;
    private SimpleStringProperty bloodType;
    private SimpleStringProperty reason;
 private LocalDateTime admissionDateTime;


    
    // Database connection details
    private static final String DB_URL = "jdbc:mysql://localhost:3306/eclinic";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // Full constructor
    
    
    
    public Patient(String name, int age, String disease, String roomNumber, String status, int heartRate, String bloodType, String reason, LocalDateTime admissionDateTime) {
        this.name = new SimpleStringProperty(name);
        this.age = new SimpleIntegerProperty(age);
        this.disease = new SimpleStringProperty(disease);
        this.roomNumber = new SimpleStringProperty(roomNumber);
        this.status = new SimpleStringProperty(status);
        this.heartRate = new SimpleIntegerProperty(heartRate);
        this.bloodType = new SimpleStringProperty(bloodType);
        this.reason = new SimpleStringProperty(reason);
        this.admissionDateTime = admissionDateTime;
    }
    
    
    

    // Constructor for minimal patient data (e.g., viewing)
    public Patient(String name, int age, int heartRate, String bloodType, String reason, LocalDateTime admissionDateTime) {
        this(name, age, null, null, null, heartRate, bloodType, reason, admissionDateTime);
    }

    // Default constructor
    
    
    public Patient() {
        this.name = new SimpleStringProperty("");
        this.age = new SimpleIntegerProperty(0);
        this.disease = new SimpleStringProperty("");
        this.roomNumber = new SimpleStringProperty("");
        this.status = new SimpleStringProperty("");
        this.heartRate = new SimpleIntegerProperty(0);
        this.bloodType = new SimpleStringProperty("");
        this.reason = new SimpleStringProperty("");
        this.admissionDateTime = LocalDateTime.now();
    }

    // Getters and setters
    
    
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getAge() {
        return age.get();
    }

    public void setAge(int age) {
        this.age.set(age);
    }

    public String getDisease() {
        return disease.get();
    }

    public void setDisease(String disease) {
        this.disease.set(disease);
    }

    public String getRoomNumber() {
        return roomNumber.get();
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber.set(roomNumber);
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public int getHeartRate() {
        return heartRate.get();
    }

    public void setHeartRate(int heartRate) {
        this.heartRate.set(heartRate);
    }

    public String getBloodType() {
        return bloodType.get();
    }

    public void setBloodType(String bloodType) {
        this.bloodType.set(bloodType);
    }

    public String getReason() {
        return reason.get();
    }

    public void setReason(String reason) {
        this.reason.set(reason);
    }

   public LocalDateTime getAdmissionDateTime() {
    return admissionDateTime;
}


    public void setAdmissionDateTime(LocalDateTime admissionDateTime) {
        this.admissionDateTime = admissionDateTime;
    }

  
     
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    
    
    public static void alterPatientsTable() {
    String[] alterStatements = {
        "ALTER TABLE patients ADD COLUMN gender VARCHAR(10)",
        "ALTER TABLE patients ADD COLUMN insurance_number VARCHAR(50)"
    };

    try (Connection conn = getConnection();
         Statement stmt = conn.createStatement()) {

        for (String sql : alterStatements) {
            try {
                stmt.executeUpdate(sql);
                System.out.println("Executed: " + sql);
            } catch (SQLException e) {
                // Ignore "duplicate column" errors (error code 1060 in MySQL)
                if (e.getErrorCode() == 1060) {
                    System.out.println("Column already exists, skipping: " + sql);
                } else {
                    throw e;
                }
            }
        }

        System.out.println("Table alteration complete.");
    } catch (SQLException e) {
        System.err.println("Error altering patients table: " + e.getMessage());
    }
}
    
    private String insuranceNumber;  // in Patient class

public String getInsuranceNumber() {
    return insuranceNumber;
}


   public boolean DBConnection() {
    String sql = "INSERT INTO patients (" +
             "name, age, disease, room_number, status, heart_rate, " +
             "blood_type, reason, admission_datetime, email, contact" +
             ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        // Safety: Null-checks can be added if needed
        pstmt.setString(1, getName());
        pstmt.setInt(2, getAge());
        pstmt.setString(3, getDisease());      
        pstmt.setString(4, getRoomNumber());
        pstmt.setString(5, getStatus());
        pstmt.setInt(6, getHeartRate());
        pstmt.setString(7, getBloodType());
        pstmt.setString(8, getReason());

        // Handle possible null datetime safely
        LocalDateTime admissionDateTime = getAdmissionDateTime();
        if (admissionDateTime != null) {
            pstmt.setTimestamp(10, Timestamp.valueOf(admissionDateTime));
        } else {
            pstmt.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now())); // fallback
            System.err.println("Warning: admissionDateTime was null, defaulting to now()");
        }

        pstmt.setString(11, getInsuranceNumber());

        int rowsAffected = pstmt.executeUpdate();
        System.out.println("Inserted " + rowsAffected + " patient record(s).");
        return rowsAffected > 0;

    } catch (SQLException e) {
        System.err.println("❌ Error saving patient: " + e.getMessage());
        return false;
    } catch (Exception ex) {
        System.err.println("❌ Unexpected error: " + ex.getMessage());
        return false;
    }
}

   
   
   
    public boolean DBConnection (int patientId) {
        String sql = "UPDATE patients SET " +
             "name = ?, age = ?, disease = ?, room_number = ?, status = ?, " +
             "heart_rate = ?, blood_type = ?, reason = ?, admission_datetime = ? " +
             "WHERE id = ?";

        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, getName());
            pstmt.setInt(2, getAge());
            pstmt.setString(3, getDisease());
            pstmt.setString(4, getRoomNumber());
            pstmt.setString(5, getStatus());
            pstmt.setInt(6, getHeartRate());
            pstmt.setString(7, getBloodType());
            pstmt.setString(8, getReason());
            pstmt.setTimestamp(9, Timestamp.valueOf(getAdmissionDateTime()));
            pstmt.setInt(10, patientId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating patient: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all patients from database
     */
    public static List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients ORDER BY admission_datetime DESC";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Patient patient = new Patient();
                patient.setName(rs.getString("name"));
                patient.setAge(rs.getInt("age"));
                patient.setDisease(rs.getString("disease"));
                patient.setRoomNumber(rs.getString("room_number"));
                patient.setStatus(rs.getString("status"));
                patient.setHeartRate(rs.getInt("heart_rate"));
                patient.setBloodType(rs.getString("blood_type"));
                patient.setReason(rs.getString("reason"));
                
                Timestamp timestamp = rs.getTimestamp("admission_datetime");
                if (timestamp != null) {
                    patient.setAdmissionDateTime(timestamp.toLocalDateTime());
                }
                
                patients.add(patient);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving patients: " + e.getMessage());
        }
        
        return patients;
    }

    /**
     * Searches for patients by name
     */
    public static List<Patient> searchPatientsByName(String searchName) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE name LIKE ? ORDER BY name";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + searchName + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Patient patient = new Patient();
                patient.setName(rs.getString("name"));
                patient.setAge(rs.getInt("age"));
                patient.setDisease(rs.getString("disease"));
                patient.setRoomNumber(rs.getString("room_number"));
                patient.setStatus(rs.getString("status"));
                patient.setHeartRate(rs.getInt("heart_rate"));
                patient.setBloodType(rs.getString("blood_type"));
                patient.setReason(rs.getString("reason"));
                
                Timestamp timestamp = rs.getTimestamp("admission_datetime");
                if (timestamp != null) {
                    patient.setAdmissionDateTime(timestamp.toLocalDateTime());
                }
                
                patients.add(patient);
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching patients: " + e.getMessage());
        }
        
        return patients;
    }

    /**
     * Deletes a patient from database by ID
     */
    public static boolean deletePatient(int patientId) {
        String sql = "DELETE FROM patients WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, patientId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting patient: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gets patient count by status
     */
    public static int getPatientCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM patients WHERE status = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting patient count: " + e.getMessage());
        }
        
        return 0;
    }

    /**
     * Gets patients by room number
     */
    public static List<Patient> getPatientsByRoom(String roomNumber) {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patients WHERE room_number = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, roomNumber);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Patient patient = new Patient();
                patient.setName(rs.getString("name"));
                patient.setAge(rs.getInt("age"));
                patient.setDisease(rs.getString("disease"));
                patient.setRoomNumber(rs.getString("room_number"));
                patient.setStatus(rs.getString("status"));
                patient.setHeartRate(rs.getInt("heart_rate"));
                patient.setBloodType(rs.getString("blood_type"));
                patient.setReason(rs.getString("reason"));
                
                Timestamp timestamp = rs.getTimestamp("admission_datetime");
                if (timestamp != null) {
                    patient.setAdmissionDateTime(timestamp.toLocalDateTime());
                }
                
                patients.add(patient);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting patients by room: " + e.getMessage());
        }
        
        return patients;
    }

    @Override
    public String toString() {
        return String.format("Patient{name='%s', age=%d, disease='%s', room='%s', status='%s', heartRate=%d, bloodType='%s', reason='%s', admissionDateTime=%s}",
                getName(), getAge(), getDisease(), getRoomNumber(), getStatus(), 
                getHeartRate(), getBloodType(), getReason(), getAdmissionDateTime());
    }
}

// Doctor.java
class Doctor {
    private int id;
    private String name;
    private String specialization;
    private String availability;
    private LocalDate dateOfHire;
    private String status;

    public Doctor(int id, String name, String specialization, String availability) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.availability = availability;
        this.status = "ACTIVE";
    }

    public Doctor(int id, String name, String specialization, String availability, LocalDate dateOfHire, String status) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.availability = availability;
        this.dateOfHire = dateOfHire;
        this.status = status;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public LocalDate getDateOfHire() { return dateOfHire; }
    public void setDateOfHire(LocalDate dateOfHire) { this.dateOfHire = dateOfHire; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

// Appointment.java
class Appointment {
    private String doctor;
    private String specialization;
    private String appointmentDate;
    private String appointmentTime;
    private String status;

    public Appointment(String doctor, String specialization, String appointmentDate, String appointmentTime, String status) {
        this.doctor = doctor;
        this.specialization = specialization;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
    }

    // Getters and setters
    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

// DoctorService.java
class DoctorService {
    
    private Connection connect() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/eclinic", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Doctor getAvailableDoctorBySpecialization(String specialization, LocalDate date, String time) {
         String sql = "SELECT d.id, d.name, d.specialization, d.availability " +
                 "FROM doctors d " +
                 "WHERE d.specialization = ? " +
                 "AND d.status = 'ACTIVE' " +
                 "AND d.id NOT IN ( " +
                     "SELECT DISTINCT doctor_id FROM appointments a " +
                     "INNER JOIN doctors d2 ON d2.name = a.doctor " +
                     "WHERE a.appointment_date = ? AND a.appointment_time = ? " +
                     "AND a.status != 'CANCELLED' " +
                 ") " +
                 "ORDER BY d.name " +
                 "LIMIT 1";


        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, specialization);
            stmt.setDate(2, java.sql.Date.valueOf(date));
            stmt.setString(3, time);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Doctor(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("specialization"),
                    rs.getString("availability")
                );
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT id, name, specialization, availability, date_of_hire, status FROM doctors ORDER BY name";
        
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Doctor doctor = new Doctor(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("specialization"),
                    rs.getString("availability"),
                    rs.getDate("date_of_hire") != null ? rs.getDate("date_of_hire").toLocalDate() : null,
                    rs.getString("status")
                );
                doctors.add(doctor);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return doctors;
    }

    public boolean addDoctor(Doctor doctor) {
        String sql = "INSERT INTO doctors (name, specialization, availability, date_of_hire, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, doctor.getName());
            stmt.setString(2, doctor.getSpecialization());
            stmt.setString(3, doctor.getAvailability());
            stmt.setDate(4, doctor.getDateOfHire() != null ? java.sql.Date.valueOf(doctor.getDateOfHire()) : java.sql.Date.valueOf(LocalDate.now()));
            stmt.setString(5, doctor.getStatus() != null ? doctor.getStatus() : "ACTIVE");
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDoctor(Doctor doctor) {
        String sql = "UPDATE doctors SET name = ?, specialization = ?, availability = ?, status = ? WHERE id = ?";
        
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, doctor.getName());
            stmt.setString(2, doctor.getSpecialization());
            stmt.setString(3, doctor.getAvailability());
            stmt.setString(4, doctor.getStatus());
            stmt.setInt(5, doctor.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}



