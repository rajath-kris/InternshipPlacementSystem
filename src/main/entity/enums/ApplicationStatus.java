package main.entity.enums;

public enum ApplicationStatus {
    PENDING,        // waiting for company rep review
    SUCCESSFUL,     // approved by company rep
    UNSUCCESSFUL,   // rejected by company rep
    ACCEPTED,       // student accepted placement
    WITHDRAWN,       // student withdrew or auto-withdrawn
    WITHDRAWAL_PENDING // Pending withdrawal --> needs approval from staff
}
