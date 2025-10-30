package main.entity;

import main.control.UserManager;
import main.data.DataLoader;
import main.entity.enums.AccountStatus;

/**
 * Represents a Company Representative user
 *
 * OOP PRINCIPLES APPLIED:
 * 1. INHERITANCE - Extends User class
 * 2. SINGLE RESPONSIBILITY - Only handles company rep-specific data
 * 3. ENCAPSULATION - Status is controlled via enum type
 */

public class CompanyRepresentative extends User {
    private String companyName;
    private String department;
    private String position;
    private AccountStatus accountStatus; // e.g., "Pending", "Approved", "Rejected"

    public CompanyRepresentative(String name, String id, String email, String password,
                                 String companyName, String department, String position, AccountStatus accountStatus) {
        super(name, id, email, password);
        this.companyName = companyName;
        this.department = department;
        this.position = position;
        this.accountStatus = accountStatus;
    }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public AccountStatus getAccountStatus() { return accountStatus; }
    public void setAccountStatus(AccountStatus status) { this.accountStatus = status; }

    @Override
    public String getRole() {
        return "Company Representative";
    }

    public boolean isAuthorized() {
        return accountStatus == AccountStatus.APPROVED;
    }

    @Override
    public String toString() {
        return String.format("Company Rep: %s [ID: %s, Company: %s, Status: %s]",
                getName(), getUserId(), companyName, accountStatus);
    }
}
