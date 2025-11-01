package main.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FileHandler - Handles all CSV file input/output operations
 *
 * RESPONSIBILITIES:
 *  - Read CSV files and return data as List<String[]>
 *  - Write data (List<String[]>) back to CSV files
 *
 * OOP & SOLID PRINCIPLES:
 *  - SINGLE RESPONSIBILITY: Handles only file reading/writing
 *  - OPEN/CLOSED: Easily extendable for other file formats
 *  - ENCAPSULATION: File operations are hidden from other components
 */
public class FileHandler {

    /**
     * Reads a CSV file and returns the content as a List of String arrays.
     * Each row corresponds to one record.
     *
     * @param filePath Path to the CSV file
     * @return List of String[] containing rows of the CSV
     */
    public static List<String[]> readCSV(String filePath) {
        List<String[]> records = new ArrayList<>();

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("File not found: " + filePath);
            return records;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean headerSkipped = false;

            while ((line = br.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) continue;

                // Skip header (first line)
                if (!headerSkipped && line.toLowerCase().contains("name")) {
                    headerSkipped = true;
                    continue;
                }

                String[] values = line.split(",", -1); // keep empty fields
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].trim();
                }
                records.add(values);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + filePath);
            e.printStackTrace();
        }

        return records;
    }

    /**
     * Writes data to a CSV file. Overwrites existing file content.
     *
     * @param filePath Path to write to
     * @param rows     List of String[] rows to write
     * @param header   Header line for CSV file
     */
    public static void writeCSV(String filePath, List<String[]> rows, String header) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write header
            if (header != null && !header.isEmpty()) {
                writer.append(header).append("\n");
            }

            // Write rows
            for (String[] row : rows) {
                writer.append(String.join(",", row)).append("\n");
            }

//            System.out.println("Saved successfully to: " + filePath);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + filePath);
            e.printStackTrace();
        }
    }

    /**
     * Appends a single record (String array) to an existing CSV file.
     * Automatically creates the file if it does not exist.
     *
     * @param filePath Path to CSV file
     * @param record   Record to append
     */
    public static void appendToCSV(String filePath, String[] record) {
        try {
            boolean fileExists = new File(filePath).exists();
            try (FileWriter writer = new FileWriter(filePath, true)) {
                if (!fileExists) {
                    System.out.println("File did not exist, created new CSV: " + filePath);
                }
                writer.append(String.join(",", record)).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Error appending to file: " + filePath);
            e.printStackTrace();
        }
    }
}
