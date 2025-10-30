package main.control;

import main.data.InternshipRepository;
import main.entity.Internship;
import main.entity.enums.InternshipLevel;
import main.entity.enums.InternshipStatus;

import java.util.ArrayList;
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

    // --- INTERNAL HELPER (used only for seeding or system bootstrapping) ---
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

        i.setVisible(visible);
        internshipRepo.saveInternships();

        System.out.println("💡 Visibility for " + i.getTitle() + " set to " + (visible ? "ON" : "OFF"));
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

    // --- VIEW ALL INTERNSHIPS (Staff) ---
    public void displayAllInternships() {
        System.out.println("\n---- ALL INTERNSHIPS ----");
        List<Internship> internships = internshipRepo.getAllInternships();

        if (internships.isEmpty()) {
            System.out.println("No internships yet.\n");
            return;
        }

        for (Internship i : internships) {
            System.out.println(i);
        }
    }

    // --- VIEW  INTERNSHIPS (Company Rep) ---
    public void displayInternshipsForRep(String repId) {
        System.out.println("\n---- MY INTERNSHIPS ----");
        List<Internship> internships = internshipRepo.getAllInternships();
        boolean found = false;

        for (Internship i : internships) {
            if (i.getRepresentativeId().equalsIgnoreCase(repId)) {
                System.out.println(i);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No internships created yet.\n");
        }
    }

    // --- FILTER FOR STUDENTS ---
    public List<Internship> getVisibleInternshipsForStudent(String major, int year) {
        List<Internship> internships = internshipRepo.getAllInternships();
        List<Internship> visibleList = new ArrayList<>();

        for (Internship i : internships) {
            boolean levelAllowed = (year <= 2 && i.getLevel() == InternshipLevel.BASIC)
                    || (year >= 3); // Y3+ can apply to any
            if (i.isVisible() && i.getStatus() == InternshipStatus.APPROVED
                    && majorsMatch(major, i.getPreferredMajor())
                    && levelAllowed) {
                visibleList.add(i);
            }
        }
        return visibleList;
    }
    // --- HELPER: Flexible major matching ---
    private boolean majorsMatch(String studentMajor, String internshipMajor) {
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

}
