package main.boundary;

import main.entity.FilterSettings;
import main.entity.enums.InternshipLevel;
import main.entity.enums.InternshipStatus;
import main.util.InputHandler;

public class FilterMenu {

    private final InputHandler input = new InputHandler();

    /**
     * Opens a reusable filter configuration menu.
     * @param filters  The FilterSettings instance (persisted per user/menu)
     * @param allowStatus Whether this role can filter by Internship Status
     * @param allowMajor Whether this role can filter by Preferred Major
     * @param allowLevel Whether this role can filter by Internship Level
     * @param allowSort Whether this role can change sorting order
     * @param allowVisibility Whether this role can filter by visibility (ON/OFF/ALL)
     */
    public void open(FilterSettings filters, boolean allowStatus, boolean allowMajor,
                     boolean allowLevel, boolean allowSort, boolean allowVisibility) {

        System.out.println("\n--- FILTER SETTINGS ---");

        if (filters.isActive()) {
            System.out.println("🎯 Filters Active → " + filters);
        } else {
            System.out.println("⚪ No filters active (showing all internships).");
        }

        int option = -1;
        while (true) {
            System.out.println("\nSelect a parameter to change:");
            int count = 1;

            if (allowStatus) System.out.println((count++) + ". Filter by Status");
            if (allowMajor) System.out.println((count++) + ". Filter by Preferred Major");
            if (allowLevel) System.out.println((count++) + ". Filter by Internship Level");
            if (allowVisibility) System.out.println((count++) + ". Filter by Visibility");
            if (allowSort) System.out.println((count++) + ". Change Sorting Order");
            System.out.println((count++) + ". Clear All Filters");
            System.out.println((count) + ". Back");

            option = input.readInt("Enter choice: ", 1, count);

            int index = 1;
            if (allowStatus && option == index++) { setStatusFilter(filters); continue; }
            if (allowMajor && option == index++) { setMajorFilter(filters); continue; }
            if (allowLevel && option == index++) { setLevelFilter(filters); continue; }
            if (allowVisibility && option == index++) { setVisibilityFilter(filters); continue; }
            if (allowSort && option == index++) { setSortOrder(filters); continue; }
            if (option == index++) { filters.clear(); System.out.println("🧹 Filters cleared."); continue; }
            if (option == index) { break; }
        }

        System.out.println("\n✅ Updated Filters: " + filters);
    }

    // --- FILTER SETTERS ---

    private void setStatusFilter(FilterSettings filters) {
        System.out.println("\nChoose status filter:");
        System.out.println("1. PENDING  2. APPROVED  3. REJECTED  4. ANY");
        int c = input.readInt("Select: ", 1, 4);
        filters.setStatus((c == 4) ? null : InternshipStatus.values()[c - 1]);
    }

    private void setMajorFilter(FilterSettings filters) {
        System.out.println("\nEnter major keyword (e.g. CS, EEE) or 'ANY' to remove:");
        String major = input.readString("Major: ");
        if (major.equalsIgnoreCase("any")) major = null;
        filters.setPreferredMajor(major);
    }

    private void setLevelFilter(FilterSettings filters) {
        System.out.println("\nSelect internship level:");
        System.out.println("1. BASIC  2. INTERMEDIATE  3. ADVANCED  4. ANY");
        int c = input.readInt("Select: ", 1, 4);
        filters.setLevel((c == 4) ? null : InternshipLevel.values()[c - 1]);
    }

    private void setVisibilityFilter(FilterSettings filters) {
        System.out.println("\nSelect visibility filter:");
        System.out.println("1. Visible Only");
        System.out.println("2. Hidden Only");
        System.out.println("3. All");
        int c = input.readInt("Select: ", 1, 3);
        filters.setVisible((c == 3) ? null : (c == 1));
    }

    private void setSortOrder(FilterSettings filters) {
        System.out.println("\nSelect sorting order:");
        System.out.println("1. Alphabetical (Default)");
        System.out.println("2. Closing Date");
        System.out.println("3. Opening Date");
        int c = input.readInt("Select: ", 1, 3);
        filters.setSortBy((c == 1) ? "title" : (c == 2) ? "closingDate" : "openingDate");
    }
}
