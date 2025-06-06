package Eclinic;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ExtendedStaff {
    private Staff baseStaff;
    private SimpleStringProperty fullName;
    private SimpleStringProperty sex;
    private SimpleIntegerProperty age;
    private SimpleStringProperty address;
    private SimpleStringProperty specificFunction;

    public ExtendedStaff(Staff baseStaff,  String sex, int age, String address, String specificFunction) {
        this.baseStaff = baseStaff;
        this.sex = new SimpleStringProperty(sex);
        this.age = new SimpleIntegerProperty(age);
        this.address = new SimpleStringProperty(address);
        this.specificFunction = new SimpleStringProperty(specificFunction);
    }

    // Delegated Getters
    public int getId() { return baseStaff.getId(); }
    public String getName() { return baseStaff.getName(); }
    public String getRole() { return baseStaff.getRole(); }
    public String getStatus() { return baseStaff.getStatus(); }

    // New Getters
    public String getFullName() { return fullName.get(); }
    public String getSex() { return sex.get(); }
    public int getAge() { return age.get(); }
    public String getAddress() { return address.get(); }
    public String getSpecificFunction() { return specificFunction.get(); }
}
