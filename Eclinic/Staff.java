package Eclinic;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Staff {
    
    
    private SimpleIntegerProperty id;
    private SimpleStringProperty name;
    private SimpleStringProperty role;
    private SimpleStringProperty status;

    
    
    // Constructor
    public Staff(int id, String name, String role, String status) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.role = new SimpleStringProperty(role);
        this.status = new SimpleStringProperty(status);
    }

    // Getters
    public int getId() {
        return id.get();
    }

    public String getName() {
        return name.get();
    }

    public String getRole() {
        return role.get();
    }

    public String getStatus() {
        return status.get();
    }

    // Setter for status
    public void setStatus(String status) {
        this.status.set(status);
    }
}