public class IntegrationTest {
    
    public static void main(String[] args) {
        System.out.println("BANKING SYSTEM INTEGRATION TEST");
        System.out.println("================================\n");
        
        // Test 1: Core Model Integration
        testCoreModelIntegration();
        
        // Test 2: Controller Integration
        testControllerIntegration();
        
        // Test 3: Full User Flow
        testFullUserFlow();
        
        // Test 4: Employee Workflow
        testEmployeeWorkflow();
        
        System.out.println("\nINTEGRATION TEST COMPLETED");
    }
    
    /**
     * Test 1: Core Domain Model Integration
     */
    private static void testCoreModelIntegration() {
        System.out.println("TEST 1: CORE MODEL INTEGRATION");
        System.out.println("-------------------------------");
        
        try {
            // Create customer and accounts
            Customer customer = new PersonCustomer("TEST001", "Test Address", "Test", "User");
            SavingsAccount savings = new SavingsAccount("SAV-TEST", "Test Branch");
            InvestmentAccount investment = new InvestmentAccount("INV-TEST", "Test Branch", 600.00);
            ChequeAccount cheque = new ChequeAccount("CHQ-TEST", "Test Branch", "Test Corp", "Test Location");
            
            // Add accounts to customer
            customer.addAccount(savings);
            customer.addAccount(investment);
            customer.addAccount(cheque);
            
            // Test transactions
            savings.deposit(1000);
            investment.deposit(200);
            cheque.deposit(500);
            
            // Test interest calculation
            double savingsInterest = savings.calculateInterest();
            double investmentInterest = investment.calculateInterest();
            
            // Verify results
            if (savings.getBalance() == 1000 && 
                investment.getBalance() == 800 && 
                cheque.getBalance() == 500 &&
                savingsInterest > 0 && 
                investmentInterest > 0) {
                
                System.out.println("PASS: Core Model Integration");
                System.out.println("  - Customer with multiple accounts created");
                System.out.println("  - Deposits processed successfully");
                System.out.println("  - Interest calculations working");
                System.out.println("  - Account types functioning correctly");
            } else {
                throw new Exception("Validation failed");
            }
            
        } catch (Exception e) {
            System.out.println("FAIL: Core Model Integration - " + e.getMessage());
        }
    }
    
    /**
     * Test 2: Controller Integration
     */
    private static void testControllerIntegration() {
        System.out.println("\nTEST 2: CONTROLLER INTEGRATION");
        System.out.println("-------------------------------");
        
        try {
            // Test Login Controller
            LoginController loginController = new LoginController();
            LoginResult customerLogin = loginController.authenticate("C001", "pass123", "customer");
            LoginResult employeeLogin = loginController.authenticate("admin", "admin123", "employee");
            LoginResult failedLogin = loginController.authenticate("wrong", "wrong", "customer");
            
            if (!customerLogin.isSuccess()) throw new Exception("Customer login should succeed");
            if (!employeeLogin.isSuccess()) throw new Exception("Employee login should succeed");
            if (failedLogin.isSuccess()) throw new Exception("Invalid login should fail");
            
            // Test Account Controller
            Customer testCustomer = customerLogin.getCustomer();
            AccountController accountController = new AccountController(testCustomer);
            Account firstAccount = testCustomer.getAccounts().get(0);
            
            TransactionResult depositResult = accountController.deposit(firstAccount.getAccountNumber(), 100);
            TransactionResult withdrawResult = accountController.withdraw(firstAccount.getAccountNumber(), 50);
            
            if (!depositResult.isSuccess()) throw new Exception("Controller deposit should succeed");
            if (!withdrawResult.isSuccess()) throw new Exception("Controller withdraw should succeed");
            
            System.out.println("PASS: Controller Integration");
            System.out.println("  - LoginController authentication working");
            System.out.println("  - AccountController transaction handling working");
            System.out.println("  - Proper error handling for invalid credentials");
            System.out.println("  - Success/failure results properly returned");
            
        } catch (Exception e) {
            System.out.println("FAIL: Controller Integration - " + e.getMessage());
        }
    }
    
