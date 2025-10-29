package main.data;

import main.control.UserManager;
import main.entity.*;
import main.entity.enums.AccountStatus;

import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    // ---------- LOAD USERS ----------
    public static void loadUsers(UserManager userManager,
                                 String studentFile,
                                 String companyFile,
                                 String staffFile) {

        // --- Load Students ---
        List<String[]> studentRecords = FileHandler.readCSV(studentFile);
        for (String[] r : studentRecords) {
            try {
                // CSV: StudentID, Name, Major, Year, Email
                String id = r[0].trim();
                String name = r[1].trim();
                String major = r[2].trim();
                int year = Integer.parseInt(r[3].trim());
                String email = r[4].trim();

                userManager.addUser(new Student(name, id, email, "password", year, major));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading student record: " + String.join(",", r));
            }
        }

        // --- Load Company Representatives ---
        List<String[]> companyRecords = FileHandler.readCSV(companyFile);
        for (String[] r : companyRecords) {
            try {
                // CSV: CompanyRepID, Name, CompanyName, Department, Position, Email, Status
                String id = r[0].trim();
                String name = r[1].trim();
                String companyName = r[2].trim();
                String department = r[3].trim();
                String position = r[4].trim();
                String email = r[5].trim();
                String statusStr = r[6].trim().toUpperCase();

                AccountStatus status;
                try {
                    status = AccountStatus.valueOf(statusStr);
                } catch (IllegalArgumentException e) {
                    status = AccountStatus.PENDING;
                }

                userManager.addUser(new CompanyRepresentative(
                        name, id, email, "password", companyName, department, position, status
                ));
            } catch (Exception e) {
                System.out.println("⚠️ Error loading company representative record: " + String.join(",", r));
            }
        }

        // --- Load Career Centre Staff ---
        List<String[]> staffRecords = FileHandler.readCSV(staffFile);
        for (String[] r : staffRecords) {
            try {
                // CSV: StaffID, Name, Role, Department, Email
                String id = r[0].trim();
                String name = r[1].trim();
                String role = r[2].trim();
                String department = r[3].trim();
                String email = r[4].trim();

                CareerCenterStaff staff = new CareerCenterStaff(name, id, email, "password", department);
                userManager.addUser(staff);
            } catch (Exception e) {
                System.out.println("⚠️ Error loading staff record: " + String.join(",", r));
            }
        }

        System.out.println("✅ All user data loaded successfully (default password = 'password').");
    }

    // ---------- LOAD INTERNSHIPS ----------
    public static InternshipRepository loadInternships(String internshipFile) {
        System.out.println("Loading internships from: " + internshipFile);
        return new InternshipRepository(internshipFile);
    }

    // ---------- APPEND NEW INTERNSHIP ----------
    public static void appendNewInternship(Internship internship) {
        // CSV order: id,title,description,level,major,openDate,closeDate,status,company,repId,slots,visible
        FileHandler.appendToCSV("data/internships.csv", new String[]{
                internship.getInternshipId(),
                internship.getTitle(),
                internship.getDescription(),
                internship.getLevel().name(),
                internship.getPreferredMajor(),
                internship.getOpeningDate(),
                internship.getClosingDate(),
                internship.getStatus().name(),
                internship.getCompanyName(),
                internship.getRepresentativeId(),
                String.valueOf(internship.getNumSlots()),
                String.valueOf(internship.isVisible())
        });
    }
    // ---------- APPEND NEW USER ----------
    public static void appendNewUser(User user) {
        if (user instanceof Student s) {
            // Student: StudentID, Name, Major, Year, Email
            FileHandler.appendToCSV("data/sample_student_list.csv",
                    new String[]{s.getUserId(), s.getName(), s.getMajor(),
                            String.valueOf(s.getYearOfStudy()), s.getEmail()});

        } else if (user instanceof CompanyRepresentative rep) {
            // CompanyRep: CompanyRepID, Name, CompanyName, Department, Position, Email, Status
            FileHandler.appendToCSV("data/sample_company_representative_list.csv",
                    new String[]{rep.getUserId(), rep.getName(), rep.getCompanyName(),
                            rep.getDepartment(), rep.getPosition(),
                            rep.getEmail(), rep.getAccountStatus().name()});

        } else if (user instanceof CareerCenterStaff staff) {
            // Staff: StaffID, Name, Role, Department, Email
            FileHandler.appendToCSV("data/sample_staff_list.csv",
                    new String[]{staff.getUserId(), staff.getName(), staff.getRole(),
                            staff.getStaffDepartment(), staff.getEmail()});
        }
    }

    // ---------- UPDATE EXISTING USER ----------
    public static void updateUser(User user, UserManager userManager) {
        if (user instanceof Student) {
            saveStudents("data/sample_student_list.csv", userManager);
        } else if (user instanceof CompanyRepresentative) {
            saveCompanyReps("data/sample_company_representative_list.csv", userManager);
        } else if (user instanceof CareerCenterStaff) {
            saveStaff("data/sample_staff_list.csv", userManager);
        }
    }

    // ---------- SAVE STUDENTS ----------
    public static void saveStudents(String filePath, UserManager userManager) {
        List<String[]> rows = new ArrayList<>();
        String header = "StudentID,Name,Major,Year,Email";

        for (User u : userManager.getAllUsers()) {
            if (u instanceof Student s) {
                rows.add(new String[]{
                        s.getUserId(), s.getName(), s.getMajor(),
                        String.valueOf(s.getYearOfStudy()), s.getEmail()
                });
            }
        }

        FileHandler.writeCSV(filePath, rows, header);
    }

    // ---------- SAVE COMPANY REPRESENTATIVES ----------
    public static void saveCompanyReps(String filePath, UserManager userManager) {
        List<String[]> rows = new ArrayList<>();
        String header = "CompanyRepID,Name,CompanyName,Department,Position,Email,Status";

        for (User u : userManager.getAllUsers()) {
            if (u instanceof CompanyRepresentative rep) {
                rows.add(new String[]{
                        rep.getUserId(), rep.getName(), rep.getCompanyName(),
                        rep.getDepartment(), rep.getPosition(),
                        rep.getEmail(), rep.getAccountStatus().name()
                });
            }
        }

        FileHandler.writeCSV(filePath, rows, header);
    }

    // ---------- SAVE CAREER CENTER STAFF ----------
    public static void saveStaff(String filePath, UserManager userManager) {
        List<String[]> rows = new ArrayList<>();
        String header = "StaffID,Name,Role,Department,Email";

        for (User u : userManager.getAllUsers()) {
            if (u instanceof CareerCenterStaff s) {
                rows.add(new String[]{
                        s.getUserId(), s.getName(), s.getRole(),
                        s.getStaffDepartment(), s.getEmail()
                });
            }
        }

        FileHandler.writeCSV(filePath, rows, header);
    }

    // ---------- SAVE INTERNSHIPS ----------
    public static void saveInternships(InternshipRepository internshipRepo) {
        internshipRepo.saveInternships();
    }


    // ---------- FULL SAVE (BACKUP) ----------
    public static void saveAllUsers(UserManager userManager) {
        saveStudents("data/sample_student_list.csv", userManager);
        saveCompanyReps("data/sample_company_representative_list.csv", userManager);
        saveStaff("data/sample_staff_list.csv", userManager);
        System.out.println("💾 All user data saved successfully.");
    }
}
