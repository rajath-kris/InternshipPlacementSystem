package main.control;

import main.entity.User;

public class Authenticator {
    private final UserManager userManager;
    private User currentUser;

    public Authenticator(UserManager userManager) {
        this.userManager = userManager;
    }

    // Attempt login
    public boolean login(String id, String password) {
        User user = userManager.findUserById(id);
        if (user == null) {
            System.out.println("Invalid ID: No such user found.");
            return false;
        }
        if (!user.getPassword().equals(password)) {
            System.out.println("Incorrect password.");
            return false;
        }

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
    public boolean changePassword(String oldPassword, String newPassword) {
        if (currentUser == null) {
            System.out.println("Please login first.");
            return false;
        }

        if (!currentUser.getPassword().equals(oldPassword)) {
            System.out.println("Incorrect current password.");
            return false;
        }

        currentUser.setPassword(newPassword);
        System.out.println("Password changed successfully.");
        return true;
    }

    // Get current logged-in user
    public User getCurrentUser() {
        return currentUser;
    }
}
