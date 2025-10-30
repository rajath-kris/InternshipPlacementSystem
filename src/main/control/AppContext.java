package main.control;
import main.data.*;

/**
 * AppContext - One place where all the classes are instantiated
 * Prevents multiple instances of managers and repositories.
 */
public class AppContext {
    public final UserManager userManager;
    public final CompanyRepManager companyRepManager;
    public final Authenticator authenticator;
    public final InternshipRepository internshipRepository;
    public final InternshipManager internshipManager;
    public final ApplicationRepository applicationRepository;
    public final ApplicationManager applicationManager;

    public AppContext() {
        // Initialize User and Auth
        userManager = new UserManager();
        companyRepManager = new CompanyRepManager(userManager);

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

        applicationRepository = new ApplicationRepository("data/applications.csv");
        applicationManager = new ApplicationManager(applicationRepository,internshipManager);

        System.out.println("✅ AppContext initialized successfully.");
    }
}
