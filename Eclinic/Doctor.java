package Eclinic;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Doctor {
    private int id;
    private String name;
    private String gender;
    private String specialization;
    private LocalDate dob;
    private String phone;
    private String availability;
    private LocalDate dateJoined;

    // Full constructor
    public Doctor(int id, String name, String gender, String specialization, LocalDate dob, String phone) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.specialization = specialization;
        this.dob = dob;
        this.phone = phone;
    }

    // Simpler constructor for UI use
    public Doctor(String name, String specialization, String phone) {
        this.name = name;
        this.specialization = specialization;
        this.phone = phone;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public LocalDate getDateJoined() { return dateJoined; }
    public void setDateJoined(LocalDate dateJoined) { this.dateJoined = dateJoined; }

    // Additional accessors for AdminDashboard compatibility
    public String getSpecialty() { return getSpecialization(); }
    public void setSpecialty(String specialty) { setSpecialization(specialty); }

    public String getContact() { return getPhone(); }
    public void setContact(String contact) { setPhone(contact); }

    // ✅ Static method to fetch all doctors from the database
  
    public static List<Doctor> fetchAllFromDatabase() {
    List<Doctor> doctors = new ArrayList<>();
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/eclinic", "username", "password");
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT name, specialization, phone FROM doctors")) {

        while (rs.next()) {
            Doctor doc = new Doctor(
                rs.getString("name"),
                rs.getString("specialization"),
                rs.getString("phone")
            );
            doctors.add(doc);
        }
    } catch (SQLException e) {
        e.printStackTrace(); // Or log error
    }
    return doctors;
}
}
