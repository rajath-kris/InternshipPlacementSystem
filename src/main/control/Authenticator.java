package main.control;

import main.entity.CompanyRepresentative;
import main.entity.User;
import main.entity.enums.AccountStatus;

import java.util.Scanner;

public class Authenticator {
    private final UserManager userManager;
    private User currentUser;

    public Authenticator(UserManager userManager) {
        this.userManager = userManager;
    }

    // Attempt login (ID or Email depending on role)
    public boolean login(String idOrEmail, String password) {
        User user = null;

        // --- Try finding by ID first ---
        user = userManager.findUserById(idOrEmail);

        // --- If not found by ID, try finding by email (for company reps) ---
        if (user == null) {
            user = userManager.findUserByEmail(idOrEmail);
        }

        // --- If still not found, invalid credentials ---
        if (user == null) {
            System.out.println("Invalid credentials: No such user found.");
            return false;
        }

        // --- Verify password ---
        if (!user.getPassword().equals(password)) {
            System.out.println("Incorrect password.");
            return false;
        }

        // --- Business rule: Company Rep must use email ---
        if (user instanceof CompanyRepresentative rep) {
            // If login input was not email but ID, block it
            if (!idOrEmail.equalsIgnoreCase(rep.getEmail())) {
                System.out.println("Company Representatives must log in using their company email.");
                return false;
            }
            if (rep.getAccountStatus() != AccountStatus.APPROVED) {
                System.out.println("Your account has not been approved yet.");
                System.out.println("   Current status: " + rep.getAccountStatus());
                return false;
            }
        }

        // --- Set current user ---
        currentUser = user;
        System.out.println("Login successful. Welcome, " + user.getName() + "!");
        return true;
    }


    // Log out
    public void logout() {
        if (currentUser != null) {
            System.out.println("Logged out: " + currentUser.getName());
            currentUser = null;
        } else {
            System.out.println("No user currently logged in.");
        }
    }

    // Change password
    public boolean changePassword(User currentUser) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter old password: ");
        String oldPw = sc.nextLine();
        System.out.print("Enter new password: ");
        String newPw = sc.nextLine();

        if (currentUser.changePassword(oldPw, newPw)) {
//            userManager.saveUsersToFile(); // persist changes
            System.out.println("✅ Password changed successfully!");
            return true;
        } else {
            System.out.println("❌ Incorrect old password!");
            return false;
        }
    }

    // Get current logged-in user
    public User getCurrentUser() {
        return currentUser;
    }
}
