package main;

import main.control.*;
import main.data.*;
import main.entity.*;
import main.entity.enums.*;
import main.boundary.FilterMenu;
import main.entity.FilterSettings;

import java.util.List;

public class Test {

    public static void main(String[] args) {

        System.out.println("====================================");
        System.out.println(" NTU Internship Placement System");
        System.out.println("====================================\n");

        // --- Initialize ---
        String internshipPath = System.getProperty("user.dir") + "/data/internships.csv";
        InternshipRepository internshipRepo = new InternshipRepository(internshipPath);
        InternshipManager internshipManager = new InternshipManager(internshipRepo);

        UserManager userManager = new UserManager();
        Authenticator auth = new Authenticator(userManager);

        // --- Load Users (Students, Company Reps, Staff) ---
        DataLoader.loadUsers(
                userManager,
                "data/sample_student_list.csv",
                "data/sample_company_representative_list.csv",
                "data/sample_staff_list.csv"
        );

        // --- Seed sample Company Reps & Internships if not present ---
        seedCompanyReps(userManager);
        seedInternships(internshipManager, internshipRepo);

        System.out.println("\n✅ Data loading complete.\n");

        // -------------------------------
        // TEST SCENARIOS
        // -------------------------------

        // 🎓 STUDENT LOGIN & INTERNSHIP VIEW
        System.out.println("\n🎓 Testing student login and internship view...");
        if (auth.login("U2310001A", "password")) {
            Student s = (Student) auth.getCurrentUser();
            FilterSettings studentFilters = new FilterSettings();

            // Student views internships
            internshipManager.displayInternshipsForUser(s, studentFilters);

            // Optionally simulate applying for an internship
            System.out.println("\n📩 Simulating internship application...");
            internshipManager.displayInternshipsForUser(s, studentFilters);
            auth.logout();
        }

        // 🏢 COMPANY REP LOGIN & MANAGEMENT
        System.out.println("\n🏢 Testing company rep login and internship view...");
        if (auth.login("alice@google.com", "password")) {
            CompanyRepresentative rep = (CompanyRepresentative) auth.getCurrentUser();
            FilterSettings repFilters = new FilterSettings();

            // Rep views their internships
            internshipManager.displayInternshipsForUser(rep, repFilters);

            // Toggle visibility of an internship
            internshipManager.toggleVisibilityForRep(rep.getUserId(), "INT001", true);

            // View again
            internshipManager.displayInternshipsForUser(rep, repFilters);
            auth.logout();
        }

        // 👩‍💼 STAFF LOGIN & APPROVAL
        System.out.println("\n👩‍💼 Testing staff login and internship approval flow...");
        if (auth.login("tan002@ntu.edu.sg", "1234")) { // Replace with real staff email if needed
            CareerCenterStaff staff = (CareerCenterStaff) auth.getCurrentUser();
            FilterSettings staffFilters = new FilterSettings();

            // Staff views all internships
            internshipManager.displayInternshipsForUser(staff, staffFilters);

            // Approve one internship
            internshipManager.updateInternshipStatus("INT003", InternshipStatus.APPROVED);

            // View again to confirm
            internshipManager.displayInternshipsForUser(staff, staffFilters);
            auth.logout();
        }

        // 💾 SAVE FINAL DATA
        System.out.println("\n💾 Saving final data...");
        DataLoader.saveAllUsers(userManager);
        internshipRepo.saveInternships();
        System.out.println("\n✅ Full system test complete.\n");
    }

    // ================================================================
    // Seeder Methods
    // ================================================================

    private static void seedCompanyReps(UserManager userManager) {
        boolean hasReps = userManager.getAllUsers().stream()
                .anyMatch(u -> u instanceof CompanyRepresentative);

        if (hasReps) {
            System.out.println("📦 Company Representatives already exist. Skipping seeding.\n");
            return;
        }

        System.out.println("🌱 Seeding sample company representatives...\n");

        // ✅ Rep IDs now use EMAILS (to match login system)
        CompanyRepresentative rep1 = new CompanyRepresentative(
                "Alice Tan",
                "alice@google.com",   // <-- ID = EMAIL
                "alice@google.com",
                "password",
                "Google",
                "Engineering",
                "Talent Partner",
                AccountStatus.APPROVED
        );

        CompanyRepresentative rep2 = new CompanyRepresentative(
                "Ben Lee",
                "ben@microsoft.com",
                "ben@microsoft.com",
                "password",
                "Microsoft",
                "HR",
                "Recruitment Lead",
                AccountStatus.APPROVED
        );

        CompanyRepresentative rep3 = new CompanyRepresentative(
                "Chloe Lim",
                "chloe@openai.com",
                "chloe@openai.com",
                "password",
                "OpenAI",
                "Research",
                "AI Recruiter",
                AccountStatus.APPROVED
        );

        userManager.addUser(rep1);
        userManager.addUser(rep2);
        userManager.addUser(rep3);

        DataLoader.appendNewUser(rep1);
        DataLoader.appendNewUser(rep2);
        DataLoader.appendNewUser(rep3);

        System.out.println("✅ Company Representatives seeded successfully.\n");
    }

    private static void seedInternships(InternshipManager internshipManager, InternshipRepository internshipRepo) {
        if (!internshipRepo.getAllInternships().isEmpty()) {
            System.out.println("📦 Internships already exist. Skipping seeding.\n");
            return;
        }

        System.out.println("🌱 Seeding sample internships...\n");

        // ✅ Use representative EMAILS as repId (not REP001 etc.)
        Internship i1 = new Internship(
                "INT001",
                "Software Engineering Intern",
                "Assist in developing full-stack web applications.",
                InternshipLevel.BASIC,
                "CS",
                "2025-11-01",
                "2025-12-31",
                "Google",
                "alice@google.com",
                3
        );
        i1.setStatus(InternshipStatus.APPROVED);
        i1.setVisible(true);

        Internship i2 = new Internship(
                "INT002",
                "Data Analyst Intern",
                "Work on dashboards and data visualizations for performance tracking.",
                InternshipLevel.INTERMEDIATE,
                "EEE",
                "2025-10-15",
                "2025-12-30",
                "Microsoft",
                "ben@microsoft.com",
                2
        );
        i2.setStatus(InternshipStatus.APPROVED);
        i2.setVisible(true);

        Internship i3 = new Internship(
                "INT003",
                "AI Research Assistant",
                "Support AI team in developing machine learning models.",
                InternshipLevel.ADVANCED,
                "CS",
                "2025-09-01",
                "2025-12-15",
                "OpenAI",
                "chloe@openai.com",
                1
        );
        i3.setStatus(InternshipStatus.PENDING);
        i3.setVisible(false);

        // Add directly
        internshipRepo.addInternship(i1);
        internshipRepo.addInternship(i2);
        internshipRepo.addInternship(i3);
        internshipRepo.saveInternships();

        System.out.println("✅ Internship seeding complete!\n");
    }
}
