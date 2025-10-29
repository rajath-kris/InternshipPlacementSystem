package main;

import main.boundary.MainMenu;
import main.control.AppContext;
import main.data.DataLoader;

public class InternshipPlacementSystem {
    public static void main(String[] args) {
        AppContext context = new AppContext(); // Shared managers and data

        MainMenu mainMenu = new MainMenu(
                context.authenticator,
                context.userManager,
                context.internshipManager
        );

        mainMenu.start();

        // Optional: save on exit
        DataLoader.saveAllUsers(context.userManager);
        DataLoader.saveInternships(context.internshipRepository);
        System.out.println("💾 All data saved. Goodbye!");
    }
}
