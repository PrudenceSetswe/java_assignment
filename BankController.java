import java.util.ArrayList;
import java.util.List;

public class BankController {
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    private Customer currentCustomer;
    private BankEmployee currentEmployee;

    public BankController() {
        // Initialize database FIRST
        DatabaseConnection.initializeDatabase();
        this.customerDAO = new CustomerDAO();
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }

    public boolean authenticateCustomer(String customerID, String password) {
        // Get customer FROM DATABASE
        Customer customer = customerDAO.findCustomerById(customerID);
        if (customer != null && customer.getPassword().equals(password)) {
            currentCustomer = customer;
            
            // Load customer's accounts FROM DATABASE
            List<Account> accounts = accountDAO.findAccountsByCustomer(customerID);
            for (Account account : accounts) {
                customer.addAccount(account);
            }
            
            return true;
        }
        return false;
    }

    public boolean authenticateEmployee(String username, String password) {
        // For now, keep hardcoded - you can move to database later
        if ("admin".equals(username) && "admin123".equals(password)) {
            currentEmployee = new BankEmployee("admin", "admin123");
            return true;
        }
        return false;
    }

    // Enhanced methods that use DATABASE
    public boolean deposit(String accountNumber, double amount) {
        if (currentCustomer == null) return false;
        
        Account account = currentCustomer.getAccountByNumber(accountNumber);
        if (account != null && amount > 0) {
            account.deposit(amount);
            
            // UPDATE DATABASE
            accountDAO.updateAccountBalance(accountNumber, account.getBalance());
            transactionDAO.saveTransaction(accountNumber, "DEPOSIT", amount, "Customer deposit");
            
            return true;
        }
        return false;
    }

    public boolean withdraw(String accountNumber, double amount) {
        if (currentCustomer == null) return false;
        
        Account account = currentCustomer.getAccountByNumber(accountNumber);
        if (account != null && amount > 0) {
            try {
                account.withdraw(amount);
                
                // UPDATE DATABASE
                accountDAO.updateAccountBalance(accountNumber, account.getBalance());
                transactionDAO.saveTransaction(accountNumber, "WITHDRAWAL", amount, "Customer withdrawal");
                
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    // Account creation that SAVES TO DATABASE
    public Account openAccount(String accountType, double initialDeposit, String... additionalInfo) {
        if (currentCustomer == null) {
            throw new IllegalStateException("No customer logged in");
        }

        String accountNumber = "ACC" + System.currentTimeMillis();
        String branch = "Main Branch";
        Account newAccount;

        switch (accountType.toLowerCase()) {
            case "savings":
                newAccount = new SavingsAccount(accountNumber, branch);
                if (initialDeposit > 0) {
                    newAccount.deposit(initialDeposit);
                }
                break;
            case "investment":
                newAccount = new InvestmentAccount(accountNumber, branch, initialDeposit);
                break;
            case "cheque":
                if (additionalInfo.length < 2) {
                    throw new IllegalArgumentException("Cheque account requires employer and company address");
                }
                newAccount = new ChequeAccount(accountNumber, branch, additionalInfo[0], additionalInfo[1]);
                if (initialDeposit > 0) {
                    newAccount.deposit(initialDeposit);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown account type: " + accountType);
        }

        // SAVE TO DATABASE
        accountDAO.saveAccount(newAccount, currentCustomer.getCustomerID());
        
        // Add to current customer
        currentCustomer.addAccount(newAccount);
        
        return newAccount;
    }

    // Getters
    public Customer getCurrentCustomer() { return currentCustomer; }
    public BankEmployee getCurrentEmployee() { return currentEmployee; }
    public List<Customer> getAllCustomers() { return customerDAO.getAllCustomers(); }
    
    public void logout() {
        currentCustomer = null;
        currentEmployee = null;
    }
}