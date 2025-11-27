import java.util.List;

public class EmployeeController {
    private BankController bankController;
    
    public EmployeeController(BankController bankController) {
        this.bankController = bankController;
    }
    
    public List<Customer> getAllCustomers() {
        return bankController.getAllCustomers();
    }
    
    public boolean approveCustomer(String customerId) {
        for (Customer customer : bankController.getAllCustomers()) {
            if (customer.getCustomerID().equals(customerId)) {
                customer.setStatus("Approved");
                return true;
            }
        }
        return false;
    }
    
    public AccountCreationResult createAccountForCustomer(String customerId, String accountType, 
                                                         double initialDeposit, String... additionalInfo) {
        Customer customer = findCustomerById(customerId);
        if (customer == null) {
            return new AccountCreationResult(false, "Customer not found: " + customerId, null);
        }
        
        try {
            Account newAccount = bankController.openAccount(accountType, initialDeposit, additionalInfo);
            if (newAccount != null) {
                customer.addAccount(newAccount);
                return new AccountCreationResult(true, 
                    "Account created successfully for customer " + customerId, newAccount);
            } else {
                return new AccountCreationResult(false, "Failed to create account", null);
            }
        } catch (Exception e) {
            return new AccountCreationResult(false, "Account creation failed: " + e.getMessage(), null);
        }
    }
    
    private Customer findCustomerById(String customerId) {
        for (Customer customer : bankController.getAllCustomers()) {
            if (customer.getCustomerID().equals(customerId)) {
                return customer;
            }
        }
        return null;
    }
}

class AccountCreationResult {
    private boolean success;
    private String message;
    private Account createdAccount;
    
    public AccountCreationResult(boolean success, String message, Account createdAccount) {
        this.success = success;
        this.message = message;
        this.createdAccount = createdAccount;
    }
    
    // Getters
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Account getCreatedAccount() { return createdAccount; }
}