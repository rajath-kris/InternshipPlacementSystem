package main.control;

import main.data.ApplicationRepository;
import main.entity.Application;
import main.entity.Internship;
import main.entity.Student;
import main.entity.enums.ApplicationStatus;
import main.entity.enums.InternshipLevel;
import main.entity.enums.InternshipStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ApplicationManager {
    private final ApplicationRepository appRepo;
    private final InternshipManager internshipMgr;
    private static final int MAX_APPLICATIONS_PER_STUDENT = 3;


    public ApplicationManager(ApplicationRepository appRepo, InternshipManager internshipMgr) {
        this.appRepo = appRepo;
        this.internshipMgr = internshipMgr;
    }

    // --- STUDENT APPLY ---
    public void applyForInternship(Student student, String internshipId) {
        Internship internship = internshipMgr.findInternshipById(internshipId);
        if (internship == null) {
            System.out.println("❌ Internship not found.");
            return;
        }

        // --- Rule 0: Student cannot apply if they already accepted an offer ---
        boolean hasAcceptedOffer = appRepo.getApplicationsByStudent(student.getUserId()).stream()
                .anyMatch(a -> a.getStatus() == ApplicationStatus.ACCEPTED);

        if (hasAcceptedOffer) {
            System.out.println(" ❌ You have already accepted an internship offer. You cannot apply for new internships.");
            return;
        }

        // Rule 1: Internship must be visible and approved
        if (!internship.isVisible() || internship.getStatus() != InternshipStatus.APPROVED) {
            System.out.println("❌ Internship is not open for applications.");
            return;
        }

        // --- Rule 2: Check student already has 3 applications ---
        long existingApps = appRepo.getAllApplications().stream()
                .filter(a -> a.getStudentId().equalsIgnoreCase(student.getUserId()))
                .count();

        if (existingApps >= MAX_APPLICATIONS_PER_STUDENT) {
            System.out.println(" ❌ You have already submitted " + MAX_APPLICATIONS_PER_STUDENT + " applications. Please withdraw one before applying again.");
            return;
        }

        //  Rule 3: No applications before opening and after closing date
        LocalDate closing = LocalDate.parse(internship.getClosingDate());
        LocalDate opening = LocalDate.parse(internship.getOpeningDate());

        if (LocalDate.now().isBefore(opening)) {
            System.out.println("❌ The application period has not opened yet.");
            return;
        }
        if (LocalDate.now().isAfter(closing)) {
            System.out.println("❌ The application period has closed.");
            return;
        }
        //  Rule 4: Applications should respect the Major Rules
        if (!internshipMgr.majorsMatch(student.getMajor(), internship.getPreferredMajor())) {
            System.out.printf("❌ You cannot apply. Internship is restricted to %s majors.%n",
                    internship.getPreferredMajor());
            return;
        }


        //  Rule 5: Applications should respect the Level rules
        boolean levelAllowed = (student.getYearOfStudy() <= 2 && internship.getLevel() == InternshipLevel.BASIC)
                || (student.getYearOfStudy()  >= 3); // Year 3+ can apply to any level
        if (!levelAllowed) {
            System.out.println("❌ You are not eligible to apply for this internship level.");
            return;
        }

        // Rule 6 : Check slots left

        if (internship.hasAvailableSlots()) {
            System.out.println("❌ This internship has no remaining slots.");
            return;
        }

        //  Rule 7: Prevent duplicate application for same internship ---
        for (Application existing : appRepo.getApplicationsByStudent(student.getUserId())) {
            if (existing.getInternshipId().equalsIgnoreCase(internshipId)) {
                System.out.println("❌ You already applied for this internship.");
                return;
            }
        }

        //  Create new application
        String appId = generateAppId();
        String date = LocalDate.now().toString();
        Application app = new Application(
                appId,
                student.getUserId(),
                student.getName(),
                student.getMajor(),
                student.getYearOfStudy(),
                internshipId,
                date,
                ApplicationStatus.PENDING
        );
        appRepo.addApplication(app);
        System.out.println("✅ Application submitted successfully!");
    }

    public void approveApplication(Application a) {
        Internship internship = internshipMgr.findInternshipById(a.getInternshipId());
        if (internship == null) return;

        if (!internship.hasAvailableSlots()) {
            System.out.printf("⚠ Internship '%s' is full.%n", internship.getTitle());
            return;
        }

        a.setStatus(ApplicationStatus.SUCCESSFUL);
        internship.decrementSlot();
        saveApplications();
        internshipMgr.saveAllInternships();
    }

    public void rejectApplication(Application a) {
        a.setStatus(ApplicationStatus.UNSUCCESSFUL);
        saveApplications();
    }

    // COMPANY REP REVIEWS APPLICATIONS

    public List<Application> getPendingApplicationsForRep(String repId) {
        List<Application> result = new ArrayList<>();
        for (Internship i : internshipMgr.getAllInternships()) {
            if (i.getRepresentativeId().equalsIgnoreCase(repId)) {
                for (Application a : appRepo.getApplicationsByInternship(i.getInternshipId())) {
                    if (a.getStatus() == ApplicationStatus.PENDING) result.add(a);
                }
            }
        }
        return result;
    }

    public List<Application> getApplicationsForRep(String repId) {
        List<Application> result = new ArrayList<>();

        // Go through all internships created by this rep
        for (Internship internship : internshipMgr.getAllInternships()) {
            if (internship.getRepresentativeId().equalsIgnoreCase(repId)) {
                // Add all applications for that internship
                List<Application> apps = appRepo.getApplicationsByInternship(internship.getInternshipId());
                result.addAll(apps);
            }
        }

        return result;
    }

    public List<Application> getApplicationsForInternship(String internshipId) {
        return appRepo.getApplicationsByInternship(internshipId);
    }


    public void updateApplicationStatus(String appId, ApplicationStatus newStatus) {
        appRepo.updateApplicationStatus(appId, newStatus);
    }


    //  GET STUDENT APPLICATIONS
    public List<Application> getMyApplications(String studentId) {
        return appRepo.getApplicationsByStudent(studentId);
    }

    public void displayApplicationsForStudent(Student student) {
        List<Application> apps = getMyApplications(student.getUserId());

        if (apps.isEmpty()) {
            System.out.println("You have no applications yet.");
            return;
        }

        System.out.println("\n--- YOUR APPLICATIONS ---");
        for (Application a : apps) {
            Internship i = internshipMgr.findInternshipById(a.getInternshipId());
            String title = (i != null) ? i.getTitle() : a.getInternshipId();
            System.out.printf("[%s] %s | Status: %s%n", a.getApplicationId(), title, a.getStatus());
        }
    }

    public boolean hasSuccessfulOffer(Student student) {
        return getMyApplications(student.getUserId()).stream()
                .anyMatch(a -> a.getStatus() == ApplicationStatus.SUCCESSFUL);
    }



    public void acceptOffer(Student student, String appId) {
        Application selected = appRepo.getAllApplications().stream()
                .filter(a -> a.getApplicationId().equalsIgnoreCase(appId)
                        && a.getStudentId().equalsIgnoreCase(student.getUserId()))
                .findFirst().orElse(null);

        if (selected == null) {
            System.out.println("Application not found.");
            return;
        }
        if (selected.getStatus() != ApplicationStatus.SUCCESSFUL) {
            System.out.println("You can only accept a successful offer.");
            return;
        }

        // Accept the selected one
        selected.setStatus(ApplicationStatus.ACCEPTED);

        // Decrement slot count
        Internship acceptedInternship = internshipMgr.findInternshipById(selected.getInternshipId());
        if (acceptedInternship != null) {
            acceptedInternship.decrementSlot();
            internshipMgr.saveAllInternships();
        }

        // Withdraw all other active applications
        for (Application a : appRepo.getAllApplications()) {
            if (a.getStudentId().equalsIgnoreCase(student.getUserId())
                    && !a.getApplicationId().equalsIgnoreCase(appId)
                    && (a.getStatus() == ApplicationStatus.PENDING
                    || a.getStatus() == ApplicationStatus.SUCCESSFUL)) {
                a.setStatus(ApplicationStatus.WITHDRAWN);
            }
        }

        appRepo.saveApplications();
        System.out.println("✅ You have accepted the offer for " + selected.getInternshipId() + ".");
    }

    public void displayWithdrawableApplications(Student student) {
        List<Application> apps = getMyApplications(student.getUserId()).stream()
                .filter(a -> a.getStatus() == ApplicationStatus.PENDING)
                .toList();

        if (apps.isEmpty()) {
            System.out.println("You have no pending applications to withdraw.");
            return;
        }

        System.out.println("\n--- WITHDRAWABLE APPLICATIONS ---");
        for (Application a : apps) {
            Internship i = internshipMgr.findInternshipById(a.getInternshipId());
            String title = (i != null) ? i.getTitle() : a.getInternshipId();
            System.out.printf("[%s] %s | Status: %s%n", a.getApplicationId(), title, a.getStatus());
        }
    }


    public void withdrawApplication(Student student, String appId) {
        Application app = appRepo.getAllApplications().stream()
                .filter(a -> a.getApplicationId().equalsIgnoreCase(appId)
                        && a.getStudentId().equalsIgnoreCase(student.getUserId()))
                .findFirst().orElse(null);

        if (app == null) {
            System.out.println("Application not found.");
            return;
        }

        Internship internship = internshipMgr.findInternshipById(app.getInternshipId());
        if (internship == null) {
            System.out.println("Internship not found for this application.");
            return;
        }

        //  Rule: Students can only withdraw from approved internships
        if (internship.getStatus() != InternshipStatus.APPROVED) {
            System.out.println("You can only withdraw applications for approved internships.");
            return;
        }

        //  Rule: Only pending applications can request withdrawal
        if (app.getStatus() != ApplicationStatus.PENDING) {
            System.out.println("Only pending applications can be withdrawn.");
            return;
        }

        //  Mark as withdrawal requested
        app.setStatus(ApplicationStatus.WITHDRAWAL_PENDING);
        appRepo.saveApplications();
        System.out.println("✅ Withdrawal request submitted. Awaiting staff approval.");
    }

    // --- WITHDRAWAL APPROVAL LOGIC ---
    public List<Application> getPendingWithdrawals() {
        return getAllApplications().stream()
                .filter(a -> a.getStatus() == ApplicationStatus.WITHDRAWAL_PENDING)
                .toList();
    }

    public void approveWithdrawal(Application application) {
        application.setStatus(ApplicationStatus.WITHDRAWN);
        saveApplications(); // ✅ persistence
    }

    public void rejectWithdrawal(Application application) {
        application.setStatus(ApplicationStatus.PENDING);
        saveApplications(); // ✅ persistence
    }


    // --- HELPER: Generate readable ID ---
    private String generateAppId() {
        int max = 0;
        for (Application a : appRepo.getAllApplications()) {
            try {
                if (a.getApplicationId().startsWith("APP")) {
                    int num = Integer.parseInt(a.getApplicationId().substring(3));
                    if (num > max) max = num;
                }
            } catch (Exception ignored) {}
        }
        return String.format("APP%03d", max + 1);
    }

    public List<Application> getAllApplications() {
        return appRepo.getAllApplications();
    }

    public void saveApplications() {
        appRepo.saveApplications();
    }


}
