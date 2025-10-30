package main.boundary;

import main.control.*;
import main.entity.*;
import main.entity.enums.ApplicationStatus;
import main.util.InputHandler;
import java.util.List;

public class StudentMenu {
    private final AppContext app;
    private final Student currentStudent;
    private final InputHandler input = new InputHandler();


    public StudentMenu(AppContext app, Student student) {
        this.app = app;
        this.currentStudent = student;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== STUDENT MENU ===");
            System.out.println("1. View Available Internships");
            System.out.println("2. Apply for Internship");
            System.out.println("3. View My Applications");
            System.out.println("4. Withdraw Application");
            System.out.println("5. Change Password");
            System.out.println("6. Logout");

            int choice = input.readInt("Enter choice: ", 1, 6);
            switch (choice) {
                case 1 -> viewAvailableInternships();
                case 2 -> applyForInternship();
                case 3 -> viewMyApplications();
                case 4 -> withdrawApplication();
                case 5 -> app.authenticator.changePassword(currentStudent);
                case 6 -> running = false;
            }
        }
    }

    private void viewAvailableInternships() {
        System.out.println("\n--- AVAILABLE INTERNSHIPS ---");
        List<Internship> visible = app.internshipManager.getVisibleInternshipsForStudent(
                currentStudent.getMajor(), currentStudent.getYearOfStudy());
        if (visible.isEmpty()) {
            System.out.println("No internships available for your criteria.");
            return;
        }
        for (Internship i : visible) System.out.println(i.toStudentView());
        System.out.println("\nTotal available: " + visible.size());
    }


    private void applyForInternship() {
        System.out.println("\n--- APPLY FOR INTERNSHIP ---");
        List<Internship> visible = app.internshipManager.getVisibleInternshipsForStudent(
                currentStudent.getMajor(), currentStudent.getYearOfStudy());

        if (visible.isEmpty()) {
            System.out.println("No available internships to apply for.");
            return;
        }

        System.out.println("Available internships:");
        visible.forEach(i -> System.out.println(i.toStudentView()));


        String internshipId = input.readString("\nEnter Internship ID to apply for: ");
        app.applicationManager.applyForInternship(currentStudent, internshipId);
    }

    private void viewMyApplications() {
        List<Application> apps = app.applicationManager.getMyApplications(currentStudent.getUserId());

        if (apps.isEmpty()) {
            System.out.println("You have no applications yet.");
            return;
        }

        System.out.println("\n--- YOUR APPLICATIONS ---");
        boolean hasOffer = false;

        for (Application a : apps) {
            Internship i = app.internshipManager.findInternshipById(a.getInternshipId());
            String title = (i != null) ? i.getTitle() : a.getInternshipId();
            System.out.printf("[%s] %s | Status: %s\n", a.getApplicationId(), title, a.getStatus());

            if (a.getStatus() == ApplicationStatus.SUCCESSFUL) hasOffer = true;
        }

        //  If student has an offer, prompt to accept
        if (hasOffer) {
            System.out.println("\n🎉 You have one or more offers!");
            boolean accept = input.readYesNo("Would you like to accept an offer now? (Y/N): ");

            if (accept) {
                String id = input.readString("Enter Internship ID of the offer to accept: ");
                System.out.println("Accepting this offer will withdraw all other applications.");
                boolean confirm = input.readYesNo("Proceed? (Y/N): ");
                if (!confirm) {
                    System.out.println("Offer not accepted.");
                    return;
                }
                app.applicationManager.acceptOffer(currentStudent, id);
            }

        }
    }

    private void withdrawApplication() {
        List<Application> apps = app.applicationManager.getMyApplications(currentStudent.getUserId());
        apps.stream()
                .filter(a -> a.getStatus() == ApplicationStatus.PENDING)
                .forEach(a -> System.out.printf("[%s] %s | Status: %s\n",
                        a.getApplicationId(), a.getInternshipId(), a.getStatus()));

        String appId = input.readString("Enter Application ID to withdraw: ");
        app.applicationManager.withdrawApplication(currentStudent, appId);
    }



}
