package main.boundary;

import main.control.*;
import main.data.DataLoader;
import main.entity.*;
import main.entity.enums.*;
import main.util.InputHandler;

import java.util.List;

public class StaffMenu {
    private final AppContext app;
    private final CareerCenterStaff currentStaff;
    private final InputHandler input = new InputHandler();

    public StaffMenu(AppContext app, CareerCenterStaff staff) {
        this.app = app;
        this.currentStaff = staff;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== CAREER CENTRE STAFF MENU ===");
            System.out.println("1. View All Internships");
            System.out.println("2. View All Users");
            System.out.println("3. Approve/Reject Internship");
            System.out.println("4. Approve Company Representative Accounts");
            System.out.println("5. Approve Withdrawal Requests");
            System.out.println("6. Change Password");
            System.out.println("7. Logout");

            int choice = input.readInt("Enter choice: ", 1, 7);
            switch (choice) {
                case 1 -> app.internshipManager.displayAllInternships();
                case 2 -> app.userManager.displayAllUsers();
                case 3 -> approveInternships();
                case 4 -> approveCompanyReps();
                case 5 -> approveWithdrawals();
                case 6 -> app.authenticator.changePassword(currentStaff);
                case 7 -> running = false;
            }
        }
    }

    // --- APPROVE OR REJECT INTERNSHIP ---
    private void approveInternships() {
        System.out.println("\n--- INTERNSHIP APPROVAL ---");

        List<Internship> allInternships = app.internshipManager.getAllInternships();
        boolean foundPending = false;

        for (Internship i : allInternships) {
            if (i.getStatus() == InternshipStatus.PENDING) {
                foundPending = true;
                System.out.printf("\nID: %s | Title: %s | Company: %s | Level: %s | Slots: %d\n",
                        i.getInternshipId(), i.getTitle(), i.getCompanyName(),
                        i.getLevel(), i.getNumSlots());

                System.out.println("1. APPROVE");
                System.out.println("2. REJECT");
                int choice = input.readInt("Select action: ", 1, 2);

                if (choice == 1) {
                    i.setStatus(InternshipStatus.APPROVED);
                    System.out.println("✅ Internship approved: " + i.getTitle());
                } else {
                    i.setStatus(InternshipStatus.REJECTED);
                    System.out.println("❌ Internship rejected: " + i.getTitle());
                }
            }
        }

        if (!foundPending) {
            System.out.println("No pending internships found.");
        }

        app.internshipManager.saveAllInternships(); // persist changes
    }



    // --- APPROVE OR REJECT COMPANY REPS ---
    private void approveCompanyReps() {
        System.out.println("\n--- COMPANY REPRESENTATIVE ACCOUNT APPROVAL ---");

        boolean foundPending = false;
        for (User u : app.userManager.getAllUsers()) {
            if (u instanceof CompanyRepresentative rep && rep.getAccountStatus() == AccountStatus.PENDING) {
                foundPending = true;
                System.out.printf("\nID: %s | Name: %s | Company: %s | Department: %s | Position: %s | Email: %s\n",
                        rep.getUserId(), rep.getName(), rep.getCompanyName(),
                        rep.getDepartment(), rep.getPosition(), rep.getEmail());

                System.out.println("1. APPROVE");
                System.out.println("2. REJECT");
                int choice = input.readInt("Select action: ", 1, 2);

                if (choice == 1) {
                    rep.setAccountStatus(AccountStatus.APPROVED);
                    System.out.println("✅ Account approved for " + rep.getName());
                } else {
                    rep.setAccountStatus(AccountStatus.REJECTED);
                    System.out.println("❌ Account rejected for " + rep.getName());
                }
            }
        }

        if (!foundPending)
            System.out.println("No pending company representative accounts found.");

        // Persist updates
        DataLoader.saveCompanyReps("data/sample_company_representative_list.csv", app.userManager);
    }

    private void approveWithdrawals() {
        System.out.println("\n--- APPROVE WITHDRAWAL REQUESTS ---");
        List<Application> allApps = app.applicationManager.getAllApplications();
        boolean found = false;

        for (Application a : allApps) {
            if (a.getStatus() == ApplicationStatus.WITHDRAWAL_PENDING) {
                found = true;
                System.out.printf("[%s] %s | Student: %s | Internship: %s\n",
                        a.getApplicationId(), a.getStatus(), a.getStudentName(), a.getInternshipId());

                System.out.println("1. APPROVE");
                System.out.println("2. REJECT");
                int choice = input.readInt("Select action: ", 1, 2);

                if (choice == 1) {
                    a.setStatus(ApplicationStatus.WITHDRAWN);
                    System.out.println("✅ Withdrawal approved for " + a.getStudentName());
                } else {
                    a.setStatus(ApplicationStatus.PENDING);
                    System.out.println("❌ Withdrawal rejected for " + a.getStudentName());
                }
            }
        }

        if (!found) {
            System.out.println("No withdrawal requests found.");
        }

        app.applicationManager.saveApplications();
    }

}
