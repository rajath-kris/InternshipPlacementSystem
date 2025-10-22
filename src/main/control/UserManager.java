package main.control;

import main.entity.CareerCenterStaff;
import main.entity.CompanyRepresentative;
import main.entity.Student;
import main.entity.User;
import main.entity.enums.AccountStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private final List<User> users = new ArrayList<>();

    // --- ADD USER ---
    public void addUser(User user) {
        users.add(user);
    }

    // --- REMOVE USER ---
    public void removeUser(String id) {
        users.removeIf(u -> u.getUserId().equalsIgnoreCase(id));
    }

    // --- FIND USER BY ID ---
    public User findUserById(String id) {
        for (User u : users) {
            if (u.getUserId().equalsIgnoreCase(id)) return u;
        }
        return null;
    }

    // --- FIND USER BY EMAIL ---
    public User findUserByEmail(String email) {
        for (User u : users) {
            if (u.getEmail().equalsIgnoreCase(email)) return u;
        }
        return null;
    }

    // --- CHANGE PASSWORD ---
    public boolean changeUserPassword(String userId, String oldPw, String newPw) {
        User u = findUserById(userId);
        if (u == null) {
            System.out.println("User not found.");
            return false;
        }
        if (u.changePassword(oldPw, newPw)) {
            System.out.println("Password updated successfully.");
            return true;
        } else {
            System.out.println("Incorrect old password.");
            return false;
        }
    }

    // --- CHECK USER EXISTENCE ---
    public boolean userExists(String id) {
        return findUserById(id) != null;
    }

    // --- LIST USERS (for testing/debug) ---
    public void displayAllUsers() {
        for (User u : users) {
            System.out.printf("%s - %s - %s%n", u.getUserId(), u.getName(), u.getRole());
        }
    }

    // --- GET ALL USERS ---
    public List<User> getAllUsers() {
        return users;
    }
}
