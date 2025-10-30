package main.entity;

import main.control.UserManager;

public abstract class User {
    private String name;
    private String userId;
    private String email;
    private String password;

    public User(String name, String userId, String email, String password) {
        this.name = name;
        this.userId = userId;
        this.email = email;
        this.password = password;
    }

    //Getters
    public String getName() { return name; }
    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }

    // Abstract Methods
    // Each subclass defines its own menu or permissions
    public abstract String getRole();

    @Override
    public String toString() {
        return String.format("%s [ID: %s, Email: %s, Role: %s]",
                name, userId, email, getRole());
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        if (this.password.equals(oldPassword)) {
            this.password = newPassword;
            return true;
        }
        return false;
    }

}


