package main.boundary;

import main.control.*;
import main.entity.*;
import main.entity.enums.*;
import main.util.InputHandler;

public class CompanyRepMenu {
    private final Authenticator auth;
    private final InternshipManager internshipMgr;
    private final UserManager userMgr;
    private final CompanyRepresentative currentRep;
    private final InputHandler input = new InputHandler();

    public CompanyRepMenu(Authenticator auth, InternshipManager internshipMgr, UserManager userMgr, CompanyRepresentative rep) {
        this.auth = auth;
        this.internshipMgr = internshipMgr;
        this.userMgr = userMgr;
        this.currentRep = rep;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== COMPANY REPRESENTATIVE MENU ===");
            System.out.println("1. Add Internship");
            System.out.println("2. Edit Internship");
            System.out.println("3. Toggle Internship Visibility");
            System.out.println("4. View My Internships");
            System.out.println("5. Change Password");
            System.out.println("6. Logout");

            int choice = input.readInt("Enter choice: ", 1, 6);
            switch (choice) {
                case 1 -> addInternship();
                case 2 -> editInternship();
                case 3 -> toggleVisibility();
                case 4 -> internshipMgr.displayMyInternships(currentRep.getUserId());
                case 5 -> auth.changePassword(currentRep);
                case 6 -> running = false;
            }
        }
    }

    // --- ADD INTERNSHIP ---
    private void addInternship() {
        System.out.println("\n--- ADD INTERNSHIP ---");
        String id = "INT" + System.currentTimeMillis(); // auto-generate
        String title = input.readString("Title: ");
        String desc = input.readString("Description: ");
        InternshipLevel level = InternshipLevel.valueOf(
                input.readString("Level (BASIC / INTERMEDIATE / ADVANCED): ").toUpperCase()
        );
        String major = input.readString("Preferred Major: ");
        String openDate = input.readString("Opening Date (YYYY-MM-DD): ");
        String closeDate = input.readString("Closing Date (YYYY-MM-DD): ");
        int slots = input.readInt("Number of Slots: ", 1, 100);

        // Construct internship using your entity constructor
        Internship newInternship = new Internship(
                id, title, desc, level, major, openDate, closeDate,
                currentRep.getCompanyName(), currentRep.getUserId(), slots
        );

        internshipMgr.addInternship(newInternship);
    }

    // --- EDIT INTERNSHIP ---
    private void editInternship() {
        System.out.println("\n--- EDIT INTERNSHIP ---");
        String id = input.readString("Enter Internship ID: ");

        Internship existing = internshipMgr.findInternshipById(id);
        if (existing == null) {
            System.out.println("⚠ Internship not found.");
            return;
        }

        // ensure this rep owns it
        if (!existing.getRepresentativeId().equalsIgnoreCase(currentRep.getUserId())) {
            System.out.println("❌ You can only edit your own internships.");
            return;
        }

        if (existing.getStatus() != InternshipStatus.PENDING) {
            System.out.println("⚠ Cannot edit. Internship is already " + existing.getStatus());
            return;
        }

        String title = input.readString("New Title: ");
        String desc = input.readString("New Description: ");
        InternshipLevel level = InternshipLevel.valueOf(
                input.readString("New Level (BASIC / INTERMEDIATE / ADVANCED): ").toUpperCase()
        );
        String major = input.readString("New Preferred Major: ");
        String openDate = input.readString("New Opening Date (YYYY-MM-DD): ");
        String closeDate = input.readString("New Closing Date (YYYY-MM-DD): ");
        int slots = input.readInt("New Number of Slots: ", 1, 100);

        internshipMgr.editInternship(id, currentRep.getUserId(),
                title, desc, level, major, openDate, closeDate, slots);
    }

    // --- TOGGLE VISIBILITY ---
    private void toggleVisibility() {
        System.out.println("\n--- TOGGLE INTERNSHIP VISIBILITY ---");
        String id = input.readString("Enter Internship ID: ");
        boolean visible = input.readYesNo("Set visible (Y/N): ");
        internshipMgr.toggleVisibility(id, visible);
    }
}
