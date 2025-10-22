package main.boundary;

import main.control.*;
import main.entity.*;
import main.util.InputHandler;
import java.util.List;

public class StudentMenu {
    private final Authenticator auth;
    private final InternshipManager internshipMgr;
    private final UserManager userMgr;
    private final Student currentStudent;
    private final InputHandler input = new InputHandler();

    public StudentMenu(Authenticator auth, InternshipManager internshipMgr, UserManager userMgr, Student student) {
        this.auth = auth;
        this.internshipMgr = internshipMgr;
        this.userMgr = userMgr;
        this.currentStudent = student;
    }

    public void start() {
        boolean running = true;

        while (running) {
            System.out.println("\n=== STUDENT MENU ===");
            System.out.println("1. View Available Internships");
            System.out.println("2. Apply for Internship");
            System.out.println("3. View My Applications");
            System.out.println("4. Change Password");
            System.out.println("5. Logout");

            int choice = input.readInt("Enter choice: ", 1, 5);
            switch (choice) {
                case 1 -> viewAvailableInternships();
                case 2 -> applyForInternship();
                case 3 -> viewMyApplications();
                case 4 -> auth.changePassword(currentStudent);
                case 5 -> running = false;
            }
        }
    }

    private void viewAvailableInternships() {
        List<Internship> visible = internshipMgr.getVisibleInternships(currentStudent.getMajor(), currentStudent.getYearOfStudy());
        if (visible.isEmpty()) {
            System.out.println("⚠ No internships available for your criteria.");
            return;
        }
        for (Internship i : visible) System.out.println(i);
    }

    private void applyForInternship() {
        System.out.println("Feature coming soon: Apply for internship.");
    }

    private void viewMyApplications() {
        System.out.println("Feature coming soon: View applications.");
    }
}
