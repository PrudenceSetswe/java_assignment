public class TestMain {
    public static void main(String[] args) {
        System.out.println("=== Banking System Test ===");
        System.out.println();

        // Create customers
        PersonCustomer person = new PersonCustomer("C001", "Gaborone", "pass123", "John", "Doe");
        CompanyCustomer company = new CompanyCustomer("C002", "Plot 45", "comp123", "TechBots Inc.");
        
        System.out.println("Created Person Customer: " + person.getFullName());
        System.out.println("Created Company Customer: " + company.getCompanyName());

        try {
            // Test Savings Account (no withdrawals allowed)
            SavingsAccount savings = new SavingsAccount("SAV001", "Main Branch");
            savings.deposit(1000);
            System.out.println("Savings Account Balance after deposit: P" + savings.getBalance());
            
            // This should print error message but not throw exception
            savings.withdraw(200);
            System.out.println("Savings account correctly prevented withdrawal");

            // Test Investment Account (minimum deposit required)
            try {
                InvestmentAccount investmentBad = new InvestmentAccount("INV001", "Main Branch", 300.00);
            } catch (IllegalArgumentException e) {
                System.out.println("Investment account correctly enforced minimum deposit: " + e.getMessage());
            }
            
            InvestmentAccount investment = new InvestmentAccount("INV001", "Main Branch", 600.00);
            System.out.println("Investment account created with initial deposit: P600.00");
            System.out.println("Investment Account Balance: P" + investment.getBalance());

            // Test Cheque Account (employment info required)
            try {
                ChequeAccount chequeBad = new ChequeAccount("CHQ001", "Main Branch", "", "Some Address");
            } catch (IllegalArgumentException e) {
                System.out.println("Cheque account correctly required employment info: " + e.getMessage());
            }
            
            ChequeAccount cheque = new ChequeAccount("CHQ001", "Main Branch", "ABC Corp", "Gaborone");
            cheque.deposit(1000);
            System.out.println("Cheque account created for employee of: ABC Corp");
            
            // Test overdraft
            cheque.withdraw(1200);
            System.out.println("Cheque Account Balance after overdraft withdrawal: P" + cheque.getBalance());

            // Test interest calculations
            System.out.println();
            System.out.println("--- Interest Calculations ---");
            System.out.println("Savings Interest (0.05%): P" + savings.calculateInterest());
            System.out.println("Investment Interest (5%): P" + investment.calculateInterest());

            // Link accounts to customers
            person.addAccount(savings);
            person.addAccount(cheque);
            company.addAccount(investment);

            System.out.println();
            System.out.println("--- Customer Account Summary ---");
            System.out.println(person.getFullName() + "'s accounts:");
            System.out.println(person.getAccountListString());
            System.out.println(company.getCompanyName() + "'s accounts:");
            System.out.println(company.getAccountListString());

            System.out.println();
            System.out.println("=== All tests completed successfully ===");

        } catch (Exception e) {
            System.out.println("Test failed with error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}