package main;

import main.boundary.*;
import main.control.*;
import main.data.*;
import main.entity.*;
import main.entity.enums.*;

public class Test {
    public static void main(String[] args) {

        System.out.println("====================================");
        System.out.println(" NTU Internship Placement System");
        System.out.println("====================================\n");

        // ---------- INITIALIZE APP CONTEXT ----------
        AppContext context = new AppContext();
        UserManager userManager = context.userManager;
        Authenticator auth = context.authenticator;
        InternshipManager internshipManager = context.internshipManager;
        InternshipRepository internshipRepo = context.internshipRepository;

        // ---------- DISPLAY LOADED USERS ----------
        System.out.println("\n--- LOADED USERS ---");
        userManager.displayAllUsers();

        // ---------- DISPLAY LOADED INTERNSHIPS ----------
        System.out.println("\n--- LOADED INTERNSHIPS ---");
        internshipManager.displayAllInternships();

        // ---------- LOGIN TEST ----------
        System.out.println("\n--- LOGIN TEST ---");
        boolean loggedIn = auth.login("U2310001A", "password");

        if (loggedIn) {
            User current = auth.getCurrentUser();
            System.out.println("\n✅ Login Successful!");
            System.out.println("Logged in as: " + current.getName() + " (" + current.getRole() + ")");

            // ---------- ROLE-BASED SIMULATION ----------
            if (current instanceof CompanyRepresentative rep) {
                System.out.println("\n🏢 Company Representative Menu Simulation");

                // Create a new internship (10 args constructor)
                Internship newInternship = new Internship(
                        "INT001",
                        "Software Engineering Intern",
                        "Assist in backend Java development.",
                        InternshipLevel.BASIC,
                        "CSC",
                        "2025-11-01",
                        "2025-12-31",
                        rep.getCompanyName(),
                        rep.getUserId(), // use rep ID instead of email
                        3
                );

                internshipManager.addInternship(newInternship);
                internshipManager.displayMyInternships(rep.getUserId());

            } else if (current instanceof Student s) {
                System.out.println("\n🎓 Student Menu Simulation (View Internships)");
                internshipManager.displayAllInternships();

            } else if (current instanceof CareerCenterStaff staff) {
                System.out.println("\n👩‍💼 Career Center Staff Menu Simulation (Pending Approvals)");
                // Staff views company rep approvals
                for (User u : userManager.getAllUsers()) {
                    if (u instanceof CompanyRepresentative rep) {
                        System.out.printf("ID: %s | Name: %s | Company: %s | Status: %s\n",
                                rep.getUserId(), rep.getName(), rep.getCompanyName(), rep.getAccountStatus());
                    }
                }
            }

            // ---------- LOGOUT ----------
            auth.logout();
        } else {
            System.out.println("❌ Login failed. Please check credentials.");
        }

        // ---------- SAVE ALL CHANGES ----------
        System.out.println("\n💾 Saving data...");
        DataLoader.saveAllUsers(userManager);
        DataLoader.saveInternships(internshipRepo);

        System.out.println("\n===== System Test Completed =====");
    }
}
