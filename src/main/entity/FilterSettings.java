package main.entity;

import main.entity.enums.InternshipLevel;
import main.entity.enums.InternshipStatus;

public class FilterSettings {
    private InternshipStatus status;
    private String preferredMajor;
    private InternshipLevel level;
    private String sortBy; // "title", "closingDate", "openingDate"
    private Boolean visible; // null = all, true = visible only, false = hidden only

    public FilterSettings() {
        this.sortBy = "title"; // default alphabetical
    }

    // --- Getters & Setters ---
    public InternshipStatus getStatus() { return status; }
    public void setStatus(InternshipStatus status) { this.status = status; }

    public String getPreferredMajor() { return preferredMajor; }
    public void setPreferredMajor(String preferredMajor) { this.preferredMajor = preferredMajor; }

    public InternshipLevel getLevel() { return level; }
    public void setLevel(InternshipLevel level) { this.level = level; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public Boolean getVisible() { return visible; }
    public void setVisible(Boolean visible) { this.visible = visible; }

    // --- Utility Methods ---

    /** Whether any filter other than default sorting is active */
    public boolean isActive() {
        return status != null
                || preferredMajor != null
                || level != null
                || visible != null
                || !"title".equals(sortBy);
    }

    /** Reset all filters to defaults */
    public void clear() {
        status = null;
        preferredMajor = null;
        level = null;
        visible = null;
        sortBy = "title";
    }

    @Override
    public String toString() {
        String visibilityStr = (visible == null)
                ? "ALL"
                : (visible ? "VISIBLE ONLY" : "HIDDEN ONLY");

        return String.format(
                "Status: %s | Major: %s | Level: %s | Visible: %s | Sort: %s",
                status == null ? "ANY" : status,
                preferredMajor == null ? "ANY" : preferredMajor,
                level == null ? "ANY" : level,
                visibilityStr,
                formatSort(sortBy)
        );
    }

    // Helper to print sort option nicely
    private String formatSort(String sortBy) {
        return switch (sortBy) {
            case "closingDate" -> "Closing Date";
            case "openingDate" -> "Opening Date";
            default -> "Alphabetical";
        };
    }
}
