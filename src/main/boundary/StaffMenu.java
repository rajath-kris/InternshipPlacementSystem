package main.boundary;

import main.control.*;
import main.data.DataLoader;
import main.entity.*;
import main.entity.enums.*;
import main.util.InputHandler;

public class StaffMenu {
    private final Authenticator auth;
    private final InternshipManager internshipMgr;
    private final UserManager userMgr;
    private final CareerCenterStaff currentStaff;
    private final InputHandler input = new InputHandler();

    public StaffMenu(Authenticator auth, InternshipManager internshipMgr, UserManager userMgr, CareerCenterStaff staff) {
        this.auth = auth;
        this.internshipMgr = internshipMgr;
        this.userMgr = userMgr;
        this.currentStaff = staff;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== CAREER CENTRE STAFF MENU ===");
            System.out.println("1. View All Internships");
            System.out.println("2. Approve/Reject Internship");
            System.out.println("3. View All Users");
            System.out.println("4. Approve Company Representative Accounts");
            System.out.println("5. Change Password");
            System.out.println("6. Logout");

            int choice = input.readInt("Enter choice: ", 1, 6);
            switch (choice) {
                case 1 -> internshipMgr.displayAllInternships();
                case 2 -> updateInternshipStatus();
                case 3 -> userMgr.displayAllUsers();
                case 4 -> approveCompanyReps();
                case 5 -> auth.changePassword(currentStaff);
                case 6 -> running = false;
            }
        }
    }

    // --- APPROVE OR REJECT INTERNSHIP ---
    private void updateInternshipStatus() {
        System.out.println("\n--- APPROVE/REJECT INTERNSHIP ---");
        String id = input.readString("Enter Internship ID: ");
        System.out.println("1. APPROVE\n2. REJECT");
        int option = input.readInt("Select: ", 1, 2);
        InternshipStatus newStatus = (option == 1) ? InternshipStatus.APPROVED : InternshipStatus.REJECTED;
        internshipMgr.updateInternshipStatus(id, newStatus);
    }

    // --- APPROVE OR REJECT COMPANY REPS ---
    private void approveCompanyReps() {
        System.out.println("\n--- COMPANY REPRESENTATIVE ACCOUNT APPROVAL ---");

        boolean foundPending = false;
        for (User u : userMgr.getAllUsers()) {
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
        DataLoader.saveCompanyReps("data/sample_company_representative_list.csv", userMgr);
    }
}
