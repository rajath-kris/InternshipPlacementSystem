package main.entity;

import main.entity.enums.ApplicationStatus;

public class Application {
    private String applicationId;
    private String studentId;
    private String studentName;
    private String studentMajor;
    private int studentYear;
    private String internshipId;
    private String appliedDate;
    private ApplicationStatus status;

    public Application(String applicationId, String studentId, String studentName, String studentMajor, int studentYear,
                       String internshipId, String appliedDate, ApplicationStatus status) {
        this.applicationId = applicationId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentMajor = studentMajor;
        this.studentYear = studentYear;
        this.internshipId = internshipId;
        this.appliedDate = appliedDate;
        this.status = status;
    }

    // Getters
    public String getApplicationId() { return applicationId; }
    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getStudentMajor() { return studentMajor; }
    public int getStudentYear() { return studentYear; }
    public String getInternshipId() { return internshipId; }
    public String getAppliedDate() { return appliedDate; }
    public ApplicationStatus getStatus() { return status; }

    // Setter
    public void setStatus(ApplicationStatus status) { this.status = status; }

    @Override
    public String toString() {
        return String.format(
                "[%s] %s (%s, Y%d) applied for %s | Status: %s | Date: %s",
                applicationId, studentName, studentMajor, studentYear,
                internshipId, status, appliedDate
        );
    }
}
