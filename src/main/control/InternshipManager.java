package main.control;

import main.entity.*;
import main.entity.enums.*;

import java.util.ArrayList;
import java.util.List;

public class InternshipManager {
    private final List<Internship> internships = new ArrayList<>();

    // --- CREATE ---
    public void addInternship(Internship internship) {
        internships.add(internship);
        System.out.println("✅ Internship added: " + internship.getTitle());
    }

    // --- UPDATE STATUS (Staff only) ---
    public void updateInternshipStatus(String internshipId, InternshipStatus newStatus) {
        Internship i = findInternshipById(internshipId);
        if (i != null) {
            i.setStatus(newStatus);
            System.out.println("Internship " + internshipId + " status updated to " + newStatus);
        } else {
            System.out.println("⚠Internship not found.");
        }
    }

    // --- TOGGLE VISIBILITY (Company Rep) ---
    public void toggleVisibility(String internshipId, boolean visibility) {
        Internship i = findInternshipById(internshipId);
        if (i != null) {
            i.setVisible(visibility);
            System.out.println("Visibility for " + i.getTitle() + " set to " + visibility);
        } else {
            System.out.println("Internship not found.");
        }
    }

    // --- EDIT INTERNSHIP (Company Rep) ---
    public void editInternship(String internshipId, String repId,
                               String newTitle, String newDescription,
                               InternshipLevel newLevel, String newMajor,
                               String newOpenDate, String newCloseDate, int newSlots) {

        Internship i = findInternshipById(internshipId);

        if (i == null) {
            System.out.println("Internship not found.");
            return;
        }

        // Rule: Only the representative who created it can edit it
        if (!i.getRepresentativeId().equalsIgnoreCase(repId)) {
            System.out.println("Access denied: You can only edit your own internships.");
            return;
        }

        // Rule: Cannot edit once approved or rejected
        if (i.getStatus() != InternshipStatus.PENDING) {
            System.out.println("Cannot edit. Internship has already been " + i.getStatus());
            return;
        }

        // Update allowed fields
        i.setTitle(newTitle);
        i.setDescription(newDescription);
        i.setLevel(newLevel);
        i.setPreferredMajor(newMajor);
        i.setOpeningDate(newOpenDate);
        i.setClosingDate(newCloseDate);
        i.setNumSlots(newSlots);

        System.out.println("Internship " + internshipId + " updated successfully.");
    }

    // --- VIEW ALL INTERNSHIPS (Staff) ---
    public void displayAllInternships() {
        System.out.println("\n---- ALL INTERNSHIPS ----");

        if (internships.isEmpty()) {
            System.out.println("No internships yet.\n");
            return;
        }

        for (Internship i : internships) {
            System.out.println(i);
        }

    }

    // --- VIEW MY INTERNSHIPS (Company Rep) ---
    public void displayMyInternships(String repId) {
        System.out.println("\n---- MY INTERNSHIPS ----");
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
    public List<Internship> getVisibleInternships(String major, int year) {
        List<Internship> visibleList = new ArrayList<>();
        for (Internship i : internships) {
            boolean levelAllowed = (year <= 2 && i.getLevel() == InternshipLevel.BASIC)
                    || (year >= 3); // Y3+ can apply to any
            if (i.isVisible() && i.getStatus() == InternshipStatus.APPROVED
                    && i.getPreferredMajor().equalsIgnoreCase(major)
                    && levelAllowed) {
                visibleList.add(i);
            }
        }
        return visibleList;
    }

    // --- HELPER: Find by ID ---
    public Internship findInternshipById(String internshipId) {
        for (Internship i : internships) {
            if (i.getInternshipId().equalsIgnoreCase(internshipId)) {
                return i;
            }
        }
        return null;
    }

    // --- GETTER ---
    public List<Internship> getAllInternships() {
        return internships;
    }


}
