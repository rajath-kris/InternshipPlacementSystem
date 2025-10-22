package main;
import main.control.*;
import main.data.*;

public class StudentPlacementSystem {
    public static void main(String[] args) {
        // Initialize managers
        UserManager userManager = new UserManager();
        Authenticator auth = new Authenticator(userManager);

        // Load data
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
        auth.login("U2310001A", "password");

    }
}