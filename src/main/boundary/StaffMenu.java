package main.boundary;

import main.control.*;
import main.entity.*;
import main.util.InputHandler;

import java.util.List;

public class StaffMenu {
    private final AppContext app;
    private final CareerCenterStaff currentStaff;
    private final InputHandler input = new InputHandler();
    private final FilterMenu filterMenu = new FilterMenu();
    private final FilterSettings filters = new FilterSettings();


    public StaffMenu(AppContext app, CareerCenterStaff staff) {
        this.app = app;
        this.currentStaff = staff;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== CAREER CENTRE STAFF MENU ===");
            System.out.println("1. View All Internships");
            System.out.println("2. Set Filter for Internships");
            System.out.println("3. View All Users");
            System.out.println("4. Approve/Reject Internship");
            System.out.println("5. Approve Company Representative Accounts");
            System.out.println("6. Approve Withdrawal Requests");
            System.out.println("7. Generate Report");
            System.out.println("8. Change Password");
            System.out.println("9. Logout");

            int choice = input.readInt("Enter choice: ", 1, 9);
            switch (choice) {
                case 1 -> app.internshipManager.displayInternshipsForUser(currentStaff, filters);
                case 2 -> filterMenu.open(filters, true, false, false, true, true);
                case 3 -> app.userManager.displayAllUsers();
                case 4 -> approveInternships();
                case 5 -> approveCompanyReps();
                case 6 -> approveWithdrawals();
                case 7 ->  generateReport();
                case 8 -> app.authenticator.changePassword(currentStaff);
                case 9 -> running = false;
            }
        }
    }

    // --- APPROVE OR REJECT INTERNSHIP ---
    private void approveInternships() {
        List<Internship> pending = app.internshipManager.getPendingInternships();

        if (pending.isEmpty()) {
            System.out.println("No pending internships found.");
            return;
        }

        for (Internship i : pending) {
            System.out.printf("\nID: %s | Title: %s | Company: %s | Level: %s | Slots: %d%n",
                    i.getInternshipId(), i.getTitle(), i.getCompanyName(), i.getLevel(), i.getNumSlots());

            System.out.println("1. APPROVE\n2. REJECT");
            int choice = input.readInt("Select action: ", 1, 2);

            if (choice == 1) {
                app.internshipManager.approveInternship(i); // ✅ Delegate logic & persistence
                System.out.println("✅ Internship approved: " + i.getTitle());
            } else {
                app.internshipManager.rejectInternship(i);
                System.out.println("❌ Internship rejected: " + i.getTitle());
            }
        }
    }


    // --- APPROVE OR REJECT COMPANY REPS ---
    private void approveCompanyReps() {
        System.out.println("\n--- COMPANY REPRESENTATIVE ACCOUNT APPROVAL ---");
        List<CompanyRepresentative> pending = app.userManager.getPendingCompanyReps();

        if (pending.isEmpty()) {
            System.out.println("No pending company representative accounts found.");
            return;
        }

        for (CompanyRepresentative rep : pending) {
            System.out.printf("\nID: %s | Name: %s | Company: %s | Department: %s | Position: %s | Email: %s%n",
                    rep.getUserId(), rep.getName(), rep.getCompanyName(),
                    rep.getDepartment(), rep.getPosition(), rep.getEmail());

            int choice = input.readInt("1. APPROVE  2. REJECT: ", 1, 2);
            if (choice == 1) {
                app.userManager.approveCompanyRep(rep);
                System.out.println("✅ Account approved for " + rep.getName());
            } else {
                app.userManager.rejectCompanyRep(rep);
                System.out.println("❌ Account rejected for " + rep.getName());
            }
        }
    }


    private void approveWithdrawals() {
        System.out.println("\n--- APPROVE WITHDRAWAL REQUESTS ---");
        List<Application> pending = app.applicationManager.getPendingWithdrawals();

        if (pending.isEmpty()) {
            System.out.println("No withdrawal requests found.");
            return;
        }

        for (Application a : pending) {
            System.out.printf("[%s] %s | Student: %s | Internship: %s%n",
                    a.getApplicationId(), a.getStatus(), a.getStudentName(), a.getInternshipId());

            int choice = input.readInt("1. APPROVE  2. REJECT: ", 1, 2);
            if (choice == 1) {
                app.applicationManager.approveWithdrawal(a);
                System.out.println("✅ Withdrawal approved for " + a.getStudentName());
            } else {
                app.applicationManager.rejectWithdrawal(a);
                System.out.println("❌ Withdrawal rejected for " + a.getStudentName());
            }
        }
    }

    private void generateReport() {
        System.out.println("\n--- GENERATE INTERNSHIP REPORT ---");
        System.out.println("Filter by:");
        System.out.println("1. Status");
        System.out.println("2. Preferred Major");
        System.out.println("3. Internship Level");
        System.out.println("4. No Filter (Show All)");

        int choice = input.readInt("Select option: ", 1, 4);

        String status = null, major = null, level = null;

        switch (choice) {
            case 1 -> status = input.readString("Enter Status (APPROVED/PENDING/REJECTED): ").toUpperCase();
            case 2 -> major = input.readString("Enter Preferred Major: ");
            case 3 -> level = input.readString("Enter Level (BASIC/INTERMEDIATE/ADVANCED): ").toUpperCase();
        }

        app.internshipManager.generateReport(status, major, level);
    }



}
