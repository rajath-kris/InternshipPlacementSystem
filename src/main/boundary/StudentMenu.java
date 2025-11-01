package main.boundary;

import main.control.*;
import main.entity.*;
import main.util.InputHandler;

public class StudentMenu {
    private final AppContext app;
    private final Student currentStudent;
    private final InputHandler input = new InputHandler();
    private final FilterMenu filterMenu = new FilterMenu();
    private final FilterSettings filters = new FilterSettings();


    public StudentMenu(AppContext app, Student student) {
        this.app = app;
        this.currentStudent = student;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== STUDENT MENU ===");
            System.out.println("1. View Available Internships");
            System.out.println("2. Set Filters for Internships");
            System.out.println("3. Apply for Internship");
            System.out.println("4. View My Applications");
            System.out.println("5. Withdraw Application");
            System.out.println("6. Change Password");
            System.out.println("7. Logout");

            int choice = input.readInt("Enter choice: ", 1, 7);
            switch (choice) {
                case 1 -> app.internshipManager.displayInternshipsForUser(currentStudent, filters);
                case 2 -> filterMenu.open(filters, false, false, true, true, false);
                case 3 -> applyForInternship();
                case 4 -> viewMyApplications();
                case 5 -> withdrawApplication();
                case 6 -> app.authenticator.changePassword(currentStudent);
                case 7 -> running = false;
            }
        }
    }

    private void applyForInternship() {
        System.out.println("\n--- APPLY FOR INTERNSHIP ---");

        // Display current view of internships (already filtered by major, level, etc.)
        app.internshipManager.displayInternshipsForUser(currentStudent, filters);

        // Ask for input only if there are visible internships
        String internshipId = input.readString("\nEnter Internship ID to apply for (or 'cancel' to go back): ");
        if (internshipId.equalsIgnoreCase("cancel")) {
            System.out.println("Application cancelled.");
            return;
        }
        app.applicationManager.applyForInternship(currentStudent, internshipId);
    }

    private void viewMyApplications() {
        app.applicationManager.displayApplicationsForStudent(currentStudent);

        if (app.applicationManager.hasSuccessfulOffer(currentStudent)) {
            System.out.println("\n🎉 You have one or more offers!");
            boolean accept = input.readYesNo("Would you like to accept an offer now?");

            if (accept) {
                String id = input.readString("Enter Application ID of the offer to accept: ");
                System.out.println("Accepting this offer will withdraw all other applications.");
                boolean confirm = input.readYesNo("Proceed?");
                if (confirm)
                    app.applicationManager.acceptOffer(currentStudent, id);
                else
                    System.out.println("Offer not accepted.");
            }
        }
    }


    private void withdrawApplication() {
        app.applicationManager.displayWithdrawableApplications(currentStudent);
        String appId = input.readString("Enter Application ID to withdraw: ");
        app.applicationManager.withdrawApplication(currentStudent, appId);
    }

}

