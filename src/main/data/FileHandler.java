package main.data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    // Reads a CSV file and returns lines (excluding header)
    public static List<String[]> readCSV(String filePath) {
        List<String[]> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isHeader = true;
            while ((line = br.readLine()) != null) {
                if (isHeader) { // skip header
                    isHeader = false;
                    continue;
                }
                records.add(line.split(",", -1)); // keep empty fields
            }
        } catch (IOException e) {
            System.out.println("⚠Error reading file: " + filePath);
            e.printStackTrace();
        }
        return records;
    }
}
