package main;
import main.boundary.MainMenu;
import main.control.*;
import main.data.DataLoader;

public class InternshipPlacementSystem {
    public static void main(String[] args) {

        UserManager userManager = new UserManager();
        Authenticator auth = new Authenticator(userManager);
        InternshipManager internshipManager = new InternshipManager();

        // Load existing data
        DataLoader.loadUsers(
                userManager,
                "data/sample_student_list.csv",
                "data/sample_company_representative_list.csv",
                "data/sample_staff_list.csv"
        );

        MainMenu mainMenu = new MainMenu(auth,userManager);
        mainMenu.start();
    }

}
