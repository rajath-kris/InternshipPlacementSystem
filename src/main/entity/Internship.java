package main.entity;

import main.entity.enums.InternshipLevel;
import main.entity.enums.InternshipStatus;

import java.time.LocalDate;

public class Internship {
    private String internshipId;
    private String title;
    private String description;
    private InternshipLevel level;
    private String preferredMajor;
    private String openingDate;
    private String closingDate;
    private InternshipStatus status;
    private String companyName;
    private String representativeId;
    private int numSlots;     // total slots
    private int slotsLeft;     // available slots
    private boolean visible;
    private String createdDate;


    public Internship(String internshipId, String title, String description,
                      InternshipLevel level, String preferredMajor, String openingDate,
                      String closingDate, String companyName, String representativeId,
                      int numSlots) {

        this.internshipId = internshipId;
        this.title = title;
        this.description = description;
        this.level = level;
        this.preferredMajor = preferredMajor;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.companyName = companyName;
        this.representativeId = representativeId;
        this.numSlots = numSlots;
        this.slotsLeft = numSlots;   // initialize equal to total


        this.status = InternshipStatus.PENDING; // default
        this.visible = false;                    // default
        this.createdDate = LocalDate.now().toString();

    }

    //Getters & Setters
    public String getInternshipId() { return internshipId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public InternshipLevel getLevel() { return level; }
    public String getPreferredMajor() { return preferredMajor; }
    public String getOpeningDate() { return openingDate; }
    public String getClosingDate() { return closingDate; }
    public InternshipStatus getStatus() { return status; }
    public String getCompanyName() { return companyName; }
    public String getRepresentativeId() { return representativeId; }
    public int getNumSlots() { return numSlots; }
    public int getSlotsLeft() { return slotsLeft; }
    public String getCreatedDate(){return createdDate;}
    public boolean isVisible() { return visible; }

    //  CONTROLLED
    public void setInternshipId(String internshipId) { this.internshipId = internshipId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setLevel(InternshipLevel level) { this.level = level; }
    public void setPreferredMajor(String preferredMajor) { this.preferredMajor = preferredMajor; }
    public void setOpeningDate(String openingDate) { this.openingDate = openingDate; }
    public void setClosingDate(String closingDate) { this.closingDate = closingDate; }
    public void setNumSlots(int newTotal) {
        this.numSlots = newTotal;
        if (slotsLeft > newTotal) slotsLeft = newTotal; // clamp
    }
    public void setSlotsLeft(int slotsLeft) { this.slotsLeft = slotsLeft; }


    public void setStatus(InternshipStatus status) { this.status = status; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public void decrementSlot() {
        if (slotsLeft > 0) slotsLeft--;
    }

    public boolean hasAvailableSlots() {
        return slotsLeft > 0;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s, %s) - %s | Status: %s | Visible: %s",
                internshipId, title, companyName, level, preferredMajor, status, visible ? "ON" : "OFF");
    }

    public String toStudentView() {
        return String.format("[%s] %s\nDescription: %s\nPreferred Major: %s\nOpening: %s  |  Closing: %s  | Total Slots: %d | Slots Left: %d\n",
                internshipId, title, description, preferredMajor, openingDate, closingDate, numSlots, slotsLeft);
    }
}
