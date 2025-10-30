package main.control;

import main.data.DataLoader;
import main.entity.CompanyRepresentative;
import main.entity.enums.AccountStatus;

public class CompanyRepManager {

    private final UserManager userManager;

    public CompanyRepManager(UserManager userManager) {
        this.userManager = userManager;
    }

    public void registerNewRep(String name, String email, String company, String dept, String position) {
        if (userManager.userExists(email)) {
            System.out.println("⚠️ A representative with this email already exists.");
            return;
        }

        String repId = "REP" + System.currentTimeMillis();
        CompanyRepresentative rep = new CompanyRepresentative(
                name,
                repId,
                email,
                "password",
                company,
                dept,
                position,
                AccountStatus.PENDING
        );

        userManager.addUser(rep);
        DataLoader.appendNewUser(rep);
        System.out.println("✅ Company Representative registered successfully!");
        System.out.println("Account status: PENDING approval by Career Centre Staff.");
    }
}
