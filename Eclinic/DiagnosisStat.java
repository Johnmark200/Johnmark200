
package Eclinic;

public class DiagnosisStat {
    private String name;
    private int patientCount;

    public DiagnosisStat(String name, int patientCount) {
        this.name = name;
        this.patientCount = patientCount;
    }

    public String getName() { return name; }
    public int getPatientCount() { return patientCount; }
}