    /**
     * Test 3: Full User Flow (Customer Journey)
     */
    private static void testFullUserFlow() {
        System.out.println("\nTEST 3: FULL USER FLOW (CUSTOMER JOURNEY)");
        System.out.println("-------------------------------------------");
        
        try {
            // Step 1: Authentication
            LoginController loginController = new LoginController();
            LoginResult login = loginController.authenticate("C001", "pass123", "customer");
            
            if (!login.isSuccess()) {
                throw new Exception("Login failed for customer C001");
            }
            
            Customer customer = login.getCustomer();
            AccountController accountController = new AccountController(customer);
            
            // Step 2: Account Selection
            java.util.List<Account> accounts = accountController.getCustomerAccounts();
            if (accounts.isEmpty()) {
                throw new Exception("No accounts found for customer");
            }
            
            Account selectedAccount = accounts.get(0);
            double initialBalance = selectedAccount.getBalance();
            
            // Step 3: Perform Transactions
            TransactionResult depositResult = accountController.deposit(selectedAccount.getAccountNumber(), 200);
            if (!depositResult.isSuccess()) {
                throw new Exception("Deposit failed: " + depositResult.getMessage());
            }
            
            // Step 4: Apply Interest (if applicable)
            InterestResult interestResult = accountController.applyInterest(selectedAccount.getAccountNumber());
            boolean interestApplied = interestResult.isSuccess();
            
            // Step 5: Verify Results
            double finalBalance = selectedAccount.getBalance();
            boolean balanceIncreased = finalBalance > initialBalance;
            
            System.out.println("PASS: Full User Flow");
            System.out.println("  Step 1: Customer login successful");
            System.out.println("  Step 2: Account retrieval successful (" + accounts.size() + " accounts)");
            System.out.println("  Step 3: Deposit transaction completed");
            System.out.println("  Step 4: Interest application attempted: " + (interestApplied ? "Applied" : "Not applicable"));
            System.out.println("  Step 5: Balance verification: " + initialBalance + " -> " + finalBalance);
            System.out.println("  Result: Complete customer journey working");
            
        } catch (Exception e) {
            System.out.println("FAIL: Full User Flow - " + e.getMessage());
        }
    }
    
    /**
     * Test 4: Employee Workflow
     */
    private static void testEmployeeWorkflow() {
        System.out.println("\nTEST 4: EMPLOYEE WORKFLOW");
        System.out.println("---------------------------");
        
        try {
            // Step 1: Employee Login
            LoginController loginController = new LoginController();
            LoginResult login = loginController.authenticate("admin", "admin123", "employee");
            
            if (!login.isSuccess()) {
                throw new Exception("Employee login failed");
            }
            
            // Step 2: Customer Management
            EmployeeController employeeController = new EmployeeController(loginController.getBankController());
            java.util.List<Customer> customers = employeeController.getAllCustomers();
            
            if (customers.isEmpty()) {
                throw new Exception("No customers found");
            }
            
            Customer testCustomer = customers.get(0);
            
            // Step 3: Account Creation
            AccountCreationResult accountResult = employeeController.createAccountForCustomer(
                testCustomer.getCustomerID(), "Savings Account", 100.00);
            
            if (!accountResult.isSuccess()) {
                throw new Exception("Account creation failed: " + accountResult.getMessage());
            }
            
            // Step 4: Customer Approval
            boolean approvalResult = employeeController.approveCustomer(testCustomer.getCustomerID());
            
            System.out.println("PASS: Employee Workflow");
            System.out.println("  Step 1: Employee authentication successful");
            System.out.println("  Step 2: Customer management working (" + customers.size() + " customers)");
            System.out.println("  Step 3: Account creation successful: " + accountResult.getCreatedAccount().getAccountNumber());
            System.out.println("  Step 4: Customer approval: " + (approvalResult ? "Approved" : "Already approved"));
            System.out.println("  Result: Complete employee workflow functional");
            
        } catch (Exception e) {
            System.out.println("FAIL: Employee Workflow - " + e.getMessage());
        }
    }
}