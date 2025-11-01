package main.boundary;

import main.control.*;
import main.entity.*;
import main.entity.enums.*;
import main.util.InputHandler;

import java.util.List;

public class CompanyRepMenu {
    private final AppContext app;
    private final CompanyRepresentative currentRep;
    private final InputHandler input = new InputHandler();
    private final FilterMenu filterMenu = new FilterMenu();
    private final FilterSettings filters = new FilterSettings();

    public CompanyRepMenu(AppContext app, CompanyRepresentative rep) {
        this.app = app;
        this.currentRep = rep;
    }
    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== COMPANY REP MENU ===");
            System.out.println("1. View My Internships");
            System.out.println("2. Set Filters for Internships");
            System.out.println("3. Create New Internship");
            System.out.println("4. Edit Internship");
            System.out.println("5. Toggle Internship Visibility");
            System.out.println("6. View Applications");
            System.out.println("7. Review Applications");
            System.out.println("8. Change Password");
            System.out.println("9. Logout");

            int choice = input.readInt("Enter choice: ", 1, 9);
            switch (choice) {
                case 1 -> app.internshipManager.displayInternshipsForUser(currentRep, filters);
                case 2 -> filterMenu.open(filters, true, true, true, true, true);
                case 3 -> createInternshipInput();
                case 4 -> editInternshipInput();
                case 5 -> toggleVisibilityInput();
                case 6 -> viewApplications();
                case 7 -> reviewApplications();
                case 8 -> app.authenticator.changePassword(currentRep);
                case 9 -> running = false;
            }
        }
    }
    // ADD INTERNSHIP
    private void createInternshipInput() {
        System.out.println("\n--- ADD INTERNSHIP ---");
        String title = input.readString("Title: ");
        String desc = input.readString("Description: ");
        InternshipLevel level = InternshipLevel.valueOf(
                input.readString("Level (BASIC / INTERMEDIATE / ADVANCED): ").toUpperCase()
        );
        String major = input.readString("Preferred Major: ");
        String openDate = input.readString("Opening Date (YYYY-MM-DD): ");
        String closeDate = input.readString("Closing Date (YYYY-MM-DD): ");
        int slots = input.readInt("Number of Slots: ", 1, 100);

        app.internshipManager.createInternship(
                currentRep.getUserId(),
                currentRep.getCompanyName(),
                title, desc, level, major, openDate, closeDate, slots
        );
    }

    // EDIT INTERNSHIP
    private void editInternshipInput() {
        System.out.println("\n--- EDIT INTERNSHIP ---");
        String id = input.readString("Enter Internship ID: ");
        String title = input.readString("New Title: ");
        String desc = input.readString("New Description: ");
        InternshipLevel level = InternshipLevel.valueOf(
                input.readString("New Level (BASIC / INTERMEDIATE / ADVANCED): ").toUpperCase()
        );
        String major = input.readString("New Preferred Major: ");
        String openDate = input.readString("New Opening Date (YYYY-MM-DD): ");
        String closeDate = input.readString("New Closing Date (YYYY-MM-DD): ");
        int slots = input.readInt("New Number of Slots: ", 1, 100);

        app.internshipManager.editInternship(
                id, currentRep.getUserId(),
                title, desc, level, major, openDate, closeDate, slots
        );
    }


    // TOGGLE VISIBILITY
    private void toggleVisibilityInput() {
        System.out.println("\n--- TOGGLE INTERNSHIP VISIBILITY ---");
        String id = input.readString("Enter Internship ID: ");
        boolean visible = input.readYesNo("Set visible ");
        app.internshipManager.toggleVisibilityForRep(currentRep.getUserId(), id, visible);
    }

    private void viewApplications() {
        System.out.println("\n--- VIEW APPLICATIONS ---");
        System.out.println("1. View All Applications");
        System.out.println("2. Filter by Internship ID");
        System.out.println("3. Back");

        int choice = input.readInt("Enter choice: ", 1, 3);

        switch (choice) {
            case 1 -> viewAllApplications();
            case 2 -> filterByInternship();
            case 3 -> { return; }
        }
    }

    // View All Applications
    private void viewAllApplications() {
        List<Application> apps = app.applicationManager.getApplicationsForRep(currentRep.getUserId());

        if (apps.isEmpty()) {
            System.out.println("No applications yet for your internships.");
            return;
        }

        System.out.println("\nApplications for your internships:");
        for (Application a : apps) {
            System.out.println(a);
        }
        System.out.println("\nTotal applications: " + apps.size());
    }


    // Filter Applications by Internship ID
    private void filterByInternship() {
        app.internshipManager.displayInternshipsForUser(currentRep, filters);
        String internshipId = input.readString("Enter Internship ID to view applications: ");

        List<Application> filtered = app.applicationManager.getApplicationsForInternship(internshipId);

        if (filtered.isEmpty()) {
            System.out.println("No applications found for " + internshipId);
            return;
        }

        System.out.println("\nApplications for " + internshipId + ":");
        for (Application a : filtered) {
            System.out.println(a);
        }
        System.out.println("\nTotal: " + filtered.size());
    }

    // Review all Pending Applications

    private void reviewApplications() {
        List<Application> pending = app.applicationManager.getPendingApplicationsForRep(currentRep.getUserId());

        if (pending.isEmpty()) {
            System.out.println("No pending applications for your internships.");
            return;
        }

        System.out.println("\n--- PENDING APPLICATIONS ---");
        for (Application a : pending) {
            Internship internship = app.internshipManager.findInternshipById(a.getInternshipId());
            if (internship == null) continue;

            //  Check slot availability
            if (!internship.hasAvailableSlots()) {
                System.out.printf("Internship '%s' is full (%d/%d slots filled). Skipping.%n",
                        internship.getTitle(),
                        internship.getNumSlots() - internship.getSlotsLeft(),
                        internship.getNumSlots());
                continue;
            }

            System.out.printf("[%s] %s applied for %s\n",
                    a.getApplicationId(), a.getStudentName(), internship.getTitle());

            System.out.println("1. APPROVE  2. REJECT  3. SKIP");
            int choice = input.readInt("Select: ", 1, 3);

            if (choice == 1) {
                app.applicationManager.approveApplication(a);
                System.out.println("✅ Approved " + a.getApplicationId());
            } else if (choice == 2) {
                app.applicationManager.rejectApplication(a);
                System.out.println("❌ Rejected " + a.getApplicationId());
            }
        }
        System.out.println("✅ All decisions processed.");
    }
}