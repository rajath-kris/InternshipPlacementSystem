package main.control;
import main.data.*;

/**
 * AppContext - Holds shared singletons for the whole app.
 * Prevents multiple instances of managers and repositories.
 */
public class AppContext {
    public final UserManager userManager;
    public final Authenticator authenticator;
    public final InternshipRepository internshipRepository;
    public final InternshipManager internshipManager;

    public AppContext() {
        // Initialize User and Auth
        userManager = new UserManager();
        authenticator = new Authenticator(userManager);

        // Load all users
        DataLoader.loadUsers(
                userManager,
                "data/sample_student_list.csv",
                "data/sample_company_representative_list.csv",
                "data/sample_staff_list.csv"
        );

        // Load internships
        internshipRepository =  new InternshipRepository("data/internships.csv");
        internshipManager = new InternshipManager(internshipRepository);

        System.out.println("✅ AppContext initialized successfully.");
    }
}
