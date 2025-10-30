package main.data;

import main.entity.Application;
import main.entity.enums.ApplicationStatus;

import java.util.ArrayList;
import java.util.List;

public class ApplicationRepository {
    private final List<Application> applications;
    private final String filePath;
    private static final String CSV_HEADER =
            "appId,studentId,studentName,studentMajor,studentYear,internshipId,appliedDate,status";

    public ApplicationRepository(String filePath) {
        this.filePath = filePath;
        this.applications = loadApplications();
    }

    // --- ADD ---
    public void addApplication(Application app) {
        applications.add(app);
        saveApplications();
    }

    // --- GET ALL ---
    public List<Application> getAllApplications() {
        return applications;
    }

    // --- FILTER BY STUDENT ---
    public List<Application> getApplicationsByStudent(String studentId) {
        List<Application> list = new ArrayList<>();
        for (Application a : applications) {
            if (a.getStudentId().equalsIgnoreCase(studentId)) list.add(a);
        }
        return list;
    }

    // --- FILTER BY INTERNSHIP ---
    public List<Application> getApplicationsByInternship(String internshipId) {
        List<Application> list = new ArrayList<>();
        for (Application a : applications) {
            if (a.getInternshipId().equalsIgnoreCase(internshipId)) list.add(a);
        }
        return list;
    }

    // --- UPDATE STATUS ---
    public void updateApplicationStatus(String appId, ApplicationStatus newStatus) {
        for (Application a : applications) {
            if (a.getApplicationId().equalsIgnoreCase(appId)) {
                a.setStatus(newStatus);
                saveApplications();
                return;
            }
        }
    }

    // --- SAVE TO CSV ---
    public void saveApplications() {
        List<String[]> data = new ArrayList<>();
        for (Application a : applications) {
            data.add(new String[]{
                    a.getApplicationId(),
                    a.getStudentId(),
                    a.getStudentName(),
                    a.getStudentMajor(),
                    String.valueOf(a.getStudentYear()),
                    a.getInternshipId(),
                    a.getAppliedDate(),
                    a.getStatus().name()
            });
        }

        FileHandler.writeCSV(filePath, data, CSV_HEADER);
    }

    // --- LOAD FROM CSV ---
    private List<Application> loadApplications() {
        List<Application> list = new ArrayList<>();
        List<String[]> raw = FileHandler.readCSV(filePath);

        for (String[] row : raw) {
            try {
                if (row.length < 5) continue;
                Application app = new Application(
                        row[0], // appId
                        row[1], // studentId
                        row[2], // studentName
                        row[3], // studentMajor
                        Integer.parseInt(row[4]), // studentYear
                        row[5], // internshipId
                        row[6], // appliedDate
                        ApplicationStatus.valueOf(row[7].toUpperCase())
                );

                list.add(app);
            } catch (Exception e) {
                System.err.println("Skipping invalid application row: " + e.getMessage());
            }
        }
        return list;
    }
}
