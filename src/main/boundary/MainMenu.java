package main.boundary;

import main.control.*;
import main.entity.*;
import main.entity.enums.AccountStatus;
import main.util.InputHandler;

/**
 * Minimal Main Menu - handles Login and Registration
 * Redirects to role-specific UIs (to be implemented)
 */
public class MainMenu {
    private final Authenticator authController;
    private final UserManager userManager;
    private final InputHandler inputHandler;

    public MainMenu(Authenticator authController, UserManager userManager) {
        this.authController = authController;
        this.userManager = userManager;
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

    // ---------- MENU LOGIC ----------

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
            System.out.println("Invalid credentials.\n");
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

        // Redirect to respective menu
        redirectToRoleMenu(user);
    }

    private void redirectToRoleMenu(User user) {
        if (user instanceof Student s) {
            System.out.println("🎓 Redirecting to Student Menu...");
            new StudentMenu(authController, new InternshipManager(), userManager, s).start();
        } else if (user instanceof CompanyRepresentative rep) {
            System.out.println("🏢 Redirecting to Company Representative Menu...");
            new CompanyRepMenu(authController, new InternshipManager(), userManager, rep).start();
        } else if (user instanceof CareerCenterStaff staff) {
            System.out.println("👩‍💼 Redirecting to Staff Menu...");
            new StaffMenu(authController, new InternshipManager(), userManager, staff).start();
        }

        authController.logout();
    }

    private void handleRegistration() {
        System.out.println("\n--- REGISTER ---");
        System.out.println("Select role to register as:");
        System.out.println("1. Student");
        System.out.println("2. Company Representative");
        System.out.println("3. Career Center Staff");

        int choice = inputHandler.readInt("Enter choice: ", 1, 3);
        User newUser = null;

        switch (choice) {
            case 1 -> {
                System.out.println("\n--- STUDENT REGISTRATION ---");
                String name = inputHandler.readString("Full Name: ");
                String id = inputHandler.readString("Student ID (e.g., U1234567A): ");
                String email = inputHandler.readEmail("Email: ");
                int year = inputHandler.readInt("Year of Study (1-4): ", 1, 4);
                String major = inputHandler.readString("Major: ");
                newUser = new Student(name, id, email, "password", year, major);
            }
            case 2 -> {
                System.out.println("\n--- COMPANY REPRESENTATIVE REGISTRATION ---");
                String name = inputHandler.readString("Full Name: ");
                String email = inputHandler.readEmail("Company Email: ");
                String company = inputHandler.readString("Company Name: ");
                String dept = inputHandler.readString("Department: ");
                String position = inputHandler.readString("Position: ");
                // generate ID automatically (can also use email if required)
                String repId = "REP" + System.currentTimeMillis();
                newUser = new CompanyRepresentative(name, repId, email, "password",
                        company, dept, position, AccountStatus.PENDING);
            }
            case 3 -> {
                System.out.println("\n--- STAFF REGISTRATION ---");
                String name = inputHandler.readString("Full Name: ");
                String id = inputHandler.readString("Staff ID: ");
                String email = inputHandler.readEmail("Staff Email: ");
                String dept = inputHandler.readString("Department: ");
                newUser = new CareerCenterStaff(name, id, email, "password", dept);
            }
        }

        if (newUser != null) {
            newUser.register(userManager);
            System.out.println("Registration complete.\n");
        }
    }
}
