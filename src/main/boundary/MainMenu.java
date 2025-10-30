package main.boundary;

import main.control.*;
import main.entity.*;
import main.entity.enums.AccountStatus;
import main.util.InputHandler;

/**
 * MainMenu - Entry point for login, registration, and redirection.
 */
public class MainMenu {
    private final Authenticator authController;
    private final UserManager userManager;
    private final InternshipManager internshipManager;
    private final InputHandler inputHandler;

    public MainMenu(Authenticator authController, UserManager userManager, InternshipManager internshipManager) {
        this.authController = authController;
        this.userManager = userManager;
        this.internshipManager = internshipManager;
        this.inputHandler = new InputHandler();
    }

    public void start() {
        boolean running = true;

        while (running) {
            printBanner();
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");

            int choice = inputHandler.readInt("Enter choice: ", 1, 3);

            switch (choice) {
                case 1 -> handleLogin();
                case 2 -> handleRegistration();
                case 3 -> {
                    running = false;
                    System.out.println("\n👋 Exiting system. Goodbye!");
                }
            }
        }

        inputHandler.closeScanner();
    }

    private void printBanner() {
        System.out.println("\n===============================================");
        System.out.println("     NTU INTERNSHIP PLACEMENT SYSTEM");
        System.out.println("===============================================");
    }

    private void handleLogin() {
        System.out.println("\n--- LOGIN ---");
        String id = inputHandler.readString("User ID or Email: ");
        String password = inputHandler.readPassword("Password: ");

        boolean loggedIn = authController.login(id, password);
        if (!loggedIn) {
            System.out.println("Login Failed.\n");
            return;
        }

        User user = authController.getCurrentUser();

        // Restrict login for unapproved reps
        if (user instanceof CompanyRepresentative rep &&
                rep.getAccountStatus() != AccountStatus.APPROVED) {
            System.out.println("\nYour account is not approved yet. Current status: " + rep.getAccountStatus());
            authController.logout();
            return;
        }

        redirectToRoleMenu(user);
    }

    private void redirectToRoleMenu(User user) {
        if (user instanceof Student s) {
            System.out.println("🎓 Redirecting to Student Menu...");
            new StudentMenu(authController, internshipManager, userManager, s).start();

        } else if (user instanceof CompanyRepresentative rep) {
            System.out.println("🏢 Redirecting to Company Representative Menu...");
            new CompanyRepMenu(authController, internshipManager, userManager, rep).start();

        } else if (user instanceof CareerCenterStaff staff) {
            System.out.println("👩‍💼 Redirecting to Staff Menu...");
            new StaffMenu(authController, internshipManager, userManager, staff).start();
        }

        authController.logout();
    }

    private void handleRegistration() {
        System.out.println("\n--- REGISTER ---");
        System.out.println("Only Company Representatives can self-register in this system.");
        System.out.println("1. Register as Company Representative");
        System.out.println("2. Back to Main Menu");

        int choice = inputHandler.readInt("Enter choice: ", 1, 2);
        if (choice == 2) return;

        System.out.println("\n--- COMPANY REPRESENTATIVE REGISTRATION ---");
        String name = inputHandler.readString("Full Name: ");
        String email = inputHandler.readEmail("Company Email: ");
        String company = inputHandler.readString("Company Name: ");
        String dept = inputHandler.readString("Department: ");
        String position = inputHandler.readString("Position: ");

        CompanyRepManager repManager = new CompanyRepManager(userManager);
        repManager.registerNewRep(name, email, company, dept, position);
    }
}

