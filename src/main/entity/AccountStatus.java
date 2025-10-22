package main.entity;

/**
 * Enum for Company Representative account status
 *
 * OOP PRINCIPLES APPLIED:
 * 1. TYPE SAFETY - Using enum instead of String prevents invalid values
 * 2. SINGLE RESPONSIBILITY - Dedicated type for status values
 */

public enum AccountStatus {
    PENDING,
    APPROVED,
    REJECTED
}