package main.control;

import main.entity.User;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private final List<User> users;

    public UserManager() {
        this.users = new ArrayList<>();
    }

    // Add a new user (Student, CompanyRep, or Staff)
    public void addUser(User user) {
        users.add(user);
    }

    // Remove a user by ID
    public void removeUser(String id) {
        users.removeIf(u -> u.getUserId().equalsIgnoreCase(id));
    }

    // Find a user by ID
    public User findUserById(String id) {
        for (User u : users) {
            if (u.getUserId().equalsIgnoreCase(id)) {
                return u;
            }
        }
        return null;
    }

    // Return all users (useful for listing)
    public List<User> getAllUsers() {
        return users;
    }

    // Check if user already exists
    public boolean userExists(String id) {
        return findUserById(id) != null;
    }

    // Print all users (for testing/debug)
    public void displayAllUsers() {
        for (User u : users) {
            System.out.printf("%s - %s - %s%n", u.getUserId(), u.getName(), u.getRole());
        }
    }
}
