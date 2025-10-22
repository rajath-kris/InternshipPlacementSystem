package main;
import main.boundary.MainMenu;
import main.control.*;
import main.data.*;
import main.entity.*;
import main.entity.enums.*;


public class Test   {
    public static void main(String[] args) {


        System.out.println("====================================");
        System.out.println("NTU Internship Placement System");
        System.out.println("====================================\n");


        // ---------- INITIALIZE MANAGERS ----------
        UserManager userManager = new UserManager();
        Authenticator auth = new Authenticator(userManager);
        InternshipManager internshipManager = new InternshipManager();


        // ---------- LOAD USER DATA ----------
        DataLoader.loadUsers(
                userManager,
                "data/sample_student_list.csv",
                "data/sample_company_representative_list.csv",
                "data/sample_staff_list.csv"
        );
        // Display loaded users
        userManager.displayAllUsers();

        // Test login
        System.out.println("\n--- LOGIN TEST ---");
        boolean loggedIn = auth.login("U2310001A", "password");

        // ---------- POST-LOGIN ACTIONS ----------
        if (loggedIn) {
            User current = auth.getCurrentUser();
            System.out.println("Current user role: " + current.getRole());

            // For testing only: show role-based demo
            if (current instanceof CompanyRepresentative) {
                System.out.println("\n🏢 Company Representative Menu Simulation");

                // Create a sample internship
                Internship newInternship = new Internship(
                        "INT001",
                        "Software Engineering Intern",
                        "Assist in backend Java development.",
                        InternshipLevel.BASIC,
                        "CSC",
                        "2025-11-01",
                        "2025-12-31",
                        ((CompanyRepresentative) current).getCompanyName(),
                        current.getEmail(),
                        3
                );

                internshipManager.addInternship(newInternship);
                internshipManager.displayMyInternships(current.getEmail());

            } else if (current instanceof Student) {
                System.out.println("\n🎓 Student Menu Simulation (View Internships)");
                internshipManager.displayAllInternships();

            } else if (current instanceof CareerCenterStaff) {
                System.out.println("\n👩‍💼 Career Center Staff Menu Simulation (Approval View)");
                internshipManager.displayAllInternships();
            }

            // Logout at end
            auth.logout();
        }

        System.out.println("\n===== System Test Completed =====");
    }
}
