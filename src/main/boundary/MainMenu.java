package main.boundary;

import main.control.*;
import main.data.DataLoader;
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
            System.out.println("❌ Invalid credentials.\n");
            return;
        }

        User user = authController.getCurrentUser();

        // Restrict login for unapproved reps
        if (user instanceof CompanyRepresentative rep &&
                rep.getAccountStatus() != AccountStatus.APPROVED) {
            System.out.println("\n⚠️ Your account is not approved yet. Current status: " + rep.getAccountStatus());
            authController.logout();
            return;
        }

        System.out.println("\n✅ Login successful! Welcome, " + user.getName() + " (" + user.getRole() + ")");

        // Redirect to respective menu
        redirectToRoleMenu(user);
    }

    private void redirectToRoleMenu(User user) {
        String role = user.getRole().toLowerCase();

        if (role.contains("student")) {
            System.out.println("🎓 Redirecting to Student Menu...");
            // new StudentUI((Student) user).start();
        } else if (role.contains("company")) {
            System.out.println("🏢 Redirecting to Company Representative Menu...");
            // new CompanyRepUI((CompanyRepresentative) user).start();
        } else if (role.contains("career")) {
            System.out.println("👩‍💼 Redirecting to Staff Menu...");
            // new StaffUI((CareerCenterStaff) user).start();
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

        switch (choice) {
            case 1 -> registerStudent();
            case 2 -> registerCompanyRep();
            case 3 -> registerStaff();
        }

        DataLoader.saveAllUsers(userManager);
    }

    private void registerStudent() {
        System.out.println("\n--- STUDENT REGISTRATION ---");
        String name = inputHandler.readString("Full Name: ");
        String id = inputHandler.readString("Student ID (e.g., U1234567A): ");
        String email = inputHandler.readEmail("Email: ");
        int year = inputHandler.readInt("Year of Study (1-4): ", 1, 4);
        String major = inputHandler.readString("Major: ");

        Student student = new Student(name, id, email, "password", year, major);
        userManager.addUser(student);
        System.out.println("\n✅ Student registration successful! Default password: 'password'\n");
    }

    private void registerCompanyRep() {
        System.out.println("\n--- COMPANY REPRESENTATIVE REGISTRATION ---");
        String name = inputHandler.readString("Full Name: ");
        String email = inputHandler.readEmail("Company Email: ");
        String company = inputHandler.readString("Company Name: ");
        String dept = inputHandler.readString("Department: ");
        String position = inputHandler.readString("Position: ");

        boolean success = userManager.registerCompanyRepresentative(name, email, company, dept, position);

        if (success) {
            System.out.println("\n✅ Registration successful! Status: PENDING approval by staff.");
            System.out.println("Default password: 'password'\n");
        } else {
            System.out.println("\n❌ Registration failed! Email may already exist.\n");
        }
    }

    private void registerStaff() {
        System.out.println("\n--- CAREER CENTER STAFF REGISTRATION ---");
        String name = inputHandler.readString("Full Name: ");
        String id = inputHandler.readString("Staff ID: ");
        String email = inputHandler.readEmail("Staff Email (@ntu.edu.sg): ");
        String department = inputHandler.readString("Department: ");

        CareerCenterStaff staff = new CareerCenterStaff(name, id, email, "password", department);
        userManager.addUser(staff);
        System.out.println("\n✅ Staff registration successful! Default password: 'password'\n");
    }
}
