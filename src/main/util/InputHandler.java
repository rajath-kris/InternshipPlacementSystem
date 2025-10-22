package main.util;

import java.util.Scanner;

/**
 * InputHandler - Centralized utility for validated console input
 *
 * RESPONSIBILITIES:
 *  - Encapsulate Scanner usage
 *  - Validate user inputs (string, int, email, password)
 *  - Prevent input duplication in UI classes
 */
public class InputHandler {
    private final Scanner sc;

    public InputHandler() {
        this.sc = new Scanner(System.in);
    }

    /** Read integer with range check */
    public int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(sc.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("⚠Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    /** Read general non-empty string */
    public String readString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("Input cannot be empty.");
        }
    }

    /** Read email with basic validation */
    public String readEmail(String prompt) {
        while (true) {
            System.out.print(prompt);
            String email = sc.nextLine().trim();
            if (email.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {
                return email;
            }
            System.out.println("Invalid email format. Please try again.");
        }
    }

    /** Read password (no echo handling, just basic validation) */
    public String readPassword(String prompt) {
        while (true) {
            System.out.print(prompt);
            String pwd = sc.nextLine().trim();
            if (pwd.length() >= 4) return pwd;
            System.out.println("Password must be at least 4 characters long.");
        }
    }

    /** Read yes/no confirmation */
    public boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (Y/N): ");
            String input = sc.nextLine().trim().toLowerCase();
            if (input.equals("y")) return true;
            if (input.equals("n")) return false;
            System.out.println("Please enter Y or N.");
        }
    }

    /** Close scanner safely */
    public void closeScanner() {
        sc.close();
    }
}
