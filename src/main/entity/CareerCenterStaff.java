package main.entity;

import main.control.UserManager;
import main.data.DataLoader;

/**
 * Represents a Career Center Staff user
 *
 * OOP PRINCIPLES APPLIED:
 * 1. INHERITANCE - Extends User class
 * 2. SINGLE RESPONSIBILITY - Only handles staff-specific data
 */


public class CareerCenterStaff extends User {
    private String staffDepartment;
    private String role;

    public CareerCenterStaff(String name, String id, String email, String password, String staffDepartment) {
        super(name, id, email, password);
        this.staffDepartment = staffDepartment;
        this.role = "Career Centre Staff";
    }

    public String getStaffDepartment() { return staffDepartment; }
    public void setStaffDepartment(String staffDepartment) { this.staffDepartment = staffDepartment; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return String.format("Staff: %s [ID: %s, Department: %s]",
                getName(), getUserId(), staffDepartment);
    }

}
