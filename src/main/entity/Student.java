package main.entity;

/**
 * Represents a Student user
 *
 * OOP PRINCIPLES APPLIED:
 * 1. INHERITANCE - Extends User class, inherits common properties
 * 2. SINGLE RESPONSIBILITY - Only handles student-specific data
 * 3. LISKOV SUBSTITUTION PRINCIPLE - Can be used anywhere a User is expected
 */

public class Student extends User {
    private int yearOfStudy;
    private String major;

    public Student(String name, String id, String email, String password, int yearOfStudy, String major) {
        super(name, id, email, password);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
    }

    public int getYearOfStudy() {return yearOfStudy;}
    public void setYearOfStudy(int yearOfStudy) {
        if (yearOfStudy >= 1 && yearOfStudy <= 4) {
            this.yearOfStudy = yearOfStudy;
        }
        else {
        throw new IllegalArgumentException("Year of study must be between 1 and 4");
        }
    }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public String toString() {
        return String.format("Student: %s [ID: %s, Year: %d, Major: %s]",
                getName(), getUserId(), yearOfStudy, major);
    }

}
