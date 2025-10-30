package main;

import main.boundary.*;
import main.control.*;
import main.data.*;
import main.entity.*;
import main.entity.enums.*;

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

        // --- Load from CSV (students + staff) ---
        DataLoader.loadUsers(
                userManager,
                "data/sample_student_list.csv",
                "data/sample_company_representative_list.csv",
                "data/sample_staff_list.csv"
        );

        // --- Seed company reps & internships ---
        seedCompanyReps(userManager);
        seedInternships(internshipManager, internshipRepo);

        // --- Debug info ---
        System.out.println("\nLoaded Users:");
        userManager.displayAllUsers();

        System.out.println("\nLoaded Internships:");
        internshipManager.displayAllInternships();

        // --- Simulate student login and viewing ---
        System.out.println("\n🎓 Testing student login and internship view");
        if (auth.login("U2310001A", "password")) {
            Student s = (Student) auth.getCurrentUser();
            List<Internship> visible = internshipManager.getVisibleInternships(s.getMajor(), s.getYearOfStudy());
            System.out.println("Visible internships for " + s.getMajor() + " (" + s.getYearOfStudy() + "):");
            for (Internship i : visible) System.out.println(i);
            auth.logout();
        }

        // --- Simulate company rep login and viewing ---
        System.out.println("\n🏢 Testing company rep login and management");
        if (auth.login("alice@google.com", "password")) {
            CompanyRepresentative rep = (CompanyRepresentative) auth.getCurrentUser();
            internshipManager.displayMyInternships(rep.getUserId());
            internshipManager.toggleVisibility("INT001", false);
            internshipManager.displayMyInternships(rep.getUserId());
            auth.logout();
        }

        // --- Simulate staff login and approval ---
        System.out.println("\n👩‍💼 Testing staff login and approval menu");
        if (auth.login("tan002@ntu.edu.sg", "password")) { // replace with real staff email from CSV
            CareerCenterStaff staff = (CareerCenterStaff) auth.getCurrentUser();
            internshipManager.displayAllInternships();
            internshipManager.updateInternshipStatus("INT003", InternshipStatus.APPROVED);
            internshipManager.displayAllInternships();
            auth.logout();
        }

        // --- Final Save ---
        System.out.println("\n💾 Saving final data...");
        DataLoader.saveAllUsers(userManager);
        internshipRepo.saveInternships();

        System.out.println("\n✅ Full system test complete.");
    }

    // --- Seeder methods ---
    private static void seedCompanyReps(UserManager userManager) {
        boolean hasReps = userManager.getAllUsers().stream()
                .anyMatch(u -> u instanceof CompanyRepresentative);

        if (hasReps) {
            System.out.println("📦 Company Representatives already exist. Skipping seeding.\n");
            return;
        }

        System.out.println("🌱 Seeding sample company representatives...\n");

        CompanyRepresentative rep1 = new CompanyRepresentative(
                "Alice Tan",
                "REP001",
                "alice@google.com",
                "password",
                "Google",
                "Engineering",
                "Talent Partner",
                AccountStatus.APPROVED
        );

        CompanyRepresentative rep2 = new CompanyRepresentative(
                "Ben Lee",
                "REP002",
                "ben@microsoft.com",
                "password",
                "Microsoft",
                "HR",
                "Recruitment Lead",
                AccountStatus.APPROVED
        );

        CompanyRepresentative rep3 = new CompanyRepresentative(
                "Chloe Lim",
                "REP003",
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

        Internship i1 = new Internship(
                "INT001",
                "Software Engineering Intern",
                "Assist in developing full-stack web applications.",
                InternshipLevel.BASIC,
                "CSC",
                "2025-11-01",
                "2025-12-31",
                "Google",
                "REP001",
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
                "REP002",
                2
        );
        i2.setStatus(InternshipStatus.APPROVED);
        i2.setVisible(true);

        Internship i3 = new Internship(
                "INT003",
                "AI Research Assistant",
                "Support AI team in developing machine learning models.",
                InternshipLevel.ADVANCED,
                "CSC",
                "2025-09-01",
                "2025-12-15",
                "OpenAI",
                "REP003",
                1
        );
        i3.setStatus(InternshipStatus.PENDING);
        i3.setVisible(false);

        internshipManager.addInternship(i1);
        internshipManager.addInternship(i2);
        internshipManager.addInternship(i3);

        internshipRepo.saveInternships();
        System.out.println("✅ Internship seeding complete!\n");
    }
}
