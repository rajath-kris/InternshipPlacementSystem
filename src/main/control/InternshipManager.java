package main.control;

import main.data.InternshipRepository;
import main.entity.*;

import main.entity.enums.InternshipLevel;
import main.entity.enums.InternshipStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InternshipManager {

    private final InternshipRepository internshipRepo;
    private static final int MAX_INTERNSHIPS_PER_REP = 5;

    public InternshipManager(InternshipRepository internshipRepo) {
        this.internshipRepo = internshipRepo;
    }

    public void createInternship(
            String repId, String companyName,
            String title, String description,
            InternshipLevel level, String major,
            String openingDate, String closingDate,
            int slots
    ) {

        //Check rep internship limit
        long repCount = internshipRepo.getAllInternships().stream()
                .filter(i -> i.getRepresentativeId().equalsIgnoreCase(repId))
                .count();

        if (repCount >= MAX_INTERNSHIPS_PER_REP) {
            System.out.println("You can only post up to " + MAX_INTERNSHIPS_PER_REP + " internships.");
            return;
        }

        // Validate inputs
        if (title.isBlank() || description.isBlank()) {
            System.out.println("Title and description cannot be empty.");
            return;
        }

        if (slots <= 0) {
            System.out.println("Number of slots must be positive.");
            return;
        }

        // Auto-generate readable ID
        String newId = generateInternshipId();

        // Create internship object
        Internship internship = new Internship(
                newId, title, description,
                level, major, openingDate, closingDate,
                companyName, repId, slots
        );

        // Default status: PENDING, not visible yet
        internship.setStatus(InternshipStatus.PENDING);
        internship.setVisible(false);

        // Save to repository
        internshipRepo.addInternship(internship);
        internshipRepo.saveInternships();

        System.out.println("✅Internship created successfully: " + newId);
    }


    // --- HELPER: Generate Short Internship IDs ---
    private String generateInternshipId() {
        int maxId = 0;
        for (Internship i : internshipRepo.getAllInternships()) {
            try {
                if (i.getInternshipId().startsWith("INT")) {
                    int num = Integer.parseInt(i.getInternshipId().substring(3));
                    if (num > maxId) maxId = num;
                }
            } catch (NumberFormatException ignored) {}
        }
        return String.format("INT%03d", maxId + 1); // e.g. INT001
    }

    // --- INTERNAL HELPER (used only for seeding) ---
    protected void addInternship(Internship internship) {
        if (internship.getInternshipId() == null || internship.getInternshipId().isBlank()) {
            internship.setInternshipId(generateInternshipId());
        }
        internshipRepo.addInternship(internship);
        internshipRepo.saveInternships();
    }


    // --- EDIT INTERNSHIP (Company Rep) ---
    public void editInternship(
            String internshipId, String repId,
            String newTitle, String newDescription,
            InternshipLevel newLevel, String newMajor,
            String newOpenDate, String newCloseDate, int newSlots
    ) {
        Internship i = findInternshipById(internshipId);

        if (i == null) {
            System.out.println(" ❌ Internship not found.");
            return;
        }

        // Ownership check
        if (!i.getRepresentativeId().equalsIgnoreCase(repId)) {
            System.out.println(" Access denied: You can only edit your own internships.");
            return;
        }

        // Only editable when pending
        if (i.getStatus() != InternshipStatus.PENDING) {
            System.out.println(" Cannot edit. Internship has already been " + i.getStatus());
            return;
        }

        // Update
        i.setTitle(newTitle);
        i.setDescription(newDescription);
        i.setLevel(newLevel);
        i.setPreferredMajor(newMajor);
        i.setOpeningDate(newOpenDate);
        i.setClosingDate(newCloseDate);
        i.setNumSlots(newSlots);

        internshipRepo.saveInternships();
        System.out.println("✅ Internship " + internshipId + " updated successfully.");
    }

    // --- TOGGLE VISIBILITY (Company Rep) ---
    public void toggleVisibilityForRep(String repId, String internshipId, boolean visible) {
        Internship i = findInternshipById(internshipId);

        if (i == null) {
            System.out.println("❌ Internship not found.");
            return;
        }

        if (!i.getRepresentativeId().equalsIgnoreCase(repId)) {
            System.out.println("❌ Access denied: You can only toggle visibility for your own internships.");
            return;
        }

        if (visible && i.getStatus() != InternshipStatus.APPROVED) {
            System.out.println("❌ Only approved internships can be made visible.");
            return;
        }


        i.setVisible(visible);
        internshipRepo.saveInternships();

        System.out.println("💡 Visibility for " + i.getTitle() + " set to " + (visible ? "ON" : "OFF"));
    }

    public List<Internship> getPendingInternships() {
        return getAllInternships().stream()
                .filter(i -> i.getStatus() == InternshipStatus.PENDING)
                .toList();
    }

    public void approveInternship(Internship internship) {
        internship.setStatus(InternshipStatus.APPROVED);
        saveAllInternships();
    }

    public void rejectInternship(Internship internship) {
        internship.setStatus(InternshipStatus.REJECTED);
        saveAllInternships();
    }


    // --- UPDATE STATUS (For testing only) ---
    public void updateInternshipStatus(String internshipId, InternshipStatus newStatus) {
        Internship i = findInternshipById(internshipId);
        if (i != null) {
            i.setStatus(newStatus);
            internshipRepo.saveInternships();
            System.out.println("Internship " + internshipId + " status updated to " + newStatus);
        } else {
            System.out.println("Internship not found.");
        }
    }

    // --- HELPER: Flexible major matching ---
    public boolean majorsMatch(String studentMajor, String internshipMajor) {
        if (studentMajor == null || internshipMajor == null) return false;

        String s = studentMajor.trim().toLowerCase();
        String i = internshipMajor.trim().toLowerCase();

        // Common mappings
        String[][] majorGroups = {
                {"csc", "cs", "computer science", "computing", "comp sci"},
                {"eee", "electrical", "electrical engineering", "electrical and electronic engineering"},
                {"mech", "mechanical", "mechanical engineering", "mech eng"},
                {"dsai", "data science and ai", "data science & ai"},
                {"ce", "computer engineering"},
                {"chem", "chemical", "chemical engineering"},
                {"env", "environmental", "environmental engineering"},
                {"mat", "materials", "materials science", "materials engineering"}
        };

        for (String[] group : majorGroups) {
            boolean studentInGroup = false, internshipInGroup = false;

            for (String alias : group) {
                if (s.contains(alias)) studentInGroup = true;
                if (i.contains(alias)) internshipInGroup = true;
            }

            if (studentInGroup && internshipInGroup) return true;
        }

        // fallback: substring similarity
        return s.contains(i) || i.contains(s);
    }

    // --- HELPER: Find by ID ---
    public Internship findInternshipById(String internshipId) {
        return internshipRepo.findById(internshipId);
    }

    // --- GETTER ---
    public List<Internship> getAllInternships() {
        return internshipRepo.getAllInternships();
    }

    public void saveAllInternships() {
        internshipRepo.saveInternships();
    }
    public void displayInternshipsForUser(User user, FilterSettings filters) {
        List<Internship> all = getFilteredInternships(filters);
        List<Internship> visibleList = new ArrayList<>();

        boolean isFiltered = filters.isActive();

        if (user instanceof Student s) {
            // Students: only approved, visible, major-compatible, and eligible by level
            for (Internship i : all) {
                boolean visibleOk = i.isVisible() && i.getStatus() == InternshipStatus.APPROVED;
                boolean majorOk = majorsMatch(s.getMajor(), i.getPreferredMajor());
                boolean levelOk = (s.getYearOfStudy() <= 2 && i.getLevel() == InternshipLevel.BASIC)
                        || (s.getYearOfStudy() >= 3); // Y3+ can see all levels

                if (visibleOk && majorOk && levelOk)
                    visibleList.add(i);
            }

            if (visibleList.isEmpty()) {
                System.out.println("No internships available for your criteria.");
                return;
            }

            System.out.println("\n--- AVAILABLE INTERNSHIPS ---");
            if (isFiltered) System.out.println("🎯 Showing filtered results — use 'Set Filters' to modify or clear.\n");
            for (Internship i : visibleList) {
                System.out.println(i.toStudentView());
            }
        } else if (user instanceof CompanyRepresentative rep) {
            // Reps: only their own internships
            for (Internship i : all) {
                if (i.getRepresentativeId().equalsIgnoreCase(rep.getUserId())) {
                    visibleList.add(i);
                }
            }

            if (visibleList.isEmpty()) {
                if(isFiltered) System.out.println("No internships matching your filters");
                else System.out.println("You haven’t created any internships yet.");
                return;
            }

            System.out.println("\n--- MY INTERNSHIPS ---");
            if (isFiltered) System.out.println("🎯 Showing filtered results — use 'Set Filters' to modify or clear.\n");
            visibleList.forEach(System.out::println);

        } else if (user instanceof CareerCenterStaff) {
            // Staff: can view all internships
            if (all.isEmpty()) {
                if(isFiltered) System.out.println("No internships matching your filters");
                else System.out.println("No internships in the system.");
                return;
            }

            System.out.println("\n--- ALL INTERNSHIPS ---");
            if (isFiltered) System.out.println("🎯 Showing filtered results — use 'Set Filters' to modify or clear.\n");
            all.forEach(System.out::println);
        }
    }


    public List<Internship> getFilteredInternships(FilterSettings filters) {
        List<Internship> result = new ArrayList<>();

        for (Internship i : internshipRepo.getAllInternships()) {

            // --- Filter by Status ---
            if (filters.getStatus() != null && i.getStatus() != filters.getStatus())
                continue;

            // --- Filter by Major ---
            if (filters.getPreferredMajor() != null
                    && !majorsMatch(filters.getPreferredMajor(), i.getPreferredMajor()))
                continue;

            // --- Filter by Level ---
            if (filters.getLevel() != null && i.getLevel() != filters.getLevel())
                continue;

            // --- Filter by Visibility ---
            if (filters.getVisible() != null && i.isVisible() != filters.getVisible())
                continue;

            result.add(i);
        }

        // --- Sorting ---
        String sortBy = filters.getSortBy() == null ? "" : filters.getSortBy();
        switch (sortBy) {
            case "closingDate" -> result.sort(Comparator.comparing(Internship::getClosingDate, String.CASE_INSENSITIVE_ORDER));
            case "openingDate" -> result.sort(Comparator.comparing(Internship::getOpeningDate, String.CASE_INSENSITIVE_ORDER));
            default -> result.sort(Comparator.comparing(i -> i.getTitle().toLowerCase())); // alphabetical default
        }

        return result;
    }
    public void generateReport(String statusFilter, String majorFilter, String levelFilter) {
        System.out.println("\n=== INTERNSHIP CREATION REPORT ===");

        List<Internship> internships = getAllInternships();

        // Apply filters
        List<Internship> filtered = internships.stream()
                .filter(i -> (statusFilter == null || i.getStatus().name().equalsIgnoreCase(statusFilter)))
                .filter(i -> (majorFilter == null || majorsMatch(i.getPreferredMajor(), majorFilter)))
                .filter(i -> (levelFilter == null || i.getLevel().name().equalsIgnoreCase(levelFilter)))
                .toList();

        if (filtered.isEmpty()) {
            System.out.println("No internships match the given filters.");
            return;
        }

        System.out.printf("%-8s %-25s %-15s %-15s %-12s %-10s %-12s %-10s %-10s %-10s%n",
                "ID", "Title", "Company", "CreatedBy", "CreatedOn",
                "Level", "Status", "Major", "Slots", "Visible");
        System.out.println("-----------------------------------------------------------------------------------------------");

        for (Internship i : filtered) {
            System.out.printf("%-8s %-25s %-15s %-15s %-12s %-10s %-12s %-10s %d/%d %-10s%n",
                    i.getInternshipId(),
                    i.getTitle(),
                    i.getCompanyName(),
                    i.getRepresentativeId(),          // or name if you want to map it
                    i.getCreatedDate(),
                    i.getLevel(),
                    i.getStatus(),
                    i.getPreferredMajor(),
                    i.getSlotsLeft(), i.getNumSlots(),
                    i.isVisible() ? "Yes" : "No");
        }

        // Summary section
        System.out.println("\n--- SUMMARY ---");
        long approved = filtered.stream().filter(i -> i.getStatus() == InternshipStatus.APPROVED).count();
        long pending = filtered.stream().filter(i -> i.getStatus() == InternshipStatus.PENDING).count();
        long rejected = filtered.stream().filter(i -> i.getStatus() == InternshipStatus.REJECTED).count();

        System.out.printf("Total Internships: %d%n", filtered.size());
        System.out.printf("Approved: %d | Pending: %d | Rejected: %d%n", approved, pending, rejected);
    }


}
