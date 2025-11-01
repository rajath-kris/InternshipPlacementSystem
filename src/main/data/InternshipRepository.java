package main.data;

import main.entity.Internship;
import main.entity.enums.InternshipLevel;
import main.entity.enums.InternshipStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * InternshipRepository
 *
 * Handles persistence (loading/saving) of Internship objects from/to CSV files.
 * Uses FileHandler for I/O.
 *
 * OOP & SOLID:
 *  - SINGLE RESPONSIBILITY: Only manages internship data access.
 *  - DEPENDENCY INVERSION: Depends on FileHandler abstraction for I/O.
 */
public class InternshipRepository {

    private final List<Internship> internships;
    private final String filePath;

    // CSV header — used by FileHandler.writeCSV
    private static final String CSV_HEADER =
            "id,title,description,level,major,openDate,closeDate,status,company,repId,totalSlots,slotsLeft,visible";

    public InternshipRepository(String filePath) {
        this.filePath = filePath;
        this.internships = loadInternships();
    }

    // --- ADD ---
    public void addInternship(Internship internship) {
        internships.add(internship);
    }

    // --- GET ALL ---
    public List<Internship> getAllInternships() {
        return internships;
    }

    // --- FIND BY ID ---
    public Internship findById(String internshipId) {
        for (Internship i : internships) {
            if (i.getInternshipId().equalsIgnoreCase(internshipId)) {
                return i;
            }
        }
        return null;
    }

    // --- UPDATE ---
    public void updateInternship(Internship updated) {
        for (int j = 0; j < internships.size(); j++) {
            if (internships.get(j).getInternshipId().equalsIgnoreCase(updated.getInternshipId())) {
                internships.set(j, updated);
                return;
            }
        }
    }

    // --- SAVE TO CSV ---
    public void saveInternships() {
        List<String[]> data = new ArrayList<>();

        for (Internship i : internships) {
            data.add(new String[]{
                    i.getInternshipId(),
                    i.getTitle(),
                    i.getDescription(),
                    i.getLevel().name(),
                    i.getPreferredMajor(),
                    i.getOpeningDate(),
                    i.getClosingDate(),
                    i.getStatus().name(),
                    i.getCompanyName(),
                    i.getRepresentativeId(),
                    String.valueOf(i.getNumSlots()),
                    String.valueOf(i.getSlotsLeft()),
                    String.valueOf(i.isVisible())
            });
        }

        FileHandler.writeCSV(filePath, data, CSV_HEADER);
    }

    // --- LOAD FROM CSV ---
    private List<Internship> loadInternships() {
        List<Internship> list = new ArrayList<>();
        List<String[]> raw = FileHandler.readCSV(filePath);
        boolean headerSkipped = false;


        for (String[] row : raw) {
            try {
                // Expected CSV: id, title, desc, level, major, open, close, status, company, repId, slots, slotsLeft visible

                if (row.length == 0 || row[0].trim().isEmpty()) continue;
                if (!headerSkipped && row[0].toLowerCase().contains("id")) {
                    headerSkipped = true;
                    continue;
                }
                if (row.length < 13) continue;

                // --- LEVEL ---
                InternshipLevel level;

                try {
                    level = InternshipLevel.valueOf(row[3].trim().toUpperCase());
                } catch (IllegalArgumentException e) {
                    System.out.println("Unknown internship level: " + row[3] + " (defaulting to BASIC)");
                    level = InternshipLevel.BASIC;
                }
                // Create internship using existing constructor (10 args)
                Internship internship = new Internship(
                        row[0], // internshipId
                        row[1], // title
                        row[2], // description
                        level,
                        row[4], // preferredMajor
                        row[5], // openingDate
                        row[6], // closingDate
                        row[8], // companyName
                        row[9], // representativeId
                        Integer.parseInt(row[10]) // numSlots
                );
                internship.setSlotsLeft(Integer.parseInt(row[11]));
                // Set persisted status & visibility
                internship.setStatus(InternshipStatus.valueOf(row[7].toUpperCase()));
                internship.setVisible(Boolean.parseBoolean(row[12]));

                list.add(internship);

            } catch (Exception e) {
                System.err.println("Skipping invalid internship row: " + e.getMessage());
            }
        }

        return list;
    }
}
