package main;

import main.boundary.MainMenu;
import main.control.AppContext;
import main.data.DataLoader;

public class InternshipPlacementSystem {
    public static void main(String[] args) {
        AppContext app = new AppContext(); // Shared managers and data
        MainMenu mainMenu = new MainMenu(app);
        mainMenu.start();

        // Optional: save on exit
        DataLoader.saveAllUsers(app.userManager);
        DataLoader.saveInternships(app.internshipRepository);
        System.out.println("💾 All data saved. Goodbye!");
    }
}
