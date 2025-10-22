package main.data;

import main.control.UserManager;
import main.entity.*;
import java.util.List;

public class DataLoader {

    public static void loadUsers(UserManager userManager,
                                 String studentFile,
                                 String companyFile,
                                 String staffFile) {

        // Load Students
        List<String[]> studentRecords = FileHandler.readCSV(studentFile);
        for (String[] r : studentRecords) {
            String id = r[0].trim();
            String name = r[1].trim();
            String major = r[2].trim();
            int year = Integer.parseInt(r[3].trim());
            String email = r[4].trim();

            // Default password for all users
            String password = "password";
            userManager.addUser(new Student(name, id, email, password, year, major));
        }

        // Load Company Representatives
        List<String[]> companyRecords = FileHandler.readCSV(companyFile);
        for (String[] r : companyRecords) {
            String id = r[0].trim();
            String name = r[1].trim();
            String companyName = r[2].trim();
            String department = r[3].trim();
            String position = r[4].trim();
            String email = r[5].trim();
            String status = r[6].trim();

            // Default password for all users
            String password = "password";

            userManager.addUser(new CompanyRepresentative(
                    name, id, email, password, companyName, department, position, status
            ));
        }

        // Load Career Centre Staff
        List<String[]> staffRecords = FileHandler.readCSV(staffFile);
        for (String[] r : staffRecords) {
            String id = r[0].trim();
            String name = r[1].trim();
            String department = r[3].trim();
            String email = r[4].trim();

            // Default password for all users
            String password = "password";
            userManager.addUser(new CareerCenterStaff(name, id, email, password, department));
        }

        System.out.println("All user data loaded successfully.");
    }
}
